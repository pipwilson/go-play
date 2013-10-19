package controllers;

/* start imports for QR code generation */
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.io.UnsupportedEncodingException;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
/* end imports for QR code generation */

import java.io.IOException;
import java.util.*;
import javax.persistence.Query;

import org.apache.commons.collections.keyvalue.DefaultKeyValue;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import play.*;
import play.db.jpa.JPA;
import play.mvc.*;
import play.data.validation.*;

import controllers.modules.cas.*;

import jobs.*;
import models.*;

@With(SecureCAS.class)
public class Application extends Controller {

    public static void list() {
        List<UrlAlias> aliases = UrlAlias.findAll();
        render(aliases);
    }

    /*
     * when we come to modify an existing tiny:
     * UrlAlias existingAlias = UrlAlias.find("byTiny", tiny).first();
     *
     * if it already exists then only the owning user is allowed to override it
     * if(existingAlias != null && existingAlias.creatorUsername.equalsIgnoreCase(alias.creatorUsername)) {
     */
    public static void create(@Valid UrlAlias alias) {
        String username = controllers.modules.cas.Security.connected().toString();
        validation.required(username);

        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            list();
        } else {
            Logger.debug(alias.toString());
            alias.created = new Date();
            alias.creatorUsername = username;
            alias.save();

        }

        list();
    }

    public static void delete(@Required String tiny) {
        UrlAlias alias = UrlAlias.find("byTiny", tiny).first();
        alias.delete();
        list();
    }

    public static void go(@Required String tiny) {
        UrlAlias alias = UrlAlias.find("byTiny", tiny).first();        
        if(alias!=null && alias.target!=null) {
            new VisitRecorderJob(alias, request).now();
            redirect(alias.target);
        } else {
            list();
        }
    }




    public static void stats(@Required String tiny) {
        UrlAlias alias = UrlAlias.find("byTiny", tiny).first();

        long totalVisits = Visit.count("tinyUrl = ?", alias);

        // construct an iterator over the last 7 days
        Date startDate = DateUtils.addDays(new Date(), -7);
        Iterator weekdates = DateUtils.iterator(startDate, DateUtils.RANGE_WEEK_RELATIVE);

        //ArrayList dateclicks = new ArrayList();
        HashMap dateclicks = new HashMap<Date, Long>(7);

        Iterator visits = JPA.em().createQuery("select date, count(*) from Visit v where v.tinyUrl = :alias and day(v.date) > day(current_date()) -7 and year(v.date) = year(current_date()) group by v.date order by v.date asc").setParameter("alias", alias).getResultList().iterator();

        // loop over all visits to this tiny url and put them into a hashamp thus:
        // date : number of visits
        // the hashmap could be any size from 0 to 7
        while ( visits.hasNext() ) {
            Object[] row = (Object[])visits.next();
            Date date = (Date) row[0];
            Long clicks = (Long) row[1];
            dateclicks.put(date, clicks);
            //DefaultKeyValue dateclick = new DefaultKeyValue(date, clicks);
            //dateclicks.add(dateclick);
        }

        // loop over weekdates, getting an entry from dateclicks where one exists
        while(weekdates.hasNext()) {
            Calendar day = (java.util.Calendar)weekdates.next();
            //System.out.println(day.toString());


            //if (dateclicks.containsKey(day.getDate())) {
                // get the value and a
            //}

        }



        render(alias, totalVisits, dateclicks);
    }


    public static void qr(@Required String tiny) {
        // if we render qr codes to disk then they will be faster but we need
        // to ensure they are regenerated on update.
        // easier to do them dynamically on request.
        // http://www.copperykeenclaws.com/how-to-create-qr-codes-in-java/
        // http://stackoverflow.com/questions/4127876/send-generated-image-to-browser-using-play-framework

        String tmpDir = Play.tmpDir.toString();
        if (!tmpDir.endsWith(System.getProperty("file.separator"))) {
            tmpDir = tmpDir + System.getProperty("file.separator");
        }

        String filePath = tmpDir + tiny + ".png";
        Logger.debug("filepath: "+filePath);
        File file = new File(filePath);

        // force into ISO-8859-1 since some readers fail horribly with UTF-8 strings
        Charset charset = Charset.forName("ISO-8859-1");
        CharsetEncoder encoder = charset.newEncoder();
        byte[] b = null;
        try {
            // Convert a string to ISO-8859-1 bytes in a ByteBuffer
            ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(tiny));
            b = bbuf.array();
        } catch (CharacterCodingException e) {
            System.out.println(e.getMessage());
        }

        if (b !=null ) {
            String data;
            try {
                data = new String(b, "ISO-8859-1");

                // get a byte matrix for the data
                BitMatrix matrix = null;
                int h = 100;
                int w = 100;
                com.google.zxing.Writer writer = new com.google.zxing.qrcode.QRCodeWriter();

                matrix = writer.encode(data, com.google.zxing.BarcodeFormat.QR_CODE, w, h);

                MatrixToImageWriter.writeToFile(matrix, "PNG", file);
            } catch (UnsupportedEncodingException e) {
                System.out.println(e.getMessage());
            } catch (com.google.zxing.WriterException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        renderBinary(file);
    }

    public static void tinyByUser(@Required String creatorUsername) {
        List<UrlAlias> aliases = UrlAlias.find("creatorUsername", creatorUsername).fetch();
        render(aliases);
    }

}