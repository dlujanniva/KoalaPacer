package dlv.niva.mx.koalapacer.db;

import android.provider.BaseColumns;

/**
 * Created by daniellujanvillarreal on 3/27/15.
 */
public final class CrawlerDB {
    public CrawlerDB(){}

//    public static abstract class ClickDB implements BaseColumns{
//        public static final String TABLE_NAME = "click";
//        public static final String COLUMN_NAME_ID = "_id";
//        public static final String COLUMN_NAME_DOMAIN = "domain";
//        public static final String COLUMN_NAME_NETWORK = "network";
//        public static final String COLUMN_NAME_MEGABYTES = "megabytes";
//        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
//        public static final String COLUMN_NAME_IMAGE = "image";
//    }

//    public static abstract class RedirectDB implements BaseColumns{
//        public static final String TABLE_NAME = "redirect";
//        public static final String COLUMN_NAME_ID = "_id";
//        public static final String COLUMN_NAME_CLICKID = "click_id";
//        public static final String COLUMN_NAME_DOMAIN = "domain";
//        public static final String COLUMN_NAME_URL = "url";
//        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
//    }

    public static abstract class LandingDB implements BaseColumns{
        public static final String TABLE_NAME = "landing";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_IDCONTROL = "id_control";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_NETWORK = "network";
    }

    public static abstract class LandingParsedDB implements BaseColumns{
        public static final String TABLE_NAME = "landingParsed";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_IDCONTROL = "id_control";
        public static final String COLUMN_NAME_IMAGETYPE = "imgType";
        public static final String COLUMN_NAME_TIME = "loadTime";
    }

    public static abstract class LandingColorsDB implements BaseColumns{
        public static final String TABLE_NAME = "landingColors";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_IDCONTROL = "id_control";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
    }

//    public static abstract class DomainDB implements BaseColumns{
//        public static final String TABLE_NAME = "domain";
//        public static final String COLUMN_NAME_ID = "_id";
//        public static final String COLUMN_NAME_IS_ADSENSE = "isAdsense";
//        public static final String COLUMN_NAME_URL = "url";
//        public static final String COLUMN_NAME_VISITED = "visited";
//        public static final String COLUMN_NAME_SECONDS = "seconds";
//    }
}
