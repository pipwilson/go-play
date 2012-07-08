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
public class Stats extends Controller {


}