package models.users;

import io.ebean.Finder;
import models.BaseModel;
import play.data.validation.Constraints;

import javax.persistence.Entity;

@Entity
public class UserGroup extends BaseModel {

    @Constraints.Required
    String typeName;

    public static final Finder<Long, UserGroup> find = new Finder<>(UserGroup.class);

    public static UserGroup findByTypeName(String type){
        return find.query().where().eq("typeName", type).findOne();
    }
}
