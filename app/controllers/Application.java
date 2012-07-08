package controllers;

import java.io.IOException;
import java.util.*;

import net.sf.uadetector.UADetectorServiceFactory;
import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentStringParser;

import com.maxmind.geoip.*;

import play.*;
import play.mvc.*;
import play.data.validation.*;

import controllers.modules.cas.*;

import models.*;

@With(SecureCAS.class)
public class Application extends Controller {

    public static void list() {
        List<UrlAlias> aliases = UrlAlias.findAll();
        render(aliases);
    }

    public static void create(@Valid UrlAlias alias) {
        if (validation.hasErrors()) {
            validation.keep();
        } else {
            alias.created = new Date();
            alias.creatorUsername = "anonymous";
            alias.save();
        }

        list();
    }

    public static void go(@Required String tiny) {
        UrlAlias alias = UrlAlias.find("byTiny", tiny).first();
        if(alias!=null && alias.target!=null) {
            Visit visit = new Visit(alias);
            visit.date = new Date();
            visit.ip = request.remoteAddress;
            visit.referrer = getReferrer();
            visit.userAgent = getUserAgent();
            visit.location = getLocation(visit.ip); // get from IP
            visit.save();

            redirect(alias.target);
        } else {
            list();
        }
    }


    protected static String getReferrer() {
        String referrer = "direct";
        System.out.println(request.headers);

        if(request.headers.containsKey("referer")) {
            referrer = request.headers.get("referer").toString();
        }

        return referrer;
    }



    protected static String getUserAgent() {
        String userAgent = "none";

        if(request.headers.containsKey("user-agent")) {
            userAgent = request.headers.get("user-agent").toString();
            UserAgentStringParser parser = UADetectorServiceFactory.getUserAgentStringParser();
            UserAgent agent = parser.parse(userAgent);
            //System.out.println(agent.getName());
            //System.out.println(agent.getOperatingSystem().getName());

        }

        return userAgent;
    }


    protected static String getLocation(String ip) {
        String location = "none";

        if (!ip.equals("127.0.0.1")) {
            try {
                String geoipDb = Play.configuration.getProperty("geoip.path");
                LookupService cl = new LookupService(geoipDb, LookupService.GEOIP_MEMORY_CACHE);
                location = cl.getCountry(ip).getName();

            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        }

        return location;
    }

    public static void stats(@Required String tiny) {
        UrlAlias alias = UrlAlias.find("byTiny", tiny).first();
        List<Visit> visits = Visit.find("tinyUrl", alias).fetch();

        render(alias, visits);
    }

    public static void tinyByUser(@Required String creatorUsername) {
        List<UrlAlias> aliases = UrlAlias.find("creatorUsername", creatorUsername).fetch();
        render(aliases);
    }

}