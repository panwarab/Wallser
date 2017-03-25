package thenextvoyager.wallser.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import thenextvoyager.wallser.Data.DataModel;
import thenextvoyager.wallser.R;
import thenextvoyager.wallser.activity.ImageActivity;
import thenextvoyager.wallser.viewholder.ViewHolder;

import static thenextvoyager.wallser.Data.Constants.MODEL_TAG;

/**
 * Created by Abhiroj on 3/4/2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<ViewHolder> {


    Context context;
    ArrayList<DataModel> model;

    public ImageAdapter(Context context, ArrayList<DataModel> model) {
        this.context = context;
        this.model = model;
    }

    public void swapDataSet(ArrayList<DataModel> updatedData) {
        model.clear();
        model.addAll(updatedData);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.displayimage, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Bundle args = new Bundle();
        final ImageView imageView = holder.image;
        args.putSerializable(MODEL_TAG, model.get(position)); // Passing the current item
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ImageActivity.class);
                intent.putExtras(args);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), imageView, "shared_image");
                view.getContext().startActivity(intent, options.toBundle());
            }
        });

        Picasso.with(context).load(model.get(position).imageURL.trim()).error(R.drawable.placeholder1).placeholder(R.drawable.placeholder1).resize(200, 200).centerCrop().into(holder.image);

    }


    @Override
    public int getItemCount() {
        return model.size();
    }

}
