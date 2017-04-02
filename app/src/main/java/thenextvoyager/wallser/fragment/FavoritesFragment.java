package thenextvoyager.wallser.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import thenextvoyager.wallser.R;
import thenextvoyager.wallser.adapter.FavoritesAdapter;
import thenextvoyager.wallser.data.Constants;
import thenextvoyager.wallser.data.ImageContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {



    FavoritesAdapter favoritesAdapter;
    FrameLayout empty_view_cont;
    RecyclerView view1;
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = new CursorLoader(getContext(), ImageContract.ImageEntry.CONTENT_URI, null, null, null, null);
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data.getCount() != 0) {
                view1.setVisibility(View.VISIBLE);
                empty_view_cont.setVisibility(View.INVISIBLE);
                favoritesAdapter.swapCursor(data);
            } else if (data.getCount() == 0) {
                empty_view_cont.setVisibility(View.VISIBLE);
                view1.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            favoritesAdapter.swapCursor(null);
        }
    };

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(Constants.CURSOR_LOADER_MANAGER, null, loaderCallbacks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        empty_view_cont = (FrameLayout) view.findViewById(R.id.empty_view_container);
        empty_view_cont.setVisibility(View.VISIBLE);
        view1 = (RecyclerView) view.findViewById(R.id.recyclerview_favorite);
        view1.setVisibility(View.INVISIBLE);
        view1.setLayoutManager(new GridLayoutManager(getContext(), 1));
        favoritesAdapter = new FavoritesAdapter(getContext(), null);
        view1.setAdapter(favoritesAdapter);
        return view;
    }

}
