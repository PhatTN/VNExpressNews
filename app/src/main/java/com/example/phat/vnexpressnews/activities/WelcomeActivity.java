package com.example.phat.vnexpressnews.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.example.phat.vnexpressnews.Config;
import com.example.phat.vnexpressnews.R;
import com.example.phat.vnexpressnews.io.CategoryHandler;
import com.example.phat.vnexpressnews.io.JSONHandler;
import com.example.phat.vnexpressnews.io.RequestQueueManager;
import com.example.phat.vnexpressnews.model.Category;
import com.example.phat.vnexpressnews.util.APIEndpointBuilder;
import com.example.phat.vnexpressnews.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGD;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGW;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * The app will always displays this activity when user enter the app.
 * We also use this activity to pre-fetch Category list
 */
public class WelcomeActivity extends AppCompatActivity
        implements Response.Listener<List<Category>>, Response.ErrorListener {
    private static final String TAG = makeLogTag(WelcomeActivity.class);

    RequestQueueManager mRequestQueueManager;
    private List<Category> mCategoryList;

    private static final String REQUEST_TAG_CATEGORY_lIST =
            Config.PACKAGE_NAME + ".activities.REQUEST_TAG_CATEGORY_LIST";

    // Indicates that will displays the splash screen at least 2 seconds long.
    private static final int MINIMUM_SPLASH_TIME_OUT = 2000;
    private static final int SOCKET_TIME_OUT = 5000;


    // Default category ID (indices must correspond to below)
    private static final int[] DEFAULT_CATEGORY_ID = new int[] {
            Config.DEFAULT_CATEGORY_ID_NEWS,
            Config.DEFAULT_CATEGORY_ID_THE_WORLD,
            Config.DEFAULT_CATEGORY_ID_PERSPECTIVE,
            Config.DEFAULT_CATEGORY_ID_BUSINESS,
            Config.DEFAULT_CATEGORY_ID_ENTERTAINMENT,
            Config.DEFAULT_CATEGORY_ID_SPORT,
            Config.DEFAULT_CATEGORY_ID_LAW,
            Config.DEFAULT_CATEGORY_ID_EDUCATION,
            Config.DEFAULT_CATEGORY_ID_HEALTH,
            Config.DEFAULT_CATEGORY_ID_FAMILY,
            Config.DEFAULT_CATEGORY_ID_TRAVEL,
            Config.DEFAULT_CATEGORY_ID_SCIENCE,
            Config.DEFAULT_CATEGORY_ID_DIGITIZATION,
            Config.DEFAULT_CATEGORY_ID_VEHICLE,
            Config.DEFAULT_CATEGORY_ID_COMMUNITY,
            Config.DEFAULT_CATEGORY_ID_CONFIDENCE,
            Config.DEFAULT_CATEGORY_ID_VIDEO,
            Config.DEFAULT_CATEGORY_ID_FUNNY
    };

    // Default category name (indices must correspond the above)
    private static final int[] DEFAULT_CATEGORY_NAME = new int[] {
            R.string.navdrawer_item_news,
            R.string.navdrawer_item_the_world,
            R.string.navdrawer_item_respective,
            R.string.navdrawer_item_business,
            R.string.navdrawer_item_entertainment,
            R.string.navdrawer_item_sport,
            R.string.navdrawer_item_law,
            R.string.navdrawer_item_education,
            R.string.navdrawer_item_health,
            R.string.navdrawer_item_family,
            R.string.navdrawer_item_travel,
            R.string.navdrawer_item_science,
            R.string.navdrawer_item_digitization,
            R.string.navdrawer_item_vehicle,
            R.string.navdrawer_item_community,
            R.string.navdrawer_item_confidence,
            R.string.navdrawer_item_video,
            R.string.navdrawer_item_funny
    };

    // Is Category List Data fetched into mCategoryList property?
    private boolean isCategoriesDataFetched = false;
    // Indicates that the splash screen timeout is over or not
    private boolean isTimeoutOver = false;
    // This flag indicates that we can start BrowseNewsActivity or not
    private boolean ableStartBrowseNewsActivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mRequestQueueManager = RequestQueueManager.getInstance(this);
        JSONHandler<List<Category>> jsonHandler = new CategoryHandler();
        APIEndpointBuilder endpointBuilder = new APIEndpointBuilder(APIEndpointBuilder.APIEndpointType.API_FOLDERS_LIST);

        RetryPolicy policy = new DefaultRetryPolicy(
                SOCKET_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        );

        mRequestQueueManager.getJson(endpointBuilder.build(), null, jsonHandler,
                REQUEST_TAG_CATEGORY_lIST, policy, this, this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isTimeoutOver = true;
                if (isCategoriesDataFetched) { // if Categories Data was fetched. Start BrowseNewsActivity
                    startBrowseNewsActivity();
                }
            }
        }, MINIMUM_SPLASH_TIME_OUT);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if (volleyError.networkResponse != null) {
            LOGD(TAG, "Can not load category list from server. " +
                    "Status code: " + volleyError.networkResponse.statusCode +
                    ". NetworkTimeMs: " + volleyError.getNetworkTimeMs());
        } else {
            LOGD(TAG, "Can not load category list from server. Network response is null.");
        }

        if (!NetworkUtils.hasNetworkConnection(this)) {
            LOGW(TAG, "Can not connect to internet. Network connection is not available.");
            Toast.makeText(this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
        }

        // We create default category list
        createDefaultCategoryList();

        startBrowseNewsActivity();
    }

    @Override
    public void onResponse(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            createDefaultCategoryList();
        } else {
            mCategoryList = categories;
            isCategoriesDataFetched = true;
            LOGI(TAG, "Fetches category list successful. Number of categories: " + mCategoryList.size());
        }

        startBrowseNewsActivity();
    }

    private void startBrowseNewsActivity() {

        // Start BrowseNewsActivity only when categories data was fetched and splash screen time out was over
        if (!isCategoriesDataFetched || !isTimeoutOver) {
            return;
        }

        if (!ableStartBrowseNewsActivity) {
            return;
        }

        Intent intent = new Intent(this, BrowseNewsActivity.class);

        // Passes category list to BrowseNewsActivity
        intent.putExtra(BaseActivity.CATEGORY_LIST_KEY, new ArrayList<>(mCategoryList));

        LOGI(TAG, "Start BrowseNewsActivity with category list");
        startActivity(intent);

        ableStartBrowseNewsActivity = false;

        // Close this activity
        finish();
    }

    private void createDefaultCategoryList() {
        mCategoryList = new ArrayList<>(DEFAULT_CATEGORY_ID.length);

        for (int i = 0; i < DEFAULT_CATEGORY_ID.length; i++) {
            mCategoryList.add(new Category(DEFAULT_CATEGORY_ID[i],
                    getString(DEFAULT_CATEGORY_NAME[i])));
        }

        isCategoriesDataFetched = true;

        LOGI(TAG, "Creates default category list successful. Number of categories: " + mCategoryList.size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRequestQueueManager != null) {
            mRequestQueueManager.cancelPendingRequest(REQUEST_TAG_CATEGORY_lIST);
        }
    }
}
