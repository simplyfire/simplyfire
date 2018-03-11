package models.users;

import io.ebean.Finder;
import jdk.nashorn.internal.ir.annotations.Ignore;
import jdk.nashorn.internal.parser.Token;
import models.BaseModel;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;
import java.util.UUID;

@Entity
public class AuthToken  extends BaseModel{

    @ManyToOne
    public Users user;
    @Constraints.Required
    public String token;

    public static final Finder<Long, AuthToken> find = new Finder<>(AuthToken.class);

    public static AuthToken getToken (String token){
        return find.query().where()
                .eq("token", token)
                .findOne();
    }

    public static AuthToken createToken(String email){
        Users user = Users.findByEmail(email);
        if(user.authToken.size() == 0){
            AuthToken authToken = new AuthToken();
            authToken.user = user;
            authToken.token = UUID.randomUUID().toString();
            authToken.save();
            user.authToken.add(authToken);
        }
        return user.authToken.get(0);
    }
}
