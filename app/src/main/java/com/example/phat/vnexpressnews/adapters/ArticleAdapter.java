package com.example.phat.vnexpressnews.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.phat.vnexpressnews.R;
import com.example.phat.vnexpressnews.model.Article;
import com.example.phat.vnexpressnews.model.ExtractParagraphModule;
import com.example.phat.vnexpressnews.model.Module;
import com.example.phat.vnexpressnews.model.ParagraphModule;
import com.example.phat.vnexpressnews.model.PhotoModule;
import com.example.phat.vnexpressnews.util.DateTimeUtils;
import com.example.phat.vnexpressnews.util.ImageLoader;
import com.example.phat.vnexpressnews.util.TextUtils;

import java.util.List;

import static com.example.phat.vnexpressnews.util.LogUtils.makeLogTag;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private static final String TAG = makeLogTag(ArticleAdapter.class);

    private Context mContext;

    private Article mArticle;
    private List<Module> mModuleList;
    private int mArticleType;

    private ImageLoader mImageLoader;

    /** View Type */
    private static final int PARAGRAPH_TYPE = 0;
    private static final int EXTRACT_PARAGRAPH_TYPE = 1;
    private static final int PHOTO_TYPE = 2;
    private static final int INFOGRAPHIC_TYPE = 3;
    private static final int VIDEO_TYPE = 4;

    /** This is special type, it is a header which will displays category name, publish date, title, lead */
    private static final int ARTICLE_HEADER_TYPE = 5;

    public ArticleAdapter(Context context, List<Module> modules, int articleType, Article article) {
        mContext = context;
        mModuleList = modules;
        mArticleType = articleType;
        mArticle = article;
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        int layoutId;

        switch (viewType) {
            case PARAGRAPH_TYPE:
                layoutId = R.layout.paragraph_module;
                break;
            case EXTRACT_PARAGRAPH_TYPE:
                layoutId = R.layout.extract_paragraph_module;
                break;
            case PHOTO_TYPE:
                layoutId = R.layout.photo_module;
                break;
            case INFOGRAPHIC_TYPE:
                layoutId = R.layout.infographic_module;
                break;
            case ARTICLE_HEADER_TYPE:
                layoutId = R.layout.detail_article_header;
                break;
            default:
                throw new UnsupportedOperationException("Can not recognize view type");
        }

        v = LayoutInflater.from(mContext).inflate(layoutId, parent, false);

        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewType = holder.viewType;

        if (viewType == ARTICLE_HEADER_TYPE) {
            // Sets data to views
            holder.categoryName.setText(mArticle.getCategoryParent() != null ?
                    mArticle.getCategoryParent().getCategoryName() : "");
            holder.publishTime.setText(DateTimeUtils.format(mArticle.getPublishTime()));
            holder.articleTitle.setText(mArticle.getTitle());
            holder.articleLead.setText(mArticle.getLead());

            // Set animation for articleTitle and articleLead
            Animation showUpAnimation = AnimationUtils.loadAnimation(mContext, R.anim.textview_showup_animation);
            holder.articleTitle.startAnimation(showUpAnimation);
            holder.articleLead.startAnimation(showUpAnimation);
            return;
        }

        Module module = mModuleList.get(position - 1);
        switch (viewType) {
            case PARAGRAPH_TYPE:
                Spanned paragraph = TextUtils.trimTrailingWhiteSpace(
                        Html.fromHtml(((ParagraphModule) module).getParagraph()));
                holder.paragraph.setText(paragraph);
                break;
            case EXTRACT_PARAGRAPH_TYPE:
                Spanned extractParagraph = TextUtils.trimTrailingWhiteSpace(
                        Html.fromHtml(((ExtractParagraphModule) module).getParagraph()));
                holder.extractParagraph.setText(extractParagraph);
                break;
            case PHOTO_TYPE:
                PhotoModule photoModule = (PhotoModule) module;
                mImageLoader.loadImage(photoModule.getPhotoUrl(), holder.photo);
                if (TextUtils.isEmpty(photoModule.getPhotoCaption())) {
                    holder.photoCaption.setVisibility(View.GONE);
                } else {
                    Spanned photoCaption = TextUtils.trimTrailingWhiteSpace(
                            Html.fromHtml(photoModule.getPhotoCaption()));
                    holder.photoCaption.setText(photoCaption);
                }
                break;
            case INFOGRAPHIC_TYPE:
                PhotoModule infographicModule = (PhotoModule) module;
                mImageLoader.loadImage(infographicModule.getPhotoUrl(), holder.infographicPhoto);
                if (TextUtils.isEmpty(infographicModule.getPhotoCaption())) {
                    holder.infographicCaption.setVisibility(View.GONE);
                } else {
                    Spanned infographicCaption = TextUtils.trimTrailingWhiteSpace(
                            Html.fromHtml(infographicModule.getPhotoCaption()));
                    holder.infographicCaption.setText(infographicCaption);
                }
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        // First position always for header
        if (position == 0) {
            return ARTICLE_HEADER_TYPE;
        }

        final int moduleType = mModuleList.get(position - 1).getModuleType();

        switch (moduleType) {
            case Module.PARAGRAPH_TYPE:
                return PARAGRAPH_TYPE;
            case Module.EXTRACT_PARAGRAPH_TYPE:
                return EXTRACT_PARAGRAPH_TYPE;
            case Module.PHOTO_TYPE:
                return mArticleType == Article.ARTICLE_TYPE_INFOGRAPHIC ? INFOGRAPHIC_TYPE : PHOTO_TYPE;
            default:
                throw new UnsupportedOperationException("Can not get item view type");
        }
    }

    public void setArticle(Article article) {
        mArticle = article;
    }

    @Override
    public int getItemCount() {
        if (mArticle == null) {
            return 0;
        }

        if (mModuleList == null) {
            return 1;
        }

        return mModuleList.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        int viewType;

        /** Those views are used for {@code ARTICLE_HEADER_TYPE} */
        TextView categoryName;
        TextView publishTime;
        TextView articleTitle;
        TextView articleLead;

        /** Those views are used for {@code PHOTO_TYPE} */
        ImageView photo;
        TextView photoCaption;

        /** Those views are used for {@code INFOGRAPHIC_TYPE} */
        ImageView infographicPhoto;
        TextView infographicCaption;

        /** Those views are used for {@code PARAGRAPH_TYPE} */
        TextView paragraph;

        /** Those views are used for {@code EXTRACT_PARAGRAPH_TYPE} */
        TextView extractParagraph;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            this.viewType = viewType;

            switch (viewType) {
                case PARAGRAPH_TYPE:
                    paragraph = (TextView) itemView.findViewById(R.id.paragraph);
                    break;
                case EXTRACT_PARAGRAPH_TYPE:
                    extractParagraph = (TextView) itemView.findViewById(R.id.extract_paragraph);
                    break;
                case PHOTO_TYPE:
                    photo = (ImageView) itemView.findViewById(R.id.photo);
                    photoCaption = (TextView) itemView.findViewById(R.id.photo_caption);
                    break;
                case INFOGRAPHIC_TYPE:
                    infographicPhoto = (ImageView) itemView.findViewById(R.id.infographic_photo);
                    infographicCaption = (TextView) itemView.findViewById(R.id.infographic_caption);
                    break;
                case ARTICLE_HEADER_TYPE:
                    categoryName = (TextView) itemView.findViewById(R.id.category_name);
                    publishTime = (TextView) itemView.findViewById(R.id.publish_time);
                    articleTitle = (TextView) itemView.findViewById(R.id.article_title);
                    articleLead = (TextView) itemView.findViewById(R.id.article_lead);
                    break;
            }
        }
    }
}
