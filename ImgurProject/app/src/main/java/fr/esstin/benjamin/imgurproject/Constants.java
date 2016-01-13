package fr.esstin.benjamin.imgurproject;

/**
 * Created by Benjamin on 02/12/2015.
 */
public class Constants {

    public static final String GIF = "image/gif";

    public static final String MY_IMGUR_CLIENT_ID = "5545f69fac92186";
    public static final String MY_IMGUR_CLIENT_SECRET = "cbf89a5df48cd9b33f5a6a9797a081f1f5f801a5";

    public static String getClientAuth() {
        return "Client-ID " + MY_IMGUR_CLIENT_ID;
    }

}

