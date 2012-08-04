package models;

import java.util.ArrayList;
import java.util.Date;
import javax.persistence.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.RandomStringUtils;

import play.data.validation.*;
import play.db.jpa.*;
import play.Play;

// this entity will be renamed post DB-refactoring. TODO.

@Entity
public class UrlAlias extends Model {

    @Column(unique=true) @CheckWith(BlacklistedWordsCheck.class) @Unique public String tiny;
    @Required @URL public String target;

    public String creatorUsername;
    public boolean isCustomAlias = false;
    public Date created;

    // if tiny is empty
    // tiny = RandomStringUtils.randomAlphanumeric(4);

    public String toString() {
        return tiny;
    }

    static class BlacklistedWordsCheck extends Check {

        public final static String message = "validation.blacklisted";

        public boolean isSatisfied(Object object, Object value) {
            boolean isAnAcceptableString = true;

            if (StringUtils.isEmpty(value.toString())) {
                return isAnAcceptableString;
            }

            String blacklist = Play.configuration.getProperty("word.blacklist");
            String[] blacklistedWords = blacklist.split(",");
            for(String blacklistedWord : blacklistedWords) {
                if (value.toString().equalsIgnoreCase(blacklistedWord)) {
                    isAnAcceptableString = false;
                    break;
                }
            }

            return isAnAcceptableString;
        }
    }


}
