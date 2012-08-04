package controllers;

import java.io.IOException;
import java.util.*;
import javax.persistence.Query;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import net.sf.uadetector.UADetectorServiceFactory;
import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentStringParser;

import com.maxmind.geoip.*;

import play.*;
import play.db.jpa.JPA;
import play.mvc.*;
import play.data.validation.*;

import controllers.modules.cas.*;

import models.*;

//@With(SecureCAS.class)
public class Application extends Controller {

    public static void list() {
        List<UrlAlias> aliases = UrlAlias.findAll();
        render(aliases);
    }

    /*
    when we come to modify an existing tiny:
    UrlAlias existingAlias = UrlAlias.find("byTiny", tiny).first();

    // if it already exists then only the owning user is allowed to override it
    if(existingAlias != null && existingAlias.creatorUsername.equalsIgnoreCase(alias.creatorUsername)) {
    */

    public static void create(@Valid UrlAlias alias) {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
        } else {
            alias.created = new Date();
            alias.creatorUsername = "anonymous";// todo: replace with auth
            alias.save();
        }

        render(alias);
    }

    public static void delete(@Required String tiny) {
        UrlAlias alias = UrlAlias.find("byTiny", tiny).first();
        alias.delete();
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


        // construct an iterator over the last 7 days
        Date startDate = DateUtils.addDays(new Date(), -7);
        Iterator weekdates = DateUtils.iterator(startDate, DateUtils.RANGE_WEEK_RELATIVE);

        //ArrayList dateclicks = new ArrayList();
        HashMap dateclicks = new HashMap<Date, Long>(7);

        Iterator visits = JPA.em().createQuery("select v.date, count(v) from Visit v where day(v.date) > day(current_date()) -7 and year(v.date) = year(current_date()) group by day(v.date) order by v.date asc").getResultList().iterator();

        while ( visits.hasNext() ) {
            Object[] row = (Object[])visits.next();
            Date date = (Date) row[0];
            Long clicks = (Long) row[1];
            dateclicks.put(date, clicks);
            //DefaultKeyValue dateclick = new DefaultKeyValue(date, clicks);
            //dateclicks.add(dateclick);
        }

        while(weekdates.hasNext()) {
            Calendar day = (java.util.Calendar)weekdates.next();
            System.out.println(day.toString());
            // loop over weekdates, getting an entry from dateclicks where one exists

            if (dateclicks.get()!=null) {

            }

        }



        render(alias, dateclicks);
    }

    public static void tinyByUser(@Required String creatorUsername) {
        List<UrlAlias> aliases = UrlAlias.find("creatorUsername", creatorUsername).fetch();
        render(aliases);
    }

}