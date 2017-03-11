package thenextvoyager.wallser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

import thenextvoyager.wallser.Data.DataModel;
import thenextvoyager.wallser.R;
import thenextvoyager.wallser.fragment.ImagePager;


public class ImageActivity extends AppCompatActivity {

    private static final String MODEL_TAG = "data_model";
    private static final String MODEL_TAG1 = "pos";
    private static final String TAG = ImageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final ArrayList<DataModel> model = (ArrayList<DataModel>) bundle.getSerializable(MODEL_TAG);
        int position = bundle.getInt(MODEL_TAG1);

        Log.d(TAG, model + "");
        ViewPager viewPager = (ViewPager) findViewById(R.id.image_pager);
        final ImagePager imagePager = new ImagePager(getSupportFragmentManager(), model, this);
        viewPager.setAdapter(imagePager);
        viewPager.setCurrentItem(position);

    }


}
