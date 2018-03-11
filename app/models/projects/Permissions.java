package models.projects;

import io.ebean.Finder;
import models.BaseModel;
import play.data.validation.Constraints;

import javax.persistence.Entity;

@Entity
public class Permissions extends BaseModel {

    @Constraints.Required
    public String name;

    public boolean toCreate;

    public boolean toRead;

    public boolean toUpdate;

    public boolean toDelete;

    public boolean isOwner;

    public static final Finder<Long, Permissions> find = new Finder<>(Permissions.class);
}
