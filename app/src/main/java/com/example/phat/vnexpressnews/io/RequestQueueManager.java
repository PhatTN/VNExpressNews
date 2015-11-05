package com.example.phat.vnexpressnews.io;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;
import com.example.phat.vnexpressnews.util.URLUtils;
import com.squareup.okhttp.OkHttpClient;

import java.util.Map;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGD;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGE;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * This class uses singleton pattern, to avoids instantiating request queue
 * in each {@link android.app.Service} or {@link android.app.Activity}.
 */
public class RequestQueueManager {
    public static final String TAG = makeLogTag(RequestQueueManager.class);

    private static RequestQueueManager mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    public RequestQueueManager(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestQueueManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestQueueManager(context);
        }

        return mInstance;
    }

    /**
     * Returns a Volley request queue for creating network requests
     * @return {@link RequestQueue}
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext(),
                    new OkHttpStack(new OkHttpClient()));
        }

        return mRequestQueue;
    }

    /**
     * Adds a request to queue with a tag.
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);

        LOGD(TAG, "Add a request to request queue with tag: " + (TextUtils.isEmpty(tag) ? TAG : tag));
    }

    /**
     * Adds a request to queue. This request will uses default tag
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set default tag for request
        req.setTag(TAG);
        getRequestQueue().add(req);

        LOGD(TAG, "Add a request to request queue with default tag");
    }

    /**
     * Calls this method with a tag to cancel all pending request with that tag
     * @param tag  The tag is used to cancel requests with that tag
     */
    public void cancelPendingRequest(String tag) {
        if (mRequestQueue == null) {
            return;
        }

        LOGD(TAG, "Cancel all request from request queue by tag: " + (TextUtils.isEmpty(tag) ? TAG : tag));
        if (TextUtils.isEmpty(tag)) {
            mRequestQueue.cancelAll(TAG);
            return;
        }

        mRequestQueue.cancelAll(tag);
    }

    /**
     * Calls this method to cancel all pending request with default tag in queue.
     */
    public void cancelPendingRequest() {
        if (mRequestQueue != null) {
            LOGD(TAG, "Cancel all request with default tag from request queue");
            cancelPendingRequest(null);
        }
    }

    /**
     * This method helps creating a request to get JSON type quickly.
     *
     * @param url  The endpoint where we can get data
     * @param headers  The header of request
     * @param handler  The handler which will deserialize JSON result to appropriate type
     * @param tag  The tag which is added to request. Used to cancel the request
     * @param policy  The retry policy indicates time out, max number of retries, backoff multiplier
     * @param listener  The callback function which is called when requesting successful.
     * @param errorListener  The callback function which is called when meet any error
     * @param <T>  The type which the handler will deserialize JSON result into
     */
    public <T> void getJson(String url, Map<String, String> headers, JSONHandler<T> handler, String tag,
                            RetryPolicy policy, Response.Listener<T> listener, Response.ErrorListener errorListener) {

        if (!URLUtils.isValidURL(url)) {
            LOGE(TAG, "Invalid URL. URL: " + url);
            return;
        }

        if (handler == null) {
            LOGE(TAG, "The handler must be not null");
            throw new NullPointerException("The handler must be not null");
        }

        // Constructs a new request, and let it handle anything else :D
        JacksonRequest<T> jacksonRequest = new JacksonRequest<>(url, handler, headers, listener, errorListener);

        // Set retry policy
        if (policy != null) {
            jacksonRequest.setRetryPolicy(policy);
        }

        // Remember add the request to request queue
        addToRequestQueue(jacksonRequest, tag);

    }

    /**
     * This method helps creating a request to get JSON type quickly.
     *
     * @param url  The endpoint where we can get data
     * @param headers  The header of request
     * @param handler  The handler which will deserialize JSON result to appropriate type
     * @param tag  The tag which is added to request. Used to cancel the request
     * @param listener  The callback function which is called when requesting successful.
     * @param errorListener  The callback function which is called when meet any error
     * @param <T>  The type which the handler will deserialize JSON result into
     */
    public <T> void getJson(String url, Map<String, String> headers, JSONHandler<T> handler, String tag,
                            Response.Listener<T> listener, Response.ErrorListener errorListener) {
        getJson(url, headers, handler, tag, null, listener, errorListener);
    }
}
