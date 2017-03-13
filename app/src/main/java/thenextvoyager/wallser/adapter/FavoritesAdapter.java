package thenextvoyager.wallser.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import thenextvoyager.wallser.Data.ImageContract;
import thenextvoyager.wallser.R;
import thenextvoyager.wallser.utility.CursorRecyclerViewAdapter;
import thenextvoyager.wallser.viewholder.ViewHolder;

/**
 * Created by Abhiroj on 3/13/2017.
 */

public class FavoritesAdapter extends CursorRecyclerViewAdapter<ViewHolder> {
    private static final String TAG = FavoritesAdapter.class.getSimpleName();
    Context context;
    Cursor cursor;

    public FavoritesAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.displayimage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        int pos = viewHolder.getAdapterPosition();
        cursor.moveToPosition(pos);
        Log.d(TAG, "Adapter position = " + pos);
        Picasso.with(context).load(cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.COLUMN_REGURL))).resize(100, 100).centerCrop().into(viewHolder.image);
    }


}
