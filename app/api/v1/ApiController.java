package api.v1;

import com.fasterxml.jackson.databind.node.ObjectNode;
import models.users.AuthToken;
import models.users.Users;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.ApiHandler;

import java.util.HashMap;

public class ApiController extends Controller {

    public Result index() {
        HashMap<String, String> testMap = new HashMap<>();
        String test = request().getQueryString("name");
        testMap.put("test", test);
        return ok(Json.toJson(testMap));
    }

    public Result getToken() {
        String email = request().getQueryString("email");
        String password = request().getQueryString("password");
        if (Users.authenticate(email, password)) {
            AuthToken authToken = AuthToken.createToken(email);
            ObjectNode token = Json.newObject();
            token.put("auth_token", authToken.token);
            return ok(ApiHandler.createResponseWithMessage(token));
        }else {
            return badRequest(ApiHandler.createErrorWithMessage(400, "Invalid email or password"));
        }
    }
}