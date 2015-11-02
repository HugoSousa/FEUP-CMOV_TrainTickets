package feup.cmov;

import org.json.JSONObject;

/**
 * Created by Hugo on 28/10/2015.
 */
public interface OnApiRequestCompleted {
    void onTaskCompleted(JSONObject result, ApiRequest.requestCode requestCode);
}
