package models.projects;

import akka.http.javadsl.model.DateTime;
import io.ebean.Finder;
import models.BaseModel;
import models.files.RepoFile;
import models.users.Users;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.Constraint;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Project extends BaseModel {

    public String fileName;

    @Constraints.Required
    @Constraints.MaxLength(50)
    public String name;

    @ManyToOne
    public Users owner;

    @OneToMany
    public List<RepoFile> files;

    @OneToMany
    public List<ProjectMembers> projectMembers;

    @Column(name = "date")
    public Timestamp date;

    public static final Finder<Long, Project> find = new Finder<>(Project.class);

    public static Project getProject(String username,String projectname)
    {
//        Project project=Project.find.query()
//                .fetch("projectMembers")
//                .fetch("projectMembers.user")
//                .where()
//                .eq("projectMembers.user.username", username)
//                .eq("name", projectname)
//                .findOne();

        try {
            projectname = URLDecoder.decode(projectname, "UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Project project=Project.find.query()
                .where()
                .eq("fileName", username + "-" + projectname)
                .findOne();
        return project;
    }

    public static Project createProject(String username, String name)
    {
        ProjectMembers member = new ProjectMembers();
        Users user = Users.findByUsername(username);
        Project project = new Project();
        project.name=name;
        project.fileName = user.username + "-" + project.name;
        project.date=new Timestamp(System.currentTimeMillis());
        project.owner=user;
        project.save();
        member.user = user;
        member.permissions = Permissions.find.query().where()
                .ieq("name","Owner")
                .findOne();
        member.project = project;
        member.save();
        return project;
    }

    public static Boolean isProjectExist(String owner, String name) {
        String fileName = owner + "-" + name;
        Project projectExisted = Project.find.query().where()
                .ieq("fileName", fileName)
                .findOne();
        return projectExisted != null;
    }

    public static String getProjectFileName(String username, String projectName)
    {
        return String.format("%s-%s",username,projectName);
    }

    public static List<Project> getProjectsByUser(String sessionEmail)
    {
        Users user=Users.findByEmail(sessionEmail);
        return user.participation.stream().map(x->x.project).collect(Collectors.toList());
    }

    public static Permissions getPermission(Users user, Project project) {
        for (ProjectMembers member : project.projectMembers) {
            if(member.user.id == user.id){
                return member.permissions;
            }
        }
        return null;
    }
}
