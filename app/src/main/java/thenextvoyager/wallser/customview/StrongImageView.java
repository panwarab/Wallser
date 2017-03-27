package thenextvoyager.wallser.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Abhiroj on 3/27/2017.
 */

public class StrongImageView extends android.support.v7.widget.AppCompatImageView implements Target {
    public StrongImageView(Context context) {
        super(context);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
