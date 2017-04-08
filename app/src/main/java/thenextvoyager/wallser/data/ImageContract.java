package thenextvoyager.wallser.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Abhiroj on 3/9/2017.
 */

public class ImageContract {

    public static final String CONTENT_AUTHORITY = ImageContract.class.getPackage().getName(); // To identify the content provider unambigiously
    public static final String PATH_IMAGE = "image"; // Image Table
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY); // To contanct the content provider

    public static final class ImageEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGE).build(); // Complete path to the image table

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_IMAGE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_IMAGE;

        // Table schema
        public static final String TABLE_NAME = "imageTable";
        public static final String COLUMN_NAME = "imageName";
        public static final String COLUMN_REGURL = "imageRegUrl";
        public static final String COLUMN_DLDURL = "imageDldUrl";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_PORTFOLIONAME = "portfolioname";
        public static final String COLUMN_PROFILEIMAGE = "profileimage";

        // Uri appended with to find an image by identifier
        public static Uri buildImageuri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
