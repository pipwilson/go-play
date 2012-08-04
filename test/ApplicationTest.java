import org.junit.*;
import play.Play;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class ApplicationTest extends FunctionalTest {

    @org.junit.Before
    public void setUp() {
//        Fixtures.deleteAll();
//        Fixtures.loadModels("data.yml");
    }

    @Test
    public void testThatIndexPageWorks() {
        Response response = GET("/");
        if (Play.configuration.getProperty("cas.mockserver").equals("true")) {
            assertStatus(200, response);
        } else {
            assertStatus(302, response);
        }
    }

    @Test
    public void testThatStatsPageWorks() {
        Response response = GET("/stats/google");
        assertIsOk(response);
    }

}