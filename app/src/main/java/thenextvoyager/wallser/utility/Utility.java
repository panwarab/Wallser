package thenextvoyager.wallser.utility;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import thenextvoyager.wallser.Data.ImageContract;


/**
 * Created by Abhiroj on 3/6/2017.
 */

public class Utility {

    public static boolean saveImage(Bitmap bitmap, Context context, String name) throws Exception {
        if (bitmap != null) {
            Log.d(context.getPackageName(), "Storing bitmap");
            String fname = name + ".jpg";
            File root = Environment.getExternalStorageDirectory();
            File walserDirectory = new File(root, "Walser");
            if (!walserDirectory.exists())
                walserDirectory.mkdirs();
            String imageFinalPath = walserDirectory.getPath() + "/" + fname;
            if (new File(imageFinalPath).exists()) return true;
            else {
                File imageFile = new File(walserDirectory, fname);
                Log.d(context.getPackageName(), "Image Path = " + imageFile.getPath());

                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.close();

                //Manually, add file to gallery
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageFile.getPath())));
                return false;
            }
        } else {
            Log.d(context.getPackageName(), "Null bitmap");
        }
        return false;
    }

    public static boolean checkIfImageIsInDatabase(ContentResolver resolver, String TABLE_NAME, String param1) {
        Cursor cursor = resolver.query(ImageContract.ImageEntry.CONTENT_URI, new String[]{TABLE_NAME}, TABLE_NAME + " = ?", new String[]{param1}, null);
        return cursor.getCount() > 0;
    }

    /**
     * Created by Abhiroj on 3/8/2017.
     */

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
                Log.d(TAG, "URL received : --- >" + downloadURL);
                URL url = new URL("https://unsplash.com/photos/65sru5g6xHk/download");
                HttpURLConnection connection = getFinalURL(url);
                connection.setDoInput(true);
                connection.connect();
                InputStream inptStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inptStream);
                saveImage(getResizedBitmap(bitmap, 25, 25), context, "id of downloaded image");
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
                Log.d(TAG, "Redirected URL --->" + redirectUrl);
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
