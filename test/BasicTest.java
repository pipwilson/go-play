import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Test
    public void aVeryImportantThingToTest() {
        new UrlAlias("google", "http://www.google.com", "phil", true).save();

        assertTrue(UrlAlias.count() > 0);

        //UrlAlias alias = UrlAlias.all().first();
        UrlAlias alias = UrlAlias.find("byTiny", "google").first();

        assertNotNull(alias);
        assertEquals("phil", alias.creatorUsername);
    }

}
