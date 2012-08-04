import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

import play.db.jpa.JPA;

public class BasicTest extends UnitTest {

    @Before
    public void setUp() {
//        Fixtures.deleteAll();
//        Fixtures.loadModels("data.yml");
    }

  //  @Test
    public void aVeryImportantThingToTest() {
        UrlAlias alias = new UrlAlias();

        alias.tiny = "g";
        alias.target = "http://www.google.com";
        alias.creatorUsername = "phil";
        alias.isCustomAlias = true;
        alias.save();

        assertTrue(UrlAlias.count() > 0);

        UrlAlias aliasExists = UrlAlias.find("byTiny", "google").first();

        assertNotNull(aliasExists);
        assertEquals("phil", aliasExists.creatorUsername);
    }

    @Test
    public void aSqlQueryTest() {

        // generate list of dates and turn into OR? or use Criteria?
        Iterator results = JPA.em().createQuery("select visit.date, count(visit) from Visit visit where visit.date > CURRENT_DATE - 7 group by day(visit.date)").getResultList().iterator();

        assertNotNull(results);
        //assertTrue(results.hasNext());


        while ( results.hasNext() ) {
            Object[] row = (Object[])results.next();
            Date date = (Date) row[0];
            Long clicks = (Long) row[1];
        }


    }

}
