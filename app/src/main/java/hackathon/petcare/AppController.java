package hackathon.petcare;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by pradhumanswami on 10/1/16.
 */

public class AppController {

    public static final String TAG = AppController.class
            .getSimpleName();

    private ImageLoader mImageLoader;

    private static AppController mInstance;
    private static Context mContext;
    private static Handler handler;
    private RequestQueue mRequestQueue;
    private static EventBus eventBus;

    public static AppController getInstance(Context context) {
        mContext = context;
        eventBus = EventBus.getDefault();
        handler = new Handler(Looper.getMainLooper());

        if (mInstance == null) {
            return new AppController();
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void getUserLocation(String message) {

        Task task = new Task() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                eventBus.post(new CobbocEvent(CobbocEvent.USER_OWN_LOCATION, true,jsonObject));
            }

            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onError(Throwable throwable) {
                eventBus.post(new CobbocEvent(CobbocEvent.USER_OWN_LOCATION, false));
            }

            @Override
            public void onProgress(int percent) {

            }
        };
        postFetch(message, null, task, Request.Method.POST);
    }

    private void postFetch(final String url, final Map<String, String> requestParams, final Task task, final int method) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = new JSONObject(String.valueOf(response));
                            if (object.has("error")) {
                            } else {
                                runSuccessOnHandlerThread(task, object);
                            }
                        } catch (JSONException e) {
                            // maybe this is a string?
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // hide the progress dialog

            }
        });
        addToRequestQueue(jsonObjReq, TAG);
    }

    private void runSuccessOnHandlerThread(final Task task, final JSONObject jsonObject) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                task.onSuccess(jsonObject);
            }
        });
    }

    private void runSuccessOnHandlerThread(final Task task, final String response) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                task.onSuccess(response);
            }
        });
    }

}