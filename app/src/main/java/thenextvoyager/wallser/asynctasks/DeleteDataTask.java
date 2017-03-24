package thenextvoyager.wallser.asynctasks;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import thenextvoyager.wallser.Data.DataModel;
import thenextvoyager.wallser.Data.ImageContract;
import thenextvoyager.wallser.R;

/**
 * Created by Abhiroj on 3/24/2017.
 */

public class DeleteDataTask extends AsyncTask<DataModel, Void, Boolean> {

    public static final String TAG = DeleteDataTask.class.getSimpleName();
    Fragment fragment;
    Context context;
    ContentResolver resolver;
    FloatingActionButton favoriteb;

    public DeleteDataTask(Fragment fragment, FloatingActionButton favoriteb) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        resolver = context.getContentResolver();
        this.favoriteb = favoriteb;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            favoriteb.setImageResource(R.drawable.ic_favorite);
            fragment.getActivity().finish();
            fragment.getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        } else {
            Toast.makeText(context, "Couldn't delete from favorites", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected Boolean doInBackground(DataModel... dataModels) {
        try {
            DataModel object = dataModels[0];
            Cursor cursor = resolver.query(ImageContract.ImageEntry.CONTENT_URI, null, ImageContract.ImageEntry.COLUMN_NAME + "=?", new String[]{object.name}, null);
            Log.d(TAG, "Cursor Count is " + cursor.getCount());
            for (int i = 0; i < cursor.getColumnCount(); i++)
                Log.d(TAG, "Cursor Column " + cursor.getColumnName(i));
            cursor.moveToFirst();
            long id = cursor.getLong(cursor.getColumnIndex(ImageContract.ImageEntry._ID));
            Log.d(TAG, "ID fetched is " + id);
            Uri image_uri = ImageContract.ImageEntry.buildImageuri(id);
            resolver.delete(image_uri, null, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
