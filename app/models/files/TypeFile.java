package models.files;

import io.ebean.Finder;
import models.BaseModel;
import play.data.validation.Constraints;

import javax.persistence.Entity;

@Entity
public class TypeFile extends BaseModel {
    @Constraints.Required
    public String typeName;

    public static final Finder<Long, TypeFile> find = new Finder<>(TypeFile.class);

    public static TypeFile getType(String typeName)
    {
        TypeFile type=TypeFile.find.query().where()
                .eq("typeName",typeName)
                .findOne();
        return type;
    }

    public static boolean isDirectory(RepoFile file)
    {
        return file.typeFile.typeName.equals("Directory");
    }
}
