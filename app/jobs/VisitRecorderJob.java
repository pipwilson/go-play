package jobs;
 
import play.*;
import play.db.jpa.JPA;
import play.mvc.*;
import play.jobs.*;
import play.mvc.Http.Request;

import java.io.IOException;
import java.util.*;

import net.sf.uadetector.UADetectorServiceFactory;
import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentStringParser;

import com.maxmind.geoip.*;

import models.*;

public class VisitRecorderJob extends Job {
    
	private UrlAlias alias;
	private Http.Request request;

	public VisitRecorderJob(UrlAlias alias, Http.Request request) {
		this.alias = alias;
		this.request = request;
	}

    public void doJob() {
	    Visit visit = new Visit(alias);
	    visit.date = new Date();
	    visit.ip = request.remoteAddress;
	    visit.referrer = getReferrer();
	    visit.userAgent = getUserAgent();
	    visit.location = getLocation(visit.ip); // get from IP
	    visit.tinyUrl = alias;
	    visit.save();
    }
 
    protected String getReferrer() {
        String referrer = "direct";

        if(request.headers.containsKey("referer")) {
            referrer = request.headers.get("referer").toString();
        }

        return referrer;
    }



    protected String getUserAgent() {
        String userAgent = "none";

        if(request.headers.containsKey("user-agent")) {
            userAgent = request.headers.get("user-agent").toString();
            UserAgentStringParser parser = UADetectorServiceFactory.getUserAgentStringParser();
            UserAgent agent = parser.parse(userAgent);
        }

        return userAgent;
    }


    protected String getLocation(String ip) {
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

}