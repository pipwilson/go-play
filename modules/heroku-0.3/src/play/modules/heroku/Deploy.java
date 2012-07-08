package play.modules.heroku;


import com.heroku.herokudeployer.HerokuDeployer;

public class Deploy {

  public static void main(String[] args) {

    String[] dargs = new String[]{System.getProperty("application.path")};
    HerokuDeployer.main(dargs);

  }

}
