package models.files;

import io.ebean.Finder;
import models.BaseModel;
import models.projects.Project;
import utils.FilesHandler;

import javax.persistence.*;
import java.io.File;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class RepoFile extends BaseModel {

    @ManyToOne
    public TypeFile typeFile;

    @ManyToOne
    public Project project;

    @Column(length = 1000)
    public String path;
    public String name;

    @ManyToOne
    public RepoFile directory;

    @Column(name = "date")
    public Timestamp date;

    public static final Finder<Long, RepoFile> find = new Finder<>(RepoFile.class);

    public static List<RepoFile> getFilesFromDir(String username, String projectName, String path) throws Exception {
        if (!isRepoFileExist(username,projectName,path))
            throw new Exception("File not found");
        Project project = Project.getProject(username,projectName);
        String[] paths=path.split("/");
        if (path.equals(""))
        {
            List<RepoFile> files=RepoFile.find.query().where()
                    .eq("project",project)
                    .eq("directory",null)
                    .orderBy("typeFile desc")
                    .findList();
            return files;
        }
        RepoFile chosenDirectory= getDirectory(project,path);
        List<RepoFile> files=RepoFile.find.query().where()
                .eq("project",project)
                .eq("directory",chosenDirectory)
                .orderBy("typeFile desc")
                .findList();
        return files;
    }

    public static boolean isRepoFileExist(String username, String projectName, String directoryName, String path)
    {
        Project project = Project.getProject(username,projectName);
        RepoFile upperDirectory= getDirectory(project,path);
        RepoFile existingDirectory=RepoFile.find.query().where()
                .eq("project",project)
                .eq("directory",upperDirectory)
                .ieq("name",directoryName)
                .findOne();
        return existingDirectory!=null;
    }

    public static boolean isRepoFileExist(String username,String projectName,String path)
    {
        Project project = Project.getProject(username,projectName);
        if (path.equals(""))
            return true;
        RepoFile existingDirectory=RepoFile.find.query().where()
                .eq("project",project)
                .ieq("path",path)
                .findOne();
        return existingDirectory!=null;
    }

    public static RepoFile createDirectory(String username, String projectName, String directoryName, String path) throws Exception {
        try {
            path = URLDecoder.decode(path, "UTF-8");
            directoryName=URLDecoder.decode(directoryName, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (!isRepoFileExist(username,projectName,path))
            throw new Exception("Path not found");
        Project project = Project.getProject(username,projectName);
        if (isRepoFileExist(username,projectName,directoryName,path))
            return null;
        RepoFile upperDirectory= getDirectory(project,path);
        RepoFile newDirectory=new RepoFile();
        newDirectory.date=new Timestamp(System.currentTimeMillis());
        newDirectory.directory=upperDirectory;
        newDirectory.project=project;
        newDirectory.name=directoryName;
        newDirectory.typeFile=TypeFile.getType("Directory");
        newDirectory.path=String.format("%s%s/",path,directoryName);
        newDirectory.save();
        FilesHandler.createDir(project.fileName,path,directoryName);
        return newDirectory;
    }

    public static RepoFile createFile(String username, String projectName, String fileName, String path,File file) throws Exception {
        try {
            path = URLDecoder.decode(path, "UTF-8");
            fileName = URLDecoder.decode(fileName, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (!isRepoFileExist(username,projectName,path))
            throw new Exception("File not found");

        Project project = Project.getProject(username,projectName);
        if (isRepoFileExist(username,projectName,fileName,path))
            return null;
        RepoFile newFile=new RepoFile();
        RepoFile upperDirectory= getDirectory(project,path);
        newFile.date=new Timestamp(System.currentTimeMillis());
        newFile.directory=upperDirectory;
        newFile.project=project;
        newFile.name=fileName;
        newFile.typeFile=TypeFile.getType("File");
        newFile.path=String.format("%s%s/",path,fileName);
        newFile.save();

        VersionFile versionFile=new VersionFile();
        versionFile.date=new Timestamp(System.currentTimeMillis());
        versionFile.file=newFile;
        versionFile.fileName= FilesHandler.getFileName(versionFile.date,fileName);
        versionFile.save();
        FilesHandler.createDir(project.fileName,path,fileName);
        String fileNameWithDate=FilesHandler.getFileName(newFile.date,fileName);
        String pathToDir=String.format("%s%s",path,fileName);
        FilesHandler.saveFile(file,project.fileName,pathToDir,fileNameWithDate);
        return newFile;
    }

    public static RepoFile getDirectory(Project project, String path)
    {
        try {
            path = URLDecoder.decode(path, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        RepoFile directory=RepoFile.find.query().where()
                .eq("project",project)
                .eq("path",path)
                .findOne();
        return directory;
    }

    public static RepoFile getDirectory(String username, String projectname, String path)
    {
        try {
            path = URLDecoder.decode(path, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Project project = Project.getProject(username,projectname);
        RepoFile directory=RepoFile.find.query().where()
                .eq("project",project)
                .eq("path",path)
                .findOne();
        return directory;
    }

    public static RepoFile getUpperDirectory(String username, String projectname, String path) throws Exception {
        try {
            path = URLDecoder.decode(path, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (!isRepoFileExist(username,projectname,path))
            throw new Exception("File not found");

        Project project = Project.getProject(username,projectname);
        RepoFile upper= RepoFile.getDirectory(project,path);
        upper=upper.directory;
        return upper;
    }

    public static void delete(String username,String projectName, String path) throws Exception {
        try {
            path = URLDecoder.decode(path, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (!isRepoFileExist(username,projectName,path))
            throw new Exception("File not found");

        Project project = Project.getProject(username,projectName);
        RepoFile chosenFile=RepoFile.find.query().where()
                .eq("project",project)
                .eq("path",path)
                .findOne();
        if (chosenFile.typeFile.typeName.equals("File"))
            deleteFile(username,project,path);
        if (chosenFile.typeFile.typeName.equals("Directory"))
            deleteDirectory(username,project,chosenFile);
    }

    private static void deleteFile(String username,Project project, String path)
    {
        List<VersionFile> versions=VersionFile.find.query()
                .fetch("file")
                .fetch("file.project")
                .fetch("file.project.projectMembers")
                .fetch("file.project.projectMembers.user")
                .where()
                .eq("file.project.projectMembers.user.username",username)
                .eq("file.project.name",project.name)
                .eq("file.path",path)
                .orderBy("id")
                .findList();
        VersionFile oldestVersion=versions.get(versions.size()-1);
        String pathToFile=String.format("%s%s/%s",FilesHandler.pathRepository,project.fileName,path);
        File file=new File(pathToFile);

        //Удаление всех версий
        VersionFile deleteVersion=oldestVersion;
        RepoFile repoFile = deleteVersion.file;
        while (deleteVersion!=null) {
            VersionFile temp=deleteVersion.oldVersion;
            VersionFile.deleteVersion(deleteVersion);
            deleteVersion=temp;
        }
        boolean del=file.delete();
        repoFile.delete();
    }

    private static void deleteDirectory(String username,Project project,RepoFile chosenDirectory)
    {
        String pathToFile=String.format("%s%s/%s",FilesHandler.pathRepository,chosenDirectory.project.fileName,chosenDirectory.path);
        File file=new File(pathToFile);
        deleteRecursiveFiles(username,project,chosenDirectory);
        file.delete();
        chosenDirectory.delete();
    }

    private static void deleteRecursiveFiles(String username,Project project,RepoFile directory) {
        List<RepoFile> files = RepoFile.find.query().where()
                .eq("project", project)
                .eq("directory", directory)
                .findList();
        for (RepoFile curFile : files) {
            if (curFile.typeFile.typeName.equals("File"))
                deleteFile(username, project, curFile.path);
            if (curFile.typeFile.typeName.equals("Directory"))
                deleteDirectory(username, project, curFile);
        }
    }

    public static List<RepoFile> getPathList(String username, String projectName, String path) throws Exception {
        try {
            path = URLDecoder.decode(path, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (!isRepoFileExist(username,projectName,path))
            throw new Exception("File not found");
        Project project=Project.getProject(username,projectName);
        RepoFile directory=getDirectory(project,path);
        List<RepoFile> pathList=new ArrayList<>();
        while (directory!=null)
        {
            pathList.add(directory);
            directory=directory.directory;
        }
        Collections.reverse(pathList);
        return pathList;
    }
}
