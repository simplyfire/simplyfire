package models.projects;

import io.ebean.Finder;
import models.BaseModel;
import models.users.Users;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ProjectMembers extends BaseModel {
    @ManyToOne
    public Users user;

    @ManyToOne
    public Project project;

    @ManyToOne
    public Permissions permissions;

    public static final Finder<Long, ProjectMembers> find = new Finder<>(ProjectMembers.class);
}
