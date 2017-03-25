package thenextvoyager.wallser.utility;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import thenextvoyager.wallser.Data.DataModel;
import thenextvoyager.wallser.Data.ImageContract;


/**
 * Created by Abhiroj on 3/6/2017.
 */

public class Utility {

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
            Toast.makeText(context, "No image present!", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * returns true if image with given id as param1 exists in Database.
     *
     * @param resolver
     * @param COLUMN_NAME
     * @param param1
     * @return
     */
    public static boolean checkIfImageIsInDatabase(ContentResolver resolver, String COLUMN_NAME, String param1) {
        Cursor cursor = resolver.query(ImageContract.ImageEntry.CONTENT_URI, new String[]{COLUMN_NAME}, COLUMN_NAME + " = ?", new String[]{param1}, null);
        return cursor.getCount() > 0;
    }

    public static DataModel makeDataModelFromCursor(Cursor cursor) {
        String imageURL = cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.COLUMN_REGURL));
        String downloaURL = cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.COLUMN_DLDURL));
        String imageId = cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.COLUMN_NAME));

        return new DataModel(imageURL, downloaURL, imageId);
    }

    public static boolean detectConnection(Context context) {
        if (context == null) return false;
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile_info = conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi_info = conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobile_info == null || wifi_info == null) return false;
        if (mobile_info.getState() == NetworkInfo.State.CONNECTED
                || wifi_info.getState() == NetworkInfo.State.CONNECTED) {

            return true;

        } else if (mobile_info.getState() == NetworkInfo.State.DISCONNECTED
                || wifi_info.getState() == NetworkInfo.State.DISCONNECTED) {

            return false;
        }
        return false;
    }

    public static class AsyncImageDownloader extends AsyncTask<Object, Object, Void> {


        private static final String TAG = AsyncImageDownloader.class.getSimpleName();
        Context context;
        String downloadURL;
        ProgressDialog dialog;


        AsyncImageDownloader(Context context, String downloadURL) {
            this.context = context;
            this.downloadURL = downloadURL;
        }


        @Override
        protected Void doInBackground(Object... voids) {
            try {
                URL url = new URL("https://unsplash.com/photos/65sru5g6xHk/download");
                HttpURLConnection connection = getFinalURL(url);
                connection.setDoInput(true);
                connection.connect();
                InputStream inptStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inptStream);
                saveImage(getResizedBitmap(bitmap, 25, 25), context, "id of downloaded image",false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        HttpURLConnection getFinalURL(URL url) throws IOException {
            URL finalUrl = null;
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM) {
                URL redirectUrl = new URL(httpURLConnection.getHeaderField("Location"));
                finalUrl = redirectUrl;
            }
            return (HttpURLConnection) finalUrl.openConnection();
        }

        public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                    matrix, false);

            return resizedBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.show();
            dialog.setMessage("Downloading");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }
    }


}
