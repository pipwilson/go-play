package models;

import javax.persistence.*;

import play.data.validation.*;
import play.db.jpa.*;


@Entity
public class UrlAlias extends Model {

    public String tiny;
    @Required public String target;
    public String creatorUsername;
    public boolean isCustomAlias = false;

    public UrlAlias(String tiny, String target, String creatorUsername, boolean isCustomAlias) {
        this.tiny = tiny;
        this.target = target;
        this.creatorUsername = creatorUsername;
        this.isCustomAlias = isCustomAlias;
    }

}
