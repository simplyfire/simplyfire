package api.v1;

import com.fasterxml.jackson.databind.JsonNode;
import io.ebean.Ebean;
import io.ebean.SqlRow;
import models.projects.Project;
import models.users.AuthToken;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ApiHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ApiProjectController extends Controller {

    public Result index()
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

        String sql = "SELECT project.id,project.name,project.file_name "
        +"from project INNER JOIN project_members on project.id = project_members.project_id INNER JOIN users on project_members.user_id = users.id "
        +"where users.username=:username "
        +"LIMIT :limit "
        +"OFFSET :offset";

        List<SqlRow> result=Ebean.createSqlQuery(sql)
                .setParameter("username", authToken.user.username)
                .setParameter("limit",limit)
                .setParameter("offset",offset)
                .findList();
        return ok(ApiHandler.createResponse(result));
    }

    public Result create()
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

        JsonNode json = request().body().asJson();
        Project projectRequest;
        try {
            projectRequest=Json.fromJson(json, Project.class);
        }
        catch(Exception e)
        {
            return badRequest(ApiHandler.createErrorWithMessage(400,"Incorrect JSON"));
        }
        if (projectRequest.name==null)
            return badRequest(ApiHandler.createErrorWithMessage(400,"Incorrect JSON"));

        Project project = Project.getProject(authToken.user.username, projectRequest.name);
        if (project!=null)
            return badRequest(ApiHandler.createAlreadyExist("project_name"));

        Project.createProject(authToken.user.username, projectRequest.name);

        return created(ApiHandler.createResponseWithMessage("Project created"));
    }
}
