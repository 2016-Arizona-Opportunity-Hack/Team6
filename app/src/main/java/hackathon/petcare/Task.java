package hackathon.petcare;

import org.json.JSONObject;

/**
 * Created by karan on 6/3/15.
 */
public interface Task {
    public void onSuccess(JSONObject object);

    public void onSuccess(String response);

    public void onError(Throwable throwable);

    void onProgress(int percent);
}




