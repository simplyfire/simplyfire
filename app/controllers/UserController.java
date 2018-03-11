package controllers;

import models.users.UserGroup;
import models.users.Users;
import org.mindrot.jbcrypt.BCrypt;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.*;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import views.html.users.*;


public class UserController extends Controller {
    @Inject
    FormFactory formFactory;

    public Result create(){
        Form<SignUpForm> signupForm = formFactory.form(SignUpForm.class);
        return ok(signup.render(signupForm));
    }

    public Result save(){
        Form<SignUpForm> signupForm = formFactory.form(SignUpForm.class).bindFromRequest();
        if(signupForm.hasErrors()){
            return badRequest(signup.render(signupForm));
        }
        SignUpForm  signup = signupForm.get();
        Users user = new Users();
        user.email = signup.email;
        user.passwordHash = BCrypt.hashpw(signup.password, BCrypt.gensalt());
        user.username = signup.username;
        user.dateCreation = LocalDateTime.now();
        user.confirmationToken = null; //todo
        user.validated = true; //todo
        user.userGroup = UserGroup.findByTypeName("User");
        user.save();
        flash().put("success", "You successfully registered");
        return redirect(routes.UserController.login());
    }

    public Result login(){
        Form<LoginForm> loginForm = formFactory.form(LoginForm.class);
        return ok(login.render(loginForm));
    }

    public Result authenticate(){
        Form<LoginForm> loginForm = formFactory.form(LoginForm.class).bindFromRequest();
        if(loginForm.hasErrors()){
            return badRequest(login.render(loginForm));
        }
        session("email", loginForm.get().email);
        Users user = Users.getCurrentUser(Http.Context.current());
        return redirect(routes.ProjectController.index());
    }

    public Result logout()
    {
        session().clear();
        return redirect(routes.UserController.login());
    }

    @Constraints.Validate
    public static class LoginForm implements Constraints.Validatable<List<ValidationError>> {

        @Constraints.Required
        @Constraints.MaxLength(50)
        @Constraints.Email
        public String email;

        @Constraints.Required
        @Constraints.MaxLength(50)
        public String password;

        @Override
        public List<ValidationError> validate() {
            List<ValidationError> errors = new ArrayList<>();
            if (!Users.authenticate(email, password)) {
                errors.add(new ValidationError("","Invalid email or password" ));
            }
            return errors;
        }
    }

    @Constraints.Validate
    public static class SignUpForm implements Constraints.Validatable<List<ValidationError>> {

        @Constraints.Required
        @Constraints.MaxLength(50)
        public String username;

        @Constraints.Required
        @Constraints.MaxLength(50)
        @Constraints.Email
        public String email;

        @Constraints.Required
        @Constraints.MaxLength(50)
        public String password;

        @Constraints.Required
        @Constraints.MaxLength(50)
        public String repeatPassword;

        @Override
        public List<ValidationError> validate() {
            String[] userReservedWord = {"login", "logout", "signup", "docs", "project", "assets", "v1"};
            List<ValidationError> errors = new ArrayList<>();
            if (username.matches(".*/.*")) {
                errors.add(new ValidationError("", "name: invalid character '/'"));
            }
            if (!password.equals(repeatPassword)) {
                errors.add(new ValidationError("", "Passwords do not match"));
            }
            if(Users.findByEmail(email) != null){
                errors.add(new ValidationError("", "User with this email already exists"));
            }
            if(Users.findByUsername(username) != null){
                errors.add(new ValidationError("", "User with this name already exists"));
            }
            if( Arrays.stream(userReservedWord).anyMatch(username::equalsIgnoreCase)){
                errors.add(new ValidationError("", "This name is reserved"));
            }
            return errors;
        }
    }
}
