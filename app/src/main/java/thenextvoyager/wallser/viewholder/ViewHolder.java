package thenextvoyager.wallser.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import thenextvoyager.wallser.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView image;

    public ViewHolder(View itemView) {
        super(itemView);

        image = (ImageView) itemView.findViewById(R.id.image);
    }

}

