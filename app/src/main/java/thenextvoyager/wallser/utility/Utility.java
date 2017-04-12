package thenextvoyager.wallser.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.SpannableStringBuilder;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import thenextvoyager.wallser.R;
import thenextvoyager.wallser.data.DataModel;
import thenextvoyager.wallser.data.ImageContract;


/**
 * Created by Abhiroj on 3/6/2017.
 */

public class Utility {

    public static final int MAX_LINK_LENGTH = 20;
    private static File walserDirectory;

    /**
     * Saves the image in disk or shares it via Intent; share variable defines the purpose.
     *
     * @param bitmap
     * @param context
     * @param name
     * @param share
     * @return wether operation completed succesfully or not
     * @throws Exception
     */
    public static boolean saveImage(Bitmap bitmap, Context context, String name, boolean share) throws Exception {
        if (bitmap != null) {
            String fname = name + ".jpeg";
            File root = Environment.getExternalStorageDirectory();
            walserDirectory = new File(root, "Wallser");
            if (!walserDirectory.exists())
                walserDirectory.mkdirs();
            String imageFinalPath = walserDirectory.getPath() + "/" + fname;
            if (new File(imageFinalPath).exists()) {
                if (share) {
                    File file = new File(imageFinalPath);
                    deleteFileAfterShare(file, context);
                }
                return true;
            }
            else {
                File imageFile = new File(walserDirectory, fname);

                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.close();

                if (share)
                    deleteFileAfterShare(imageFile, context);
                else {
                    //Manually, add file to gallery
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageFile.getPath())));

                }
                return false;
            }
        }
        return false;
    }

    private static void deleteFileAfterShare(File file, Context context) {
        if (file.exists()) {
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            String type = mimeTypeMap.getMimeTypeFromExtension(ext);
            Intent shareintent = new Intent(Intent.ACTION_SEND);
            shareintent.setType("*/*");
            shareintent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            shareintent.putExtra(Intent.EXTRA_TEXT, "Hey! check new walls at wallser!");
            context.startActivity(Intent.createChooser(shareintent, "Share Using"));
        } else {
            Toast.makeText(context, R.string.no_image_present, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * returns true if image with given id as param1 exists in Database.
     *
     * @param resolver
     * @param COLUMN_NAME
     * @param param1
     * @return Boolean
     */
    public static boolean checkIfImageIsInDatabase(ContentResolver resolver, String COLUMN_NAME, String param1) {
        Cursor cursor = resolver.query(ImageContract.ImageEntry.CONTENT_URI, new String[]{COLUMN_NAME}, COLUMN_NAME + " = ?", new String[]{param1}, null);
        return cursor.getCount() > 0;
    }

    public static DataModel makeDataModelFromCursor(Cursor cursor) {
        String imageURL = cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.COLUMN_REGURL));
        String downloaURL = cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.COLUMN_DLDURL));
        String imageId = cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.COLUMN_NAME));
        String username = cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.COLUMN_USERNAME));
        String portfoliourl = cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.COLUMN_PORTFOLIONAME));
        String profile_image = cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.COLUMN_PROFILEIMAGE));
        return new DataModel(imageURL, downloaURL, imageId, username, portfoliourl, profile_image);
    }

    public static boolean detectConnection(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static CharSequence shortenLinks(String text) {
        return shortenLinks(text, Linkify.ALL);
    }

    public static CharSequence shortenLinks(String text, int linkMask) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Linkify.addLinks(builder, linkMask);
        URLSpan[] spans = builder.getSpans(0, builder.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = builder.getSpanStart(span);
            int end = builder.getSpanEnd(span);
            int flags = builder.getSpanFlags(span);

            CharSequence linkText = builder.subSequence(start, end);
            if (linkText.length() > MAX_LINK_LENGTH) {
                linkText = linkText.subSequence(0, 20) + "…";
                builder.replace(start, end, linkText);
                builder.removeSpan(span);
                builder.setSpan(span, start, start + linkText.length(), flags);
            }
        }
        return builder;
    }



}
