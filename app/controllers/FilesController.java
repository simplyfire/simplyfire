package controllers;

import models.files.RepoFile;
import models.files.VersionFile;
import models.projects.Project;
import models.users.Users;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.FilesHandler;
import views.html.file.*;

import javax.inject.Inject;
import java.io.*;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

@Security.Authenticated(Secured.class)
public class FilesController extends Controller {

    @Inject
    FormFactory formFactory;

    public Result showDir(String username, String projectname, String path) {
        Project project = Project.getProject(username, projectname);
        if (project == null) {
            return notFound();
        }
        if (Project.getPermission(Users.getCurrentUser(Http.Context.current()), project) == null) {
            return forbidden();
        }

        try {path = URLDecoder.decode( path, "UTF-8");}catch (Exception e){}
        try {
            List<RepoFile> repoFiles = RepoFile.getFilesFromDir(username, projectname, path);
            List<RepoFile> pathList = RepoFile.getPathList(username, projectname, path);
            return ok(showDir.render(username, projectname, path, pathList, repoFiles));
        }
        catch(Exception e)
        {
            return notFound();
        }
    }

    public Result createDir(String username, String projectname, String path) {
        Project project = Project.getProject(username, projectname);
        if (project == null) {
            return notFound();
        }
        if (Project.getPermission(Users.getCurrentUser(Http.Context.current()), project) == null) {
            return forbidden();
        }

        Form<RepoFile> fileForm = formFactory.form(RepoFile.class);
        return ok(createDir.render(username, projectname, path, fileForm));
    }

    public Result saveDir(String username, String projectname, String path) {
        Project project = Project.getProject(username, projectname);
        if (project == null) {
            return notFound();
        }
        if (Project.getPermission(Users.getCurrentUser(Http.Context.current()), project) == null) {
            return forbidden();
        }

        Form<RepoFile> fileForm = formFactory.form(RepoFile.class).bindFromRequest();
        RepoFile directory = fileForm.get();
        if (directory.name.equals("")) {
            flash("error", "Missing name");
            return badRequest(createDir.render(username, projectname, path, fileForm));
        }
        if (directory.name.length() >= 50) {
            flash("error", "name: maximum length is 50");
            return badRequest(createDir.render(username, projectname, path, fileForm));
        }
        if (directory.name.matches(".*/.*")) {
            flash("error", "Invalid character '/'");
            return badRequest(createDir.render(username, projectname, path, fileForm));
        }
        String[] fileReservedWord = {"file", "directory"};
        if(Arrays.stream(fileReservedWord).anyMatch(directory.name::equalsIgnoreCase)){
            flash("error", "Folder name is reserved");
            return badRequest(createFile.render(username, projectname, path, fileForm));
        }
        try {
            if (RepoFile.createDirectory(username, projectname, directory.name, path) == null) {
                flash("error", "Folder already exist");
                return badRequest(createDir.render(username, projectname, path, fileForm));
            }
        }
        catch (Exception e)
        {
            return notFound();
        }
        return redirect(routes.FilesController.showDir(username, projectname, path));
    }

    public Result showFile(String username, String projectname, String path) {
        Project project = Project.getProject(username, projectname);
        if (project == null) {
            return notFound();
        }
        if (Project.getPermission(Users.getCurrentUser(Http.Context.current()), project) == null) {
            return forbidden();
        }
        try {path = URLDecoder.decode( path, "UTF-8");}catch (Exception e){}
        try {
            List<VersionFile> files = VersionFile.getVersionsOfFile(username, projectname, path);
            List<RepoFile> pathList = RepoFile.getPathList(username, projectname, path);
            return ok(showFile.render(username, projectname, path, pathList, files));
        }
        catch (Exception e)
        {
            return notFound();
        }
    }

    public Result createFile(String username, String projectname, String path) {
        Project project = Project.getProject(username, projectname);
        if (project == null) {
            return notFound();
        }
        if (Project.getPermission(Users.getCurrentUser(Http.Context.current()), project) == null) {
            return forbidden();
        }

        Form<RepoFile> fileForm = formFactory.form(RepoFile.class);
        return ok(createFile.render(username, projectname, path, fileForm));
    }

