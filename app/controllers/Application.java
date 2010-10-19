package controllers;

import java.util.*;

import play.*;
import play.mvc.*;
import play.data.validation.*;

import controllers.modules.cas.*;

import models.*;

@With(SecureCAS.class)
public class Application extends Controller {

    public static void index() {
        List<UrlAlias> aliases = UrlAlias.findAll();
        render(aliases);
    }

    public static void create(@Valid UrlAlias alias) {
        if (validation.hasErrors()) {
            validation.keep();
        } else {
            alias.save();
        }

        index();
    }

    public static void go(@Required String tiny) {
        UrlAlias alias = UrlAlias.find("byTiny", tiny).first();
        if(alias!=null && alias.target!=null) {
            redirect(alias.target);
        } else {
            index();
        }
    }

}