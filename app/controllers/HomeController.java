package controllers;

import play.api.i18n.Messages;
import play.mvc.*;
import utils.DocsHandler;
import views.html.home.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    public Result index() {
        String test = request().getQueryString("name");
        return ok(index.render());
    }
}