    public Result saveFile(String username, String projectname, String path) {
        Project project = Project.getProject(username, projectname);
        if (project == null) {
            return notFound();
        }
        if (Project.getPermission(Users.getCurrentUser(Http.Context.current()), project) == null) {
            return forbidden();
        }

        Form<RepoFile> fileForm = formFactory.form(RepoFile.class).bindFromRequest();
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> fileUploaded = body.getFile("fileUploaded");
        if (fileUploaded.getFilename().equals("")) {
            flash("error", "Missing file");
            return badRequest(createFile.render(username, projectname, path, fileForm));
        }
        if (fileUploaded.getFilename().length() >= 100) {
            flash("error", "file name: maximum length is 100");
            return badRequest(createDir.render(username, projectname, path, fileForm));
        }
        String[] fileReservedWord = {"file", "directory"};
        if (Arrays.stream(fileReservedWord).anyMatch(fileUploaded.getFilename()::equalsIgnoreCase)) {
            flash("error", "File name is reserved");
            return badRequest(createFile.render(username, projectname, path, fileForm));
        }
        File file = fileUploaded.getFile();
        try {
            if (RepoFile.createFile(username, projectname, fileUploaded.getFilename(), path, file) == null) {
                flash("error", "File already exists");
                return badRequest(createFile.render(username, projectname, path, fileForm));
            }
            return redirect(routes.FilesController.showDir(username, projectname, path));
        } catch (Exception e)
        {
            return notFound();
        }
    }


    public Result destroy(String username, String projectname, String path)
    {
        Project project = Project.getProject(username, projectname);
        if (project == null) {
            return notFound();
        }
        if (Project.getPermission(Users.getCurrentUser(Http.Context.current()), project) == null) {
            return forbidden();
        }

        try {
            RepoFile upper = RepoFile.getUpperDirectory(username, projectname, path);
            RepoFile.delete(username, projectname, path);
            if (upper == null)
                return redirect(routes.FilesController.showDir(username, projectname, ""));
            else
                return redirect(routes.FilesController.showDir(username, projectname, upper.path));
        }
        catch (Exception e)
        {
            return notFound();
        }
    }


    public Result newVersion(String username, String projectname, String path) {
        Project project = Project.getProject(username, projectname);
        if (project == null) {
            return notFound();
        }
        if (Project.getPermission(Users.getCurrentUser(Http.Context.current()), project) == null) {
            return forbidden();
        }

        Form<RepoFile> fileForm = formFactory.form(RepoFile.class);
        return ok(newVersion.render(username, projectname, path, fileForm));
    }


    public Result saveVersion(String username, String projectname, String path) {
        Project project = Project.getProject(username, projectname);
        if (project == null) {
            return notFound();
        }
        if (Project.getPermission(Users.getCurrentUser(Http.Context.current()), project) == null) {
            return forbidden();
        }

        Form<RepoFile> fileForm = formFactory.form(RepoFile.class).bindFromRequest();
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> fileUploaded = body.getFile("fileUploaded");
        if (fileUploaded.getFilename().equals("")) {
            flash("error", "Missing file");
            return badRequest(newVersion.render(username, projectname, path, fileForm));
        }
        File file = fileUploaded.getFile();
        try {
            VersionFile.createFileVersion(username, projectname, path, file);
            return redirect(routes.FilesController.showFile(username, projectname, path));
        }
        catch (Exception e)
        {
            return notFound();
        }
    }

    public Result download(String username,String projectname,Long idVersion ,String path)
    {
        Project project = Project.getProject(username, projectname);
        if (project == null) {
            return notFound();
        }
        if (Project.getPermission(Users.getCurrentUser(Http.Context.current()), project) == null) {
            return forbidden();
        }

        try {
            if (idVersion == 0) {
                return ok(FilesHandler.downloadFile(username, projectname, path), false);
            } else {
                return ok(FilesHandler.downloadFile(username, projectname, path, idVersion), false);
            }
        }
        catch (Exception e)
        {
            return notFound();
        }
    }
}
