package models;

import java.util.Date;
import javax.persistence.*;

import play.data.validation.*;
import play.db.jpa.*;
import play.Play;

@Entity
public class Visit extends Model {

    @ManyToOne
    public UrlAlias tinyUrl; // retrievable by List<Visit> visits = Visit.findBy("tinyUrl", alias).fetch();
    public Date date;
    public String ip;
    public String referrer;
    public String userAgent;
    public String location; // 2-letter country code

    public Visit(UrlAlias url) {
        tinyUrl = url;
    }

}