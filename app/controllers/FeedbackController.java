package controllers;

import models.feedback.Feedback;
import models.users.Users;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.feedback.*;

import javax.inject.Inject;
import java.time.LocalDateTime;

@Security.Authenticated(Secured.class)
public class FeedbackController extends Controller {

    @Inject
    FormFactory formFactory;

    public Result create() {
        Form<Feedback> feedbackForm = formFactory.form(Feedback.class);
        return ok(create.render(feedbackForm));
    }

    public Result save() {
        Form<Feedback> feedbackForm = formFactory.form(Feedback.class).bindFromRequest();
        if(feedbackForm.hasErrors()){
            return badRequest(create.render(feedbackForm));
        }
        Feedback feedback = feedbackForm.get();
        feedback.user = Users.getCurrentUser(Http.Context.current());
        feedback.dateCreation = LocalDateTime.now();
        feedback.save();
        flash("success", "Successfully sent");
        return redirect(routes.FeedbackController.create());
    }

}
