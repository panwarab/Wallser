package thenextvoyager.wallser.data;

import java.io.Serializable;

/**
 * Created by Abhiroj on 3/6/2017.
 */

public class DataModel implements Serializable {

    public String imageURL;
    public String downloadURL;
    public String name;
    public String user_name;
    public String portfolio_url;
    public String profile_image;

    public DataModel(String imageURL, String downloadURL, String id, String user_name, String portfolio_url, String profile_image) {
        this.imageURL = imageURL;
        this.downloadURL = downloadURL;
        name = id;
        this.user_name = user_name;
        this.portfolio_url = portfolio_url;
        this.profile_image = profile_image;
    }
}
