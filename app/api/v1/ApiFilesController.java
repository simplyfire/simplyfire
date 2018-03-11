package api.v1;

import io.ebean.Ebean;
import io.ebean.SqlRow;
import models.files.RepoFile;
import models.files.TypeFile;
import models.files.VersionFile;
import models.projects.Project;
import models.users.AuthToken;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ApiHandler;
import utils.FilesHandler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.FileHandler;

public class ApiFilesController extends Controller {

    public Result index(Long projectId)
    {
        Optional<String> auth_token=request().getHeaders().get("Authorization");
        List<String> missedParams=new ArrayList<>();
        if (!auth_token.isPresent())
            missedParams.add("auth_token");
        if (missedParams.size()!=0)
            return badRequest(ApiHandler.createMissingParams(missedParams));

        AuthToken authToken = AuthToken.getToken(auth_token.get());
        if (authToken == null)
            return unauthorized(ApiHandler.createErrorWithMessage(401, "Unauthorized"));

        Project project=Project.find.byId(projectId);
        if (project==null)
            return notFound(ApiHandler.createNoResult("Project"));
        if (Project.getPermission(authToken.user, project) == null) {
            return forbidden(ApiHandler.createErrorWithMessage(403,"Access is denied"));
        }

        String path=request().getQueryString("path");
        if (path==null)
        {
            path="";
        }
        String limitString=request().getQueryString("limit");
        Integer limit;
        if (limitString!=null)
        {
            limit=Integer.parseInt(limitString);
        }
        else
        {
            limit=25;
        }

        String offsetString=request().getQueryString("offset");
        Integer offset;
        if (offsetString!=null)
        {
            offset=Integer.parseInt(offsetString);
        }
        else
        {
            offset=0;
        }
        if (path.equals(""))
        {
            String sql = "SELECT repo_file.id,repo_file.name,repo_file.path,type_file.type_name\n" +
                    "from repo_file\n" +
                    "  INNER JOIN project on repo_file.project_id = project.id\n" +
                    "  INNER JOIN project_members ON project.id = project_members.project_id\n" +
                    "  INNER JOIN users on project_members.user_id = users.id\n" +
                    "  INNER JOIN type_file ON repo_file.type_file_id = type_file.id\n" +
                    "where users.username=:username\n" +
                    "      and project.id=:projectId\n" +
                    "      and repo_file.directory_id IS NULL\n" +
                    "LIMIT :limit\n" +
                    "OFFSET :offset";

            List<SqlRow> result = Ebean.createSqlQuery(sql)
                    .setParameter("username", authToken.user.username)
                    .setParameter("projectId",projectId)
                    .setParameter("limit", limit)
                    .setParameter("offset", offset)
                    .findList();

            return ok(ApiHandler.createResponse(result));
        }
        RepoFile repoFile=RepoFile.getDirectory(project,path);
        if (repoFile==null)
            return badRequest(ApiHandler.createErrorWithMessage(404,"Path not found!"));
        if (TypeFile.isDirectory(repoFile)) {
            String sql = "SELECT repo_file.id,repo_file.name,repo_file.path,type_file.type_name\n" +
                    "from repo_file\n" +
                    "  INNER JOIN project on repo_file.project_id = project.id\n" +
                    "  INNER JOIN project_members ON project.id = project_members.project_id\n" +
                    "  INNER JOIN users on project_members.user_id = users.id\n" +
                    "  INNER JOIN type_file ON repo_file.type_file_id = type_file.id\n" +
                    "  INNER JOIN repo_file as directory ON repo_file.directory_id = directory.id\n" +
                    "where users.username=:username\n" +
                    "      and project.id=:projectId\n" +
                    "      and directory.path=:path\n" +
                    "LIMIT :limit\n" +
                    "OFFSET :offset";

            List<SqlRow> result = Ebean.createSqlQuery(sql)
                    .setParameter("username", authToken.user.username)
                    .setParameter("projectId",projectId)
                    .setParameter("path",path)
                    .setParameter("limit", limit)
                    .setParameter("offset", offset)
                    .findList();

            return ok(ApiHandler.createResponse(result));
        }
        else
        {
             return badRequest(ApiHandler.createErrorWithMessage(400,"File is not directory!"));
        }
    }

