package thenextvoyager.wallser.fragment;


import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import thenextvoyager.wallser.utility.Utility;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_image, container, false);
        final ImageView imageView = (ImageView) rootView.findViewById(R.id.fragment_image);
        final FloatingActionButton downloadb = (FloatingActionButton) rootView.findViewById(R.id.download_button);
        downloadb.setImageResource(ic_file_download);
        final FloatingActionButton favoriteb = (FloatingActionButton) rootView.findViewById(R.id.favorite_button);
        favoriteb.setImageResource(ic_favorite);
        final FloatingActionButton wallpaperb = (FloatingActionButton) rootView.findViewById(R.id.wallpaper_button);
        wallpaperb.setImageResource(ic_wallpaper);
        ImageView close_button = (ImageView) rootView.findViewById(R.id.cross_button);
        close_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().finish();
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
                imageView.setImageBitmap(bitmap);
                favoriteb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri uri = ImageContract.ImageEntry.CONTENT_URI;
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(ImageContract.ImageEntry.COLUMN_NAME, object.name);
                        contentValues.put(ImageContract.ImageEntry.COLUMN_REGURL, object.imageURL);
                        contentValues.put(ImageContract.ImageEntry.COLUMN_DLDURL, object.downloadURL);
                        try {
                            ContentResolver resolver = getContext().getContentResolver();
                            if (!(Utility.checkIfImageIsInDatabase(resolver, ImageContract.ImageEntry.COLUMN_NAME, object.name))) {
                                resolver.insert(uri, contentValues);
                                Cursor cursor = resolver.query(uri, new String[]{ImageContract.ImageEntry.COLUMN_NAME, ImageContract.ImageEntry.COLUMN_DLDURL, ImageContract.ImageEntry.COLUMN_REGURL}, null, null, null);
                                if (cursor != null) {
                                    cursor.moveToFirst();
                                    do {
                                        Log.d(TAG, "Cursor data : Name " + cursor.getString(0) + "\n");
                                        if (cursor.isAfterLast()) break;
                                    } while ((cursor.moveToNext()));
                                }
                                cursor.close();
                            } else {
                                Toast.makeText(getContext(), "Image present", Toast.LENGTH_LONG).show();
                            }
                        } catch (Error error) {
                            Log.e(TAG, "Insert failed due to " + error.getCause());
                        }
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
                        Toast.makeText(getContext(), "Wallpapaer Set!!", Toast.LENGTH_SHORT).show();
                    }
                });
                downloadb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (Utility.saveImage(bitmap, getContext(), object.name, false))
                                Toast.makeText(getContext(), "Image Present!!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getContext(), "Download done!!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Could not download!!", Toast.LENGTH_SHORT).show();
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
        Picasso.with(rootView.getContext()).load(object.imageURL.trim()).error(R.drawable.sample).placeholder(R.drawable.sample).into(target);
    }


    private void recordAnalyticsEvent(String name, String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        analytics = FirebaseAnalytics.getInstance(getContext());
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}
