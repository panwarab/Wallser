package thenextvoyager.wallser.asynctasks;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.ImageView;
import android.widget.Toast;

import thenextvoyager.wallser.R;
import thenextvoyager.wallser.data.DataModel;
import thenextvoyager.wallser.data.ImageContract;

/**
 * Created by Abhiroj on 3/24/2017.
 */

public class DeleteDataTask extends AsyncTask<DataModel, Void, Boolean> {

    Fragment fragment;
    Context context;
    ContentResolver resolver;
    ImageView favoriteb;

    public DeleteDataTask(Fragment fragment, ImageView favoriteb) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        resolver = context.getContentResolver();
        this.favoriteb = favoriteb;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            favoriteb.setImageResource(R.drawable.ic_favorite_border);
            fragment.getActivity().finish();
            fragment.getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        } else {
            Toast.makeText(context, R.string.image_present, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected Boolean doInBackground(DataModel... dataModels) {
        try {
            DataModel object = dataModels[0];
            Cursor cursor = resolver.query(ImageContract.ImageEntry.CONTENT_URI, null, ImageContract.ImageEntry.COLUMN_NAME + "=?", new String[]{object.name}, null);
            cursor.moveToFirst();
            long id = cursor.getLong(cursor.getColumnIndex(ImageContract.ImageEntry._ID));
            Uri image_uri = ImageContract.ImageEntry.buildImageuri(id);
            resolver.delete(image_uri, null, null);
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
