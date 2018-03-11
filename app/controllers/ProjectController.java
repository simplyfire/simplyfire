package controllers;

import models.files.RepoFile;
import models.projects.Permissions;
import models.projects.Project;
import models.projects.ProjectMembers;
import models.users.Users;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import utils.FilesHandler;
import views.html.project.*;

import javax.inject.Inject;
import java.util.List;

@Security.Authenticated(Secured.class)
public class ProjectController extends Controller {

    @Inject
    FormFactory formFactory;

    //TODO проверка на пользователя во всех методах

    public Result index() {
        List<Project> projects =Project.getProjectsByUser(session("email"));
        return ok(index.render(projects));
    }

    public Result create() {
        Form<Project> projectForm = formFactory.form(Project.class);
        return ok(create.render(projectForm));
    }

    public Result show(String username, String projectname) {
        return redirect(routes.FilesController.showDir(username, projectname, ""));
    }

    public Result save() {
        Form<Project> filledForm = formFactory.form(Project.class).bindFromRequest();
        Users owner = Users.findByEmail(session("email"));
        if (filledForm.hasErrors()) {
            return badRequest(create.render(filledForm));
        }
        Project project = filledForm.get();
        if (project.name.matches(".*/.*"))
        {
            flash("error", "Invalid character '/'");
            return badRequest(create.render(filledForm));
        }
        if (Project.isProjectExist(owner.username, project.name))
        {
            flash("error", "Project already exists");
            return badRequest(create.render(filledForm));
        }
        project=Project.createProject(owner.username, project.name);
        FilesHandler.createDir(project.fileName);
        return redirect(routes.ProjectController.index());
    }

    public Result destroy(Long id) {
        return TODO;
    }

}