package thenextvoyager.wallser.fragment;


import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import thenextvoyager.wallser.Data.DataModel;
import thenextvoyager.wallser.Data.ImageContract;
import thenextvoyager.wallser.R;
import thenextvoyager.wallser.asynctasks.AddDataTask;
import thenextvoyager.wallser.asynctasks.DeleteDataTask;
import thenextvoyager.wallser.utility.Utility;

import static thenextvoyager.wallser.Data.Constants.IMAGE_FRAGMENT_TAG;
import static thenextvoyager.wallser.R.drawable.ic_favorite;
import static thenextvoyager.wallser.R.drawable.ic_file_download;
import static thenextvoyager.wallser.R.drawable.ic_wallpaper;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {

    private static final String ARG_PARAM = "param2";
    private static final String TAG = ImageFragment.class.getSimpleName();
    DataModel object;
    private boolean isImageInDatabase = false;
    private ContentResolver resolver;
    private FirebaseAnalytics analytics;

    public static ImageFragment newInstance(DataModel object) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, object);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            object = (DataModel) getArguments().getSerializable(ARG_PARAM);
        }
        resolver = getContext().getContentResolver();
        isImageInDatabase = Utility.checkIfImageIsInDatabase(resolver, ImageContract.ImageEntry.COLUMN_NAME, object.name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        final ImageView imageView = (ImageView) rootView.findViewById(R.id.fragment_image);
        final FloatingActionButton downloadb = (FloatingActionButton) rootView.findViewById(R.id.download_button);
        downloadb.setImageResource(ic_file_download);
        downloadb.setVisibility(View.INVISIBLE);
        final FloatingActionButton favoriteb = (FloatingActionButton) rootView.findViewById(R.id.favorite_button);
        if (isImageInDatabase) favoriteb.setImageResource(R.drawable.ic_favorite_filled);
        else
        favoriteb.setImageResource(ic_favorite);
        favoriteb.setVisibility(View.INVISIBLE);
        final FloatingActionButton wallpaperb = (FloatingActionButton) rootView.findViewById(R.id.wallpaper_button);
        wallpaperb.setImageResource(ic_wallpaper);
        wallpaperb.setVisibility(View.INVISIBLE);
        ImageView close_button = (ImageView) rootView.findViewById(R.id.cross_button);
        close_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().supportFinishAfterTransition();
                    }
                }
        );

        final ImageView share_button = (ImageView) rootView.findViewById(R.id.share_button);

        if (object != null) {
            picasso(imageView, downloadb, favoriteb, wallpaperb, share_button, rootView);
        }

        return rootView;
    }

    private void picasso(final ImageView imageView, final FloatingActionButton downloadb, final FloatingActionButton favoriteb, final FloatingActionButton wallpaperb, final ImageView share_button, View rootView) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                Palette.generateAsync(bitmap, 3, new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int color = palette.getDominantColor(Color.BLACK);
                        if (color == Color.BLACK)
                            color = palette.getVibrantColor(Color.BLACK);
                        if (color == Color.BLACK)
                            color = palette.getDarkVibrantColor(Color.BLACK);
                        if (color == Color.BLACK)
                            color = palette.getMutedColor(Color.BLACK);

                        downloadb.setBackgroundTintList(ColorStateList.valueOf(color));
                        favoriteb.setBackgroundTintList(ColorStateList.valueOf(color));
                        wallpaperb.setBackgroundTintList(ColorStateList.valueOf(color));
                    }
                });
                downloadb.setVisibility(View.VISIBLE);
                favoriteb.setVisibility(View.VISIBLE);
                wallpaperb.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
                favoriteb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!isImageInDatabase) {
                            Log.d(TAG, "here here here");
                            AddDataTask addDataTask = new AddDataTask(getFragmentManager().findFragmentByTag(IMAGE_FRAGMENT_TAG), favoriteb);
                            addDataTask.execute(object);
                        } else {
                            Log.d(TAG, "there there there");
                            DeleteDataTask dataTask = new DeleteDataTask(getFragmentManager().findFragmentByTag(IMAGE_FRAGMENT_TAG), favoriteb);
                            dataTask.execute(object);
                        }
                        updateBooleanImageInDatabase();
                    }
                });
                wallpaperb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recordAnalyticsEvent(object.name, "image");
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
                        try {
                            wallpaperManager.setBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), R.string.wallpaper_set, Toast.LENGTH_SHORT).show();
                    }
                });
                downloadb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (Utility.saveImage(bitmap, getContext(), object.name, false))
                                Toast.makeText(getContext(), R.string.image_present, Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getContext(), R.string.download_success, Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), R.string.download_failed, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                });
                share_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Utility.saveImage(bitmap, getContext(), object.name, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                imageView.setImageDrawable(errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                imageView.setImageDrawable(placeHolderDrawable);
            }
        };
        Picasso.with(rootView.getContext()).load(object.imageURL.trim()).error(R.drawable.placeholder1).placeholder(R.drawable.placeholder1).into(target);
    }

    private void updateBooleanImageInDatabase() {
        isImageInDatabase = Utility.checkIfImageIsInDatabase(resolver, ImageContract.ImageEntry.COLUMN_NAME, object.name);
    }


    private void recordAnalyticsEvent(String name, String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        analytics = FirebaseAnalytics.getInstance(getContext());
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
