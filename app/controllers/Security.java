package controllers;

import controllers.modules.cas.*;

public class Security extends SecureCAS {

    static boolean onAuthenticated(String username, String password) {
                System.out.println("Hiiiii authenticatteee");
        System.out.println(username + " : " + password);
        return true;
    }

}