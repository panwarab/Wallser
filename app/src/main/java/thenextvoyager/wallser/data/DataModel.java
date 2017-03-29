package thenextvoyager.wallser.data;

import java.io.Serializable;

/**
 * Created by Abhiroj on 3/6/2017.
 */

public class DataModel implements Serializable {

    public String imageURL;
    public String downloadURL;
    public String name;

    public DataModel(String imageURL, String downloadURL, String id) {
        this.imageURL = imageURL;
        this.downloadURL = downloadURL;
        name = id;
    }
}
