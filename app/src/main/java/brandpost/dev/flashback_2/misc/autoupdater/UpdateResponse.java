package brandpost.dev.flashback_2.misc.autoupdater;

/**
 * Created by Viktor on 2015-10-06.
 */
public class UpdateResponse {

    public class Assets {
        String browser_download_url;
    }

    Assets[] assets = null;
    String tag_name = null;

    public UpdateResponse() {

    }

    @Override
    public String toString() {
        if(assets == null || tag_name == null) return "Nothing here.";
        return "Version: " + tag_name + "\n" + "Url: " + assets[0].browser_download_url;
    }
}
