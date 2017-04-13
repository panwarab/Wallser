package thenextvoyager.wallser.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import thenextvoyager.wallser.R;

import static thenextvoyager.wallser.data.Constants.githubURL;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView textView = (TextView) findViewById(R.id.dev_name);
        textView.setText("Abhiroj Panwar");
        textView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | textView.getPaintFlags());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(githubURL)));
            }
        });
    }

}
