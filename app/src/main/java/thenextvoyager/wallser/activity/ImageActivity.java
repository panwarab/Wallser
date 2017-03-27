package thenextvoyager.wallser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import thenextvoyager.wallser.Data.DataModel;
import thenextvoyager.wallser.R;
import thenextvoyager.wallser.fragment.ImageFragment;

import static thenextvoyager.wallser.Data.Constants.IMAGE_FRAGMENT_TAG;
import static thenextvoyager.wallser.Data.Constants.MODEL_TAG;


public class ImageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        DataModel object = (DataModel) bundle.getSerializable(MODEL_TAG);

        ImageFragment fragment = ImageFragment.newInstance(object);
        getSupportFragmentManager().beginTransaction().add(R.id.image_frag_container, fragment, IMAGE_FRAGMENT_TAG).commit();
    }


}
