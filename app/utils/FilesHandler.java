package utils;


import models.files.RepoFile;
import models.files.VersionFile;
import models.projects.Project;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class FilesHandler {
    public static String pathRepository="repository/";

    private static boolean executeSaveFile(File file, String pathFile)
    {
        try (InputStream input=new FileInputStream(file)){
            File targetFile = new File(pathFile);
            targetFile.getParentFile().mkdirs();

            java.nio.file.Files.copy(
                    input,
                    targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveFile(File file, String projectName, String path, String fileName)
    {
        String pathFile=getPath(projectName,path,fileName);
        return executeSaveFile(file,pathFile);
    }

    public static boolean saveFile(File file, String path, String fileName)
    {
        String pathFile=String.format("%s/%s",path,fileName);
        return executeSaveFile(file,pathFile);
    }

    public static void createDir(String dirName)
    {
        new File(pathRepository+dirName).mkdirs();
    }

    public static void createDir(String projectFile,String path,String dirName)
    {
        String pathToDir=getPath(projectFile,path,dirName);
        new File(pathToDir).mkdirs();
    }

    public static String getPath(String projectFile,String path, String name)
    {
        String customPath;
        if (path.equals(""))
            customPath=String.format("%s%s/%s",pathRepository,projectFile,name);
        else
            customPath=String.format("%s%s/%s/%s",pathRepository,projectFile,path,name);
        return customPath;
    }

    public static String getPathDownload(String projectFile,String path, String name)
    {
        String customPath;
        if (path.equals(""))
            customPath=String.format("%s%s/%s",pathRepository,projectFile,name);
        else
            customPath=String.format("%s%s/%s%s",pathRepository,projectFile,path,name);
        return customPath;
    }

    public static String getPath(Project project, RepoFile file)
    {
        String customPath=String.format("%s%s/%s",pathRepository,project.fileName,file.path);
        return customPath;
    }

    public static String getFileName(Timestamp date,String fileName)
    {
        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        return String.format("%s_%s",dateFormat.format(date),fileName);
    }

    public static File downloadFile(String username,String projectName,String path) throws Exception {
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!RepoFile.isRepoFileExist(username,projectName,path))
            throw new Exception("File not found");
        String projectFileName=Project.getProjectFileName(username,projectName);
        RepoFile repoFile=RepoFile.getDirectory(username,projectName,path);
        VersionFile latestVersion=VersionFile.getLatestVersionFile(repoFile);
        String pathToFile= FilesHandler.getPathDownload(projectFileName,path,latestVersion.fileName);
        return download(pathToFile,latestVersion);
    }

    public static File downloadFile(String projectFileName,RepoFile repoFile,String path)
    {
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VersionFile latestVersion=VersionFile.getLatestVersionFile(repoFile);
        String pathToFile= FilesHandler.getPathDownload(projectFileName,path,latestVersion.fileName);
        return download(pathToFile,latestVersion);
    }

    public static File downloadFile(String username,String projectName,String path,Long idVersion) throws Exception {
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        VersionFile versionFile=VersionFile.find.byId(idVersion);
        if (versionFile==null)
            throw new Exception("File not found");
//        if (!RepoFile.isRepoFileExist(username,projectName,path))
//            throw new Exception("File not found");

        String projectFileName=Project.getProjectFileName(username,projectName);
        String pathToFile= FilesHandler.getPath(projectFileName,versionFile.file.path,versionFile.fileName);
        return download(pathToFile,versionFile);
    }

    private static File download(String path,VersionFile versionFile)
    {
        File file=new File(path);
        File copyFile=file;
        try {
            try (InputStream input=new FileInputStream(file)){
                copyFile = new File(file.getParent(),versionFile.file.name);
                java.nio.file.Files.copy(
                        input,
                        copyFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyFile;
    }
}
