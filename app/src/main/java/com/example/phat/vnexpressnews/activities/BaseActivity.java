package com.example.phat.vnexpressnews.activities;

import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.phat.vnexpressnews.Config;
import com.example.phat.vnexpressnews.R;
import com.example.phat.vnexpressnews.model.Category;
import com.example.phat.vnexpressnews.util.NavDrawerUtils;

import java.util.List;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGD;
import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * A base activity that handles common functionality in the app.
 */
public abstract class BaseActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = makeLogTag(BaseActivity.class);

    public static final String CATEGORY_LIST_KEY =
            Config.PACKAGE_NAME + ".activities.CATEGORY_LIST_KEY";

    protected List<Category> mCategoryList;
    protected DrawerLayout mDrawerLayout;
    protected NavigationView mNavView;
    protected Toolbar mToolbar;
    // Indicates this activity should has a nav drawer or not
    protected boolean hasNavDrawer = false;

    protected boolean isLoggedIn = false;

    protected static final int MENU_GROUP_ID_CATEGORY = 100;
    protected static final int MENU_GROUP_ID_SETTINGS = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null && mCategoryList == null) {
            mCategoryList = extras.getParcelableArrayList(CATEGORY_LIST_KEY);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (hasNavDrawer) {
            setupNavDrawer();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getToolbar();
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Sets up the navigation drawer if it's supported.
     */
    protected void setupNavDrawer() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (mDrawerLayout == null) {
            return;
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavView = (NavigationView) mDrawerLayout.findViewById(R.id.nav_view);
        if (mNavView == null) {
            return;
        }
        mNavView.setNavigationItemSelectedListener(this);
        mNavView.setItemIconTintList(null);

        // Build Navigation Menu
        buildNavMenu(mNavView.getMenu());
    }

    /**
     * Does this activity support navigation drawer?
     *
     * @param isSupport set to true if this activity supports navigation drawer. If it doesn't, set to false.
     */
    protected void shouldSupportNavDrawer(boolean isSupport) {
        hasNavDrawer = isSupport;
    }

    private void buildNavMenu(Menu navMenu) {

        if (navMenu == null) {
            return;
        }

        if (mCategoryList == null || mCategoryList.isEmpty()) {
            return;
        }

        navMenu.clear();

        // Because we also want to create "home" menu item at first of navigation menu
        // So we create a "home" category and put it on first of navigation menu
        Category homeCategory = new Category(Config.DEFAULT_CATEGORY_ID_TOP_NEWS,
                getString(R.string.navdrawer_item_home));
        buildMenuItem(navMenu, homeCategory, 0); // third parameter indicates that it will be at first

        int index = 1; // Begin from 1, because 0 is always "home" menu item
        for(Category c : mCategoryList) {
            buildMenuItem(navMenu, c, index);
            ++index;
        }

        navMenu.setGroupCheckable(MENU_GROUP_ID_CATEGORY, true, true);

        // Add Settings group to menu
        MenuItem item = navMenu.add(MENU_GROUP_ID_SETTINGS, Config.DEFAULT_CATEGORY_SETTINGS_ID,
                index, R.string.navdrawer_item_settings);
        item.setIcon(R.drawable.ic_settings);
        ++index;
        if (isLoggedIn) {
            item = navMenu.add(MENU_GROUP_ID_SETTINGS, Config.DEFAULT_CATEGORY_LOGOUT_ID,
                    index, R.string.navdrawer_item_logout);
            item.setIcon(R.drawable.ic_account_remove);
        } else {
            item = navMenu.add(MENU_GROUP_ID_SETTINGS, Config.DEFAULT_CATEGORY_LOGIN_ID,
                    index, R.string.navdrawer_item_login);
            item.setIcon(R.drawable.ic_account_plus);
        }
    }

    protected Toolbar getToolbar() {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null) {
                mToolbar.setNavigationContentDescription(R.string.navdrawer_description);
                setSupportActionBar(mToolbar);
            }
        }
        return mToolbar;
    }

    private void buildMenuItem(Menu navMenu, Category c, int order) {
        // Adds menu item to navigation menu
        MenuItem temp = navMenu.add(MENU_GROUP_ID_CATEGORY, c.getCategoryID(),
                c.getDisplayOrder(), c.getCategoryName());

        // Creates an icon and sets color to it
        GradientDrawable icon = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.nav_menu_item_icon);
        icon.mutate();
        icon.setColor(NavDrawerUtils.getPrimaryColor(this, order));

        // Sets menu item icon to the above icon.
        temp.setIcon(icon);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        // Close side navigation
        mDrawerLayout.closeDrawer(GravityCompat.START);

        Category c = getCategory(itemId);
        if (c != null) {
            LOGI(TAG, "Category: " + c.getCategoryName() + " was selected.");
            onCategoryItemSelected(c.getCategoryID());
            return true;
        }

        switch (itemId) {
            case Config.DEFAULT_CATEGORY_ID_TOP_NEWS:
                LOGI(TAG, "Home menu was selected.");
                onCategoryItemSelected(itemId);
                break;
            case Config.DEFAULT_CATEGORY_SETTINGS_ID:
                LOGI(TAG, "Settings menu was selected.");
                // TODO start settings screen
                break;
            case Config.DEFAULT_CATEGORY_LOGIN_ID:
                LOGI(TAG, "Login menu was selected.");
                // TODO log account out
                break;
            case Config.DEFAULT_CATEGORY_LOGOUT_ID:
                LOGI(TAG, "Logout menu was selected.");
                // TODO display login screen
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Sub class will implements this method to handler when user clicks a category item on navigation
     * in its own way. We also can create a common way to handle this event, but that act will makes this method
     * become complex and mess.
     * Has many cases, contexts and situations when this event happens. So let sub class implement this method
     * and do in its own way
     *
     * @param categoryId The category id which user was selected.
     */
    protected abstract void onCategoryItemSelected(int categoryId);

    protected Category getCategory(int categoryId) {
        if (mCategoryList == null || mCategoryList.isEmpty()) {
            return null;
        }

        for(Category c : mCategoryList) {
            if (c.getCategoryID() == categoryId) {
                return c;
            }
        }

        return null;
    }

    /**
     * Sets up the given navigation item's appearance to the selected state.
     */
    protected void setSelectedNavItem(int itemId) {

        if (!hasNavDrawer || mNavView == null) {
            return;
        }

        MenuItem item = mNavView.getMenu().findItem(itemId);
        item.setChecked(true);
        LOGI(TAG, item.getTitle() + " was checked");
    }
}
