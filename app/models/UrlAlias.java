package models;

import java.util.ArrayList;
import java.util.Date;
import javax.persistence.*;

import play.data.validation.*;
import play.db.jpa.*;
import play.Play;

// this entity will be renamed post DB-refactoring. TODO.

@Entity
public class UrlAlias extends Model {

    @CheckWith(BlacklistedWordsCheck.class) public String tiny;
    @Required public String target;
    public String creatorUsername;
    public boolean isCustomAlias = false;
    public Date created;

    public UrlAlias(String tiny, String target, String creatorUsername, boolean isCustomAlias) {
        this.tiny = tiny;
        this.target = target;
        this.creatorUsername = creatorUsername;
        this.isCustomAlias = isCustomAlias;
    }

    public String toString() {
        return tiny;
    }

    static class BlacklistedWordsCheck extends Check {

        public boolean isSatisfied(Object object, Object value) {
            boolean blacklisted = false;
            String blacklist = Play.configuration.getProperty("word.blacklist");
            System.out.println(blacklist);
            String[] blacklistedWords = blacklist.split(",");
            for(String blacklistedWord : blacklistedWords) {
                System.out.println(blacklistedWord);
                if (value.toString().equalsIgnoreCase(blacklistedWord)) {
                    blacklisted = true;
                    break;
                }
            }
            return !blacklisted;
        }
    }

}
