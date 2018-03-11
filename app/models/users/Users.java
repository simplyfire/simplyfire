package models.users;

import io.ebean.Finder;
import io.ebean.annotation.CreatedTimestamp;
import jdk.nashorn.internal.ir.annotations.Ignore;
import models.BaseModel;
import models.feedback.Feedback;
import models.projects.ProjectMembers;
import org.mindrot.jbcrypt.BCrypt;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.mvc.Http;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Users extends BaseModel {

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String username;

    @Constraints.Required
    @Formats.NonEmpty
    @Column(unique = true)
    public String email;

    @Constraints.Required
    @Formats.NonEmpty
    public String passwordHash;

    public String confirmationToken;

    @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime dateCreation;

    @Formats.NonEmpty
    public Boolean validated = false;

    @ManyToOne
    public UserGroup userGroup;


    @OneToMany
    @JoinColumn(name="user_id")
    public List<Feedback> feedbacks;

    @OneToMany
    @JoinColumn(name="user_id")
    public List<AuthToken> authToken;

    @OneToMany
    @JoinColumn(name="user_id")
    public List<ProjectMembers> participation;

    public static final Finder<Long, Users> find = new Finder<>(Users.class);

    /**
     * Retrieve a user from a fullname.
     *
     * @param username User name
     * @return a user
     */
    public static Users findByUsername(String username) {
        return find.query().where().ieq("username", username).findOne();
    }

    /**
     * Retrieve a user from an email.
     *
     * @param email email to search
     * @return a user
     */
    public static Users findByEmail(String email) {
        return find.query().where().ieq("email", email).findOne();
    }

    public static Users getCurrentUser(Http.Context ctx){
        return findByEmail(ctx.session().get("email"));
    }

    public static boolean authenticate(String email, String password){
        Users users = Users.findByEmail(email);
        if(users != null){
            boolean b =BCrypt.checkpw(password, users.passwordHash);
            return BCrypt.checkpw(password, users.passwordHash);
        }else{
            return false;
        }
    }

}
