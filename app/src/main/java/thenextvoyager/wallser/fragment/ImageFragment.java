package thenextvoyager.wallser.fragment;


import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import thenextvoyager.wallser.R;
import thenextvoyager.wallser.asynctasks.AddDataTask;
import thenextvoyager.wallser.asynctasks.DeleteDataTask;
import thenextvoyager.wallser.data.DataModel;
import thenextvoyager.wallser.data.ImageContract;
import thenextvoyager.wallser.utility.Utility;

import static thenextvoyager.wallser.data.Constants.IMAGE_FRAGMENT_TAG;
import static thenextvoyager.wallser.data.Constants.makeUserURL;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageFragment extends Fragment {

    private static final String ARG_PARAM = "param2";
    private static final String TAG = ImageFragment.class.getSimpleName();
    DataModel object;
    ImageView imageView;
    private boolean isImageInDatabase = false;
    private ContentResolver resolver;
    private FirebaseAnalytics analytics;
    private ImageView downloadb;
    private ImageView favoriteb;
    private ImageView wallpaperb;

    public static ImageFragment newInstance(DataModel object) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, object);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        imageView = null;
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
        imageView = (ImageView) rootView.findViewById(R.id.fragment_image);

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
        /*FloatingActionButton material_fab = (FloatingActionButton) rootView.findViewById(R.id.material_fab);
        */
        View scrollView = rootView.findViewById(R.id.scroll_view);

        final BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(scrollView);
        final LinearLayout id_layout = (LinearLayout) rootView.findViewById(R.id.first_horizontal_layout);
        ViewTreeObserver viewTreeObserver = id_layout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                id_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                sheetBehavior.setPeekHeight(id_layout.getHeight());
            }
        });
        downloadb = (ImageView) rootView.findViewById(R.id.download_button);
        favoriteb = (ImageView) rootView.findViewById(R.id.favorite_button);
        if (isImageInDatabase)
            favoriteb.setImageResource(R.drawable.ic_favorite_filled);
        else
            favoriteb.setImageResource(R.drawable.ic_favorite_border);
        wallpaperb = (ImageView) rootView.findViewById(R.id.wallpaper_button);
        TextView name = (TextView) rootView.findViewById(R.id.name);
        name.setPaintFlags(name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        name.setText(new StringBuffer().append("@").append(object.user_name).toString());
        final String portfolio_url = makeUserURL(object.portfolio_name);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(portfolio_url));
                getActivity().startActivity(i);
            }
        });

        CircleImageView profile_image = (CircleImageView) rootView.findViewById(R.id.circle_photo);
        profile_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(getContext()).load(object.profile_image).error(R.drawable.placeholder).resize(480, 480).into(profile_image);
      /*  material_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.material_fab:
                        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        else
                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                }
            }
        });*/
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fragment_image:
                        if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        else
                            getActivity().supportFinishAfterTransition();
                        break;
                }
            }
        });

        if (object != null) {
            picasso(share_button, rootView);
        }

        return rootView;
    }

    private void picasso(final ImageView share_button, View rootView) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                if (Build.VERSION.SDK_INT >= 21)
                    getActivity().startPostponedEnterTransition();
                else
                    getActivity().supportStartPostponedEnterTransition();
                if (imageView != null)
                imageView.setImageBitmap(bitmap);
                favoriteb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!isImageInDatabase) {
                            AddDataTask addDataTask = new AddDataTask(getFragmentManager().findFragmentByTag(IMAGE_FRAGMENT_TAG), favoriteb);
                            addDataTask.execute(object);
                        } else {
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
        imageView.setTag(target);
        Picasso.with(rootView.getContext()).load(object.imageURL.trim()).error(R.drawable.sample).placeholder(R.drawable.placeholder1).into(target);
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
