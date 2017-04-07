package thenextvoyager.wallser.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import thenextvoyager.wallser.R;
import thenextvoyager.wallser.adapter.ImageAdapter;
import thenextvoyager.wallser.callback.OrderByChangeCallback;
import thenextvoyager.wallser.data.Constants;
import thenextvoyager.wallser.data.DataModel;
import thenextvoyager.wallser.network.FetchImageVolley;
import thenextvoyager.wallser.utility.EndlessRecyclerViewScrollListener;

import static thenextvoyager.wallser.data.Constants.CHOICE_TAG;
import static thenextvoyager.wallser.data.Constants.DATA_TAG;
import static thenextvoyager.wallser.data.Constants.HANDLER_DELAY_TIME;
import static thenextvoyager.wallser.data.Constants.PAGE_NO;
import static thenextvoyager.wallser.data.Constants.TagToFrag;
import static thenextvoyager.wallser.utility.Utility.detectConnection;


/**
 * Created by Abhiroj on 3/3/2017.
 */

public class PageFragment extends Fragment implements OrderByChangeCallback {

    private static final String TAG = PageFragment.class.getSimpleName();
    /**
     * Unsplash API, By Default=10
     */
    public static String order_By;
    FetchImageVolley imageVolley;
    Context context;
    ProgressBar progressBar;
    /**
     * Unsplash API call parameter, By Default=latest
     * Change it in Pager Fragment, based on Tab tapped
     */
    RecyclerView recyclerView;
    ImageAdapter imageAdapter;
    GridLayoutManager layoutManager;
    EndlessRecyclerViewScrollListener scrollListener;
    FrameLayout no_internet_container;
    Bundle savedInstanceState;
    int page_no;
    // Attaching Handler to the main thread
    Handler handler;
    boolean shouldHandlerRunAgain = true;
    private boolean isConnected = detectConnection(getContext());
    private ArrayList<DataModel> model;
    /**
     * Handler is attached to the Main Thread and it's message queue, because it is the one who created it.
     * <p>
     * Handler is responsible for checking every second that are we connected to internet, and if we are, then :-
     * 1. Then we remove empty view
     * 2. Make the network call
     * 3. Stop handler from posting the code again using shouldHandlerRunAgain variable
     * 3.1 This is a kill switch otherwise handler will post the runnable again and again to the message queue, which will be executed as soon as it reaches the looper
     * <p>
     * Handler removeCallbacks is used to remove all the pending runnables  in the Message Queue
     */
    Runnable job = new Runnable() {
        @Override
        public void run() {
            swapViews();
            if (shouldHandlerRunAgain)
                handler.postDelayed(job, HANDLER_DELAY_TIME);
        }
    };


    public static PageFragment newInstance(String TAG) {
        PageFragment pageFragment = new PageFragment();
        TagToFrag.put(TAG, pageFragment);
        return pageFragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        imageVolley.destroyRelyingObjects();
        imageVolley = null;
    }

    private Handler makeHandler() {
        return new Handler();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        imageVolley.cancelRequest(order_By);
        outState.putString(CHOICE_TAG, order_By);
        outState.putSerializable(DATA_TAG, model);
        outState.putInt(PAGE_NO, page_no);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isConnected && model.size() == 0) {
            handler.post(job);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        imageVolley = new FetchImageVolley(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        isConnected = detectConnection(context);
        handler = makeHandler();
        layoutManager = new GridLayoutManager(getContext(), 1);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {

            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                page_no=page;
                imageVolley.loadDataUsingVolley(Constants.PER_PAGE, page_no, order_By);
                if (progressBar != null)
                    progressBar.setVisibility(View.VISIBLE);
            }
        };
        if (savedInstanceState != null) {
            model = (ArrayList<DataModel>) savedInstanceState.getSerializable(DATA_TAG);
            order_By = savedInstanceState.getString(CHOICE_TAG);
            page_no = savedInstanceState.getInt(PAGE_NO);
            scrollListener.setStateAfterConfigChange(page_no,model.size());
        } else {
            model = new ArrayList<>();
            order_By = "latest";
            page_no = 1;
            imageVolley.loadDataUsingVolley(Constants.PER_PAGE, page_no, order_By);
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
        }
        imageAdapter = new ImageAdapter(getContext(), model);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * This method will only be launched when our model has no data in it, and there is no internet, in such a case we check it regularly and if connection is present, we work from the start
     */
    private void swapViews() {
        if (detectConnection(getContext()) == false) {
            recyclerView.setVisibility(View.INVISIBLE);
            no_internet_container.setVisibility(View.VISIBLE);
        } else {
            shouldHandlerRunAgain = false;
            handler.removeCallbacks(job, null);
            handler = null;
            recyclerView.setVisibility(View.VISIBLE);
            no_internet_container.setVisibility(View.INVISIBLE);
                order_By = "latest";
            page_no = 1;
            model.clear();
            imageVolley.loadDataUsingVolley(Constants.PER_PAGE, page_no, order_By);
            if (progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
        }
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, final Bundle savedInstanceState) {

        this.savedInstanceState = savedInstanceState;
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_dialog);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        no_internet_container = (FrameLayout) view.findViewById(R.id.no_internet_container);
        recyclerView.setAdapter(imageAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(scrollListener);
        if (isConnected) {
            recyclerView.setVisibility(View.VISIBLE);
            no_internet_container.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            no_internet_container.setVisibility(View.VISIBLE);
        }


        return view;
    }



    /**
     * marks a new network call to Unsplash API
     * Thus, set model array list to 0, to start fresh.
     * @param order_by
     */
    @Override
    public void onDialogFinish(String order_by) {
        imageAdapter.clearDataSet();
        scrollListener.resetState();
        imageVolley.cancelRequest(order_By);
        order_By = order_by;
        page_no = 1;
        imageVolley.loadDataUsingVolley(Constants.PER_PAGE, page_no, order_By);
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }


    public void addNewData(ArrayList<DataModel> incomingmodel) {
        if (imageAdapter != null) {
            imageAdapter.addNewData(incomingmodel);
        } else {
            Toast.makeText(context, R.string.loading_in_a_bit, Toast.LENGTH_SHORT).show();
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
