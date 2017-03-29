package thenextvoyager.wallser.callback;

import java.util.ArrayList;

import thenextvoyager.wallser.Data.DataModel;

/**
 * Created by Abhiroj on 3/29/2017.
 */

public interface OnResultFetchedCallback {

    void getData(ArrayList<DataModel> model);
}
