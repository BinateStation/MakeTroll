package rkr.binatestation.maketroll.web;


import rkr.binatestation.maketroll.BuildConfig;

/**
 * Created by RKR on 20-12-2016.
 * WebServiceConstants.
 */

public final class WebServiceConstants {

    public static final String KEY_ITEM_MODELS = "item_models";
    public static final String KEY_DATA = "data";
    public static final String KEY_STATUS = "status";
    public static final String KEY_MESSAGE = "message";
    public static final String URL_LIVE_IMAGE = "http://binatestation.com/picme/images/";
    static final String KEY_SEARCH = "search";
    /**
     * Project base API url
     */
    private static final String URL_LIVE_BASE = "http://binatestation.com/picme/";
    private static final String URL_BASE = BuildConfig.DEBUG ? URL_LIVE_BASE : URL_LIVE_BASE;
    /**
     * End Urls
     */
    static String URL_SEARCH = URL_BASE + "search.php";
}
