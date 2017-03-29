package thenextvoyager.wallser.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import thenextvoyager.wallser.R;
import thenextvoyager.wallser.activity.ImageActivity;
import thenextvoyager.wallser.data.DataModel;
import thenextvoyager.wallser.data.ImageContract;
import thenextvoyager.wallser.utility.CursorRecyclerViewAdapter;
import thenextvoyager.wallser.viewholder.ViewHolder;

import static thenextvoyager.wallser.data.Constants.MODEL_TAG;
import static thenextvoyager.wallser.utility.Utility.makeDataModelFromCursor;

/**
 * Created by Abhiroj on 3/13/2017.
 */

public class FavoritesAdapter extends CursorRecyclerViewAdapter<ViewHolder> {
    Context context;
    Cursor cursor;
    DataModel object;

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
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        int pos = viewHolder.getAdapterPosition();
        cursor.moveToPosition(pos);

        final Bundle args = new Bundle();
        args.putSerializable(MODEL_TAG, makeDataModelFromCursor(cursor));
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ImageActivity.class);
                i.putExtras(args);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), viewHolder.image, "shared_image");
                view.getContext().startActivity(i, options.toBundle());
            }
        });
        Picasso.with(context).load(cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.COLUMN_REGURL))).placeholder(R.drawable.placeholder1).resize(200, 200).centerCrop().into(viewHolder.image);
    }


}