    public Result show(Long projectId,Long fileId)
    {
        Optional<String> auth_token=request().getHeaders().get("Authorization");
        List<String> missedParams=new ArrayList<>();
        if (!auth_token.isPresent())
            missedParams.add("auth_token");
        if (missedParams.size()!=0)
            return badRequest(ApiHandler.createMissingParams(missedParams));

        AuthToken authToken = AuthToken.getToken(auth_token.get());
        if (authToken == null)
            return unauthorized(ApiHandler.createErrorWithMessage(401, "unauthorized"));

        Project project= Project.find.byId(projectId);
        if (project==null)
            return badRequest(ApiHandler.createErrorWithMessage(404,"Project not found"));
        if (Project.getPermission(authToken.user, project) == null) {
            return forbidden(ApiHandler.createErrorWithMessage(403,"Access is denied"));
        }

        RepoFile file = RepoFile.find.byId(fileId);
        if (file==null)
            return badRequest(ApiHandler.createErrorWithMessage(404, "File not found"));
        if (TypeFile.isDirectory(file))
            return badRequest(ApiHandler.createErrorWithMessage(400,"File is directory!"));
        File downloadFile=FilesHandler.downloadFile(project.fileName,file,file.path);
        return ok(downloadFile);
    }

    public Result uploadNew(Long projectId) {
        Optional<String> auth_token = request().getHeaders().get("Authorization");
        String strPath = request().getQueryString("path");
        String fileName;
        File uploadedFile;
        try {
            fileName = request().body().asMultipartFormData().getFile("File").getFilename();
            uploadedFile = (File) request().body().asMultipartFormData().getFile("File").getFile();
        } catch (Exception e) {
            return badRequest(ApiHandler.createErrorWithMessage
                    (400, "Error during upload file! Check file key, it should be 'File'"));
        }

        List<String> missedParams = new ArrayList<>();
        if (!auth_token.isPresent())
            missedParams.add("auth_token");
        if (strPath == null)
            strPath="";
        if (missedParams.size() != 0)
            return badRequest(ApiHandler.createMissingParams(missedParams));

        AuthToken authToken = AuthToken.getToken(auth_token.get());
        if (authToken == null)
            return unauthorized(ApiHandler.createErrorWithMessage(401, "unauthorized"));

        Project project = Project.find.byId(projectId);
        if (project == null)
            return badRequest(ApiHandler.createErrorWithMessage(404, "Project not found"));
        if (Project.getPermission(authToken.user, project) == null) {
            return forbidden(ApiHandler.createErrorWithMessage(403,"Access is denied"));
        }

        try {
            if (RepoFile.createFile(authToken.user.username, project.name, fileName, strPath, uploadedFile) == null)
                return badRequest(ApiHandler.createAlreadyExist("File"));
        } catch (Exception e) {
            return internalServerError(ApiHandler.createErrorWithMessage(500, "Error during upload!"));
        }
        return ok(ApiHandler.createResponseWithMessage("File was uploaded!"));
    }

    public Result uploadVersion(Long projectId,Long fileId)
    {
        Optional<String> auth_token=request().getHeaders().get("Authorization");
        String fileName;
        File uploadedFile;
        try {
            fileName=request().body().asMultipartFormData().getFile("File").getFilename();
            uploadedFile= (File) request().body().asMultipartFormData().getFile("File").getFile();
        }
        catch(Exception e)
        {
            return badRequest(ApiHandler.createErrorWithMessage(400,"Error during upload file! Check file key, it should be 'File'"));
        }

        List<String> missedParams=new ArrayList<>();
        if (!auth_token.isPresent())
            missedParams.add("auth_token");
        if (missedParams.size()!=0)
            return badRequest(ApiHandler.createMissingParams(missedParams));

        AuthToken authToken = AuthToken.getToken(auth_token.get());
        if (authToken == null)
            return unauthorized(ApiHandler.createErrorWithMessage(401, "unauthorized"));

        Project project=Project.find.byId(projectId);
        if (project==null)
            return badRequest(ApiHandler.createErrorWithMessage(404,"Project not found"));
        if (Project.getPermission(authToken.user, project) == null) {
            return forbidden(ApiHandler.createErrorWithMessage(403,"Access is denied"));
        }

        RepoFile repoFile=RepoFile.find.byId(fileId);
        if (repoFile==null)
            return badRequest(ApiHandler.createErrorWithMessage(404,"File not found"));
        try {
            VersionFile.createFileVersion(authToken.user.username, project.name, repoFile.path, uploadedFile);
        }
        catch (Exception e)
        {
            return internalServerError(ApiHandler.createErrorWithMessage(500,"Error during upload!"));
        }
        return ok(ApiHandler.createResponseWithMessage("File was uploaded!"));
    }
}
