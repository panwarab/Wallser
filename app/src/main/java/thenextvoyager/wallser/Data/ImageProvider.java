package thenextvoyager.wallser.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Abhiroj on 3/9/2017.
 */

public class ImageProvider extends ContentProvider {

    private static final int IMAGE = 100;
    private static final int IMAGE_ID = 101;

    private static final UriMatcher MATCHER = buildUriMatcher();

    private ImageDBHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        String content_auth = ImageContract.CONTENT_AUTHORITY;

        UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        MATCHER.addURI(content_auth, ImageContract.PATH_IMAGE, IMAGE);
        MATCHER.addURI(content_auth, ImageContract.PATH_IMAGE + "/#", IMAGE_ID);

        return MATCHER;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new ImageDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionargs, @Nullable String sortorder) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor retCursor = null;

        switch (MATCHER.match(uri)) {
            case IMAGE:
                retCursor = database.query(ImageContract.ImageEntry.TABLE_NAME, projection, selection, selectionargs, null, null, sortorder);
                break;
            case IMAGE_ID:
                long _id = ContentUris.parseId(uri);
                retCursor = database.query(ImageContract.ImageEntry.TABLE_NAME, projection, ImageContract.ImageEntry._ID + "= ?", new String[]{String.valueOf(_id)}, null, null, sortorder);
                break;
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = MATCHER.match(uri);
        switch (match) {
            case IMAGE:
                return ImageContract.ImageEntry.CONTENT_TYPE;
            case IMAGE_ID:
                return ImageContract.ImageEntry.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch (MATCHER.match(uri)) {
            case IMAGE:
                _id = db.insert(ImageContract.ImageEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    returnUri = ImageContract.ImageEntry.buildImageuri(_id);
                } else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        switch (MATCHER.match(uri)) {
            case IMAGE:
                database.execSQL("DROP TABLE " + ImageContract.ImageEntry.TABLE_NAME + ";");
                break;
            case IMAGE_ID:
                long id = (int) ContentUris.parseId(uri);
                database.execSQL("DELETE FROM " + ImageContract.ImageEntry.TABLE_NAME + " WHERE " + ImageContract.ImageEntry._ID + "=" + id);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

}
