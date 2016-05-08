package com.phattn.vnexpressnews.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phattn.vnexpressnews.Config;
import com.phattn.vnexpressnews.R;
import com.phattn.vnexpressnews.model.BriefArticle;
import com.phattn.vnexpressnews.util.ImageLoader;

import java.util.List;

import static com.phattn.vnexpressnews.util.LogUtils.LOGI;
import static com.phattn.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * This adapter binds {@link BriefArticle}s into views to display in RecyclerView
 */
public class BriefArticleAdapter extends RecyclerView.Adapter<BriefArticleAdapter.ViewHolder> {
    private static final String TAG = makeLogTag(BriefArticleAdapter.class);

    /** Item type */
    private static final int DEFAULT_ITEM_TYPE = 0;
    private static final int PERSPECTIVE_ITEM_TYPE = 1;
    private static final int VIDEO_ITEM_TYPE = 2;

    /** Data set */
    private List<BriefArticle> mBriefArticleList;

    private ImageLoader mImageLoader;
    private Context mContext;

    public BriefArticleAdapter(Context context, List<BriefArticle> dataSet) {
        mBriefArticleList = dataSet;
        mImageLoader = new ImageLoader(context);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType == PERSPECTIVE_ITEM_TYPE) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.perspective_item, parent, false);
        } else if (viewType == VIDEO_ITEM_TYPE) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.video_item, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.brief_article_item, parent, false);
        }

        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Handles PERSPECTIVE_ITEM_TYPE viewType
        if (holder.viewType == PERSPECTIVE_ITEM_TYPE) {
            RecyclerView.LayoutParams layoutParams =
                    (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
        }

        // Binds value to views
        BriefArticle item = mBriefArticleList.get(position);

        holder.title.setText(item.getTitle());
        mImageLoader.loadImage(mContext, item.getThumbnailUrl(), holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        if (mBriefArticleList == null) {
            return 0;
        }
        return mBriefArticleList.size();
    }

    @Override
    public int getItemViewType(int position) {
        BriefArticle item = mBriefArticleList.get(position);
        if (item.getOriginalCategory() == Config.DEFAULT_CATEGORY_ID_PERSPECTIVE) {
            return PERSPECTIVE_ITEM_TYPE;
        }

        if (item.getArticleType() == BriefArticle.ARTICLE_TYPE_VIDEO) {
            return VIDEO_ITEM_TYPE;
        } else {
            return DEFAULT_ITEM_TYPE;
        }
    }

    @Override
    public long getItemId(int position) {
        if (mBriefArticleList == null) {
            return -1;
        }

        return mBriefArticleList.get(position).getArticleId();
    }

    public BriefArticle getItem(int position) {
        if (mBriefArticleList == null) {
            return null;
        }

        return mBriefArticleList.get(position);
    }

    public void add(int position, BriefArticle item) {
        mBriefArticleList.add(position, item);
        notifyItemInserted(position);
        LOGI(TAG, "New item was add at " + position + ", item:" + item.getTitle());
    }

    public void remove(BriefArticle item) {
        int position = mBriefArticleList.indexOf(item);
        mBriefArticleList.remove(position);
        notifyItemRemoved(position);
        LOGI(TAG, "Remove item at " + position + ", item:" + item.getTitle());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final int viewType;

        public ImageView thumbnail;
        public TextView title;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            // Binds view to variables
            thumbnail = (ImageView) itemView.findViewById(R.id.brief_article_thumbnail);
            title = (TextView) itemView.findViewById(R.id.brief_article_title);
        }
    }
}
