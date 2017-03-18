package thenextvoyager.wallser.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import thenextvoyager.wallser.Data.ImageContract;
import thenextvoyager.wallser.R;

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

        return new RemoteAdapter(WidgetService.this, appWidgetId);
    }


    public static class RemoteAdapter implements RemoteViewsFactory {

        private static final String TAG = RemoteAdapter.class.getSimpleName();
        Cursor cursor;
        Context context;
        int appWidgetId;

        public RemoteAdapter(Context context, int appWidgetId) {
            this.appWidgetId = appWidgetId;
            this.context = context;
        }

        @Override
        public void onCreate() {
            cursor = context.getContentResolver().query(ImageContract.ImageEntry.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            cursor.moveToPosition(i);
            Log.d(TAG, "Cursor is at " + cursor.getPosition());
            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
            try {
                Picasso picasso = Picasso.with(context);
                picasso.setLoggingEnabled(true);
                Bitmap bitmap = picasso.load(cursor.getString(cursor.getColumnIndex(ImageContract.ImageEntry.COLUMN_REGURL))).error(R.drawable.sample).get();
                remoteViews.setImageViewBitmap(R.id.wid_image, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
