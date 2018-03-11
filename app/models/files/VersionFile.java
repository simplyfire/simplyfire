package models.files;

import akka.http.javadsl.model.DateTime;
import io.ebean.Finder;
import io.ebean.annotation.NotNull;
import models.BaseModel;
import models.projects.Project;
import play.data.validation.Constraints;
import utils.FilesHandler;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.File;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@Entity
public class VersionFile extends BaseModel {

    @ManyToOne
    @NotNull
    public RepoFile file;

    @ManyToOne
    public VersionFile oldVersion;

    @Constraints.Required
    public String fileName;

    @Column(name = "date")
    public Timestamp date;

    public static final Finder<Long, VersionFile> find = new Finder<>(VersionFile.class);

    public static VersionFile getLatestVersionFile(RepoFile file) {
        List<VersionFile> versions = VersionFile.find.query().where()
                .eq("file", file)
                .orderBy("date")
                .findList();
        return versions.get(versions.size() - 1);
    }

    public static VersionFile createFileVersion(String username, String projectname, String path, File file) throws Exception {
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!RepoFile.isRepoFileExist(username, projectname, path))
            throw new Exception("File not found");
        RepoFile mainFile = RepoFile.getDirectory(username, projectname, path);
        VersionFile versionFile = new VersionFile();
        versionFile.date = new Timestamp(System.currentTimeMillis());
        versionFile.file = mainFile;
        versionFile.fileName = FilesHandler.getFileName(versionFile.date, mainFile.name);
        versionFile.oldVersion = VersionFile.getLatestVersionFile(mainFile);
        versionFile.save();
        mainFile.date = versionFile.date;
        mainFile.save();
        FilesHandler.saveFile(file, Project.getProjectFileName(username, projectname), path, versionFile.fileName);
        return versionFile;
    }

    public static List<VersionFile> getVersionsOfFile(String username, String projectname, String path) throws Exception {
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!RepoFile.isRepoFileExist(username, projectname, path))
            throw new Exception("File not found");
        RepoFile mainFile = RepoFile.getDirectory(username, projectname, path);
        List<VersionFile> versions = VersionFile.find.query().where()
                .eq("file", mainFile)
                .orderBy("date desc")
                .findList();
        return versions;
    }

    public static void deleteVersion(VersionFile versionFile) {
        String pathToFile = String.format("%s%s/%s%s", FilesHandler.pathRepository,
                versionFile.file.project.fileName,
                versionFile.file.path,
                versionFile.fileName);
        File file = new File(pathToFile);
        boolean del = file.delete();
        versionFile.delete();
    }
}
