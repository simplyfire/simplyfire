package models.feedback;

import io.ebean.Finder;
import models.BaseModel;
import models.users.Users;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class Feedback  extends BaseModel{

    @Constraints.Required
    @Constraints.MaxLength(50)
    public String theme;

    @Constraints.Required
    @Constraints.MaxLength(message = "error.contentMaxLen", value = 1000)
    @Column(length = 1000)
    public String content;

    @ManyToOne
    public Users user;

    public LocalDateTime dateCreation;

    public static final Finder<Long, Users> find = new Finder<>(Users.class);
}
