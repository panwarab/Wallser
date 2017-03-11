package thenextvoyager.wallser.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by Abhiroj on 3/4/2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private static final String MODEL_TAG = "data_model";
    private static final String MODEL_TAG1 = "pos";
    Context context;
    ArrayList<DataModel> model;

    public ImageAdapter(Context context, ArrayList<DataModel> model) {
        this.context = context;
        this.model = model;
    }


    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.displayimage, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ImageAdapter.ViewHolder holder, final int position) {


        ImageView imageView = holder.image;
        final Bundle args = new Bundle();
        args.putSerializable(MODEL_TAG, model);
        args.putInt(MODEL_TAG1, position);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ImageActivity.class);
                intent.putExtras(args);
                view.getContext().startActivity(intent);
            }
        });

        Picasso.with(context).load(model.get(position).imageURL.trim()).error(R.drawable.sample).into(imageView);


    }

    @Override
    public int getItemCount() {
        return model.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
        }

    }
}
