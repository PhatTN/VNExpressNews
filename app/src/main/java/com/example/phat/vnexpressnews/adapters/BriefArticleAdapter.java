package com.example.phat.vnexpressnews.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.phat.vnexpressnews.Config;
import com.example.phat.vnexpressnews.R;
import com.example.phat.vnexpressnews.model.BriefArticle;
import com.example.phat.vnexpressnews.util.ImageLoader;

import java.util.List;

import static com.example.phat.vnexpressnews.util.LogUtils.LOGI;
import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

/**
 * This adapter binds {@link BriefArticle}s into views to display in RecyclerView
 */
public class BriefArticleAdapter extends RecyclerView.Adapter<BriefArticleAdapter.ViewHolder> {
    private static final String TAG = makeLogTag(BriefArticleAdapter.class);

    /** Item type */
    private static final int FULL_SPAN_TYPE = 0;
    private static final int DEFAULT_SPAN_TYPE = 1;

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

        if (viewType == FULL_SPAN_TYPE) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.perspective_item, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.brief_article_item, parent, false);
        }

        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Handles FULL_SPAN_TYPE viewType
        if (holder.viewType == FULL_SPAN_TYPE) {
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
        int categoryId = mBriefArticleList.get(position).getOriginalCategory();
        if (categoryId == Config.DEFAULT_CATEGORY_ID_PERSPECTIVE) {
            return FULL_SPAN_TYPE;
        }

        return DEFAULT_SPAN_TYPE;
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
