package com.nightwind.tcfl.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nightwind.tcfl.AvatarOnClickListener;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.activity.AddCommentActivity;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.bean.Comment;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wind on 2014/11/28.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private static Context mContext;

    private Article mArticle;
    private List<Comment> mCommentEntities;
//    private int mClassify;
//    private int mRowId;
    private int mArticleId;

    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    DisplayImageOptions ImageOptions = Options.getArticleImageOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public CommentAdapter(Context context, Article article/*, int classify, int rowId, int articleId*/) {
        mContext = context;
        mArticle = article;
        mCommentEntities = article.getCommentEntities();
//        mClassify = classify;
//        mRowId = rowId;
//        mArticleId = articleId;
        mArticleId = mArticle.getId();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public int id;

        //内容：id=0
        public RelativeLayout authItem;
        public TextView title0;
        public TextView username0;
        public TextView dateTime0;
        public ImageView imageView0;
        public LinearLayout contentLayout;
        public View divider;

        public RelativeLayout commentItem;
        public TextView TvUsername;
        public TextView TvDateTime;
        public TextView TvReplySome;
        public TextView TvContent;
        public ImageView imageView1;
        public ImageView commentIcon;

        public ViewHolder(View itemView) {
            super(itemView);

            authItem = (RelativeLayout) itemView.findViewById(R.id.authItem);
            title0 = (TextView) itemView.findViewById(R.id.title0);
            username0 = (TextView) itemView.findViewById(R.id.username0);
            dateTime0 = (TextView) itemView.findViewById(R.id.datetime0);
            imageView0 = (ImageView) itemView.findViewById(R.id.avatar);
            contentLayout = (LinearLayout) itemView.findViewById(R.id.contentLayout0);
            divider = itemView.findViewById(R.id.divider);

            commentItem = (RelativeLayout) itemView.findViewById(R.id.commentItem);
            TvUsername = (TextView) itemView.findViewById(R.id.username);
            TvDateTime = (TextView) itemView.findViewById(R.id.datetime);
            TvReplySome = (TextView) itemView.findViewById(R.id.replySomeone);
            TvContent = (TextView) itemView.findViewById(R.id.comment);
            imageView1 = (ImageView) itemView.findViewById(R.id.avatar);
            commentIcon = (ImageView) itemView.findViewById(R.id.commentIcon);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.id = position;

        if (position == 0) {
            holder.authItem.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);
            holder.commentItem.setVisibility(View.GONE);

            holder.title0.setText(mArticle.getTitle());
            holder.username0.setText(mArticle.getUsername());
            holder.dateTime0.setText(mArticle.getDate());
            //从服务器加载图片
//            imageLoader.displayImage(Dummy.getImgURLList()[position % 8], holder.imageView1, options);
//            User user = mUserController.getUser(mArticle.getUsername());
//            if (user != null) {
//                imageLoader.displayImage(user.getAvatarUrl(), holder.imageView0, options);
//                holder.imageView0.setOnClickListener(new AvatarOnClickListener(mContext, user.getUsername()));
//            }
                imageLoader.displayImage(mArticle.getAvatarUrl(), holder.imageView0, options);
                holder.imageView0.setOnClickListener(new AvatarOnClickListener(mContext, mArticle.getUsername()));

            //test:ContentLayout
            holder.contentLayout.removeAllViews();

            String content = mArticle.getContent();
            if (content != null) {
                final String strPattern = "<img src=\".{1,100}?\"/>";
                String[] texts = content.split(strPattern);
                Pattern pattern = Pattern.compile(strPattern);
                Matcher matcher = pattern.matcher(content);

                for (String text : texts) {
                    TextView tvContent = new TextView(mContext);
                    tvContent.setTextIsSelectable(true);
                    tvContent.setText(text);
                    tvContent.setTextColor(Color.BLACK);
                    tvContent.setTextSize(16);
                    holder.contentLayout.addView(tvContent);

                    if (matcher.find()) {
                        ImageView iv = new ImageView(mContext);
                        String imageTag = matcher.group();
                        String url = imageTag.substring(10, imageTag.length() - 3);
                        imageLoader.displayImage(url, iv, ImageOptions, new SimpleImageLoadingListener(), new ImageLoadingProgressListener() {
                            @Override
                            public void onProgressUpdate(String imageUri, View view, int current, int total) {

                            }
                        });
                        holder.contentLayout.addView(iv);
                    }
                }

                while (matcher.find()) {
                    ImageView iv = new ImageView(mContext);
                    String imageTag = matcher.group();
                    String url = imageTag.substring(10, imageTag.length() - 3);
                    imageLoader.displayImage(url, iv, ImageOptions);
                    holder.contentLayout.addView(iv);
                }
            }


        } else {
            holder.authItem.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
            holder.commentItem.setVisibility(View.VISIBLE);

            Comment comment = mCommentEntities.get(position);

            //是否回复某个评论
            if (comment.getParentId() != -1) {
                holder.TvReplySome.setVisibility(View.VISIBLE);
                Comment parentComment = mCommentEntities.get(comment.getParentId());
                String parentContent = "回复" + parentComment.getUsername() + ": " + parentComment.getContent();
                holder.TvReplySome.setText(parentContent);
            } else {
                holder.TvReplySome.setVisibility(View.GONE);
            }

            holder.TvUsername.setText(comment.getUsername());
            holder.TvDateTime.setText(comment.getDateTime());
            holder.TvContent.setText(comment.getContent());
//        holder.imageView1.setImageBitmap(mCommentEntities[position].getImg());
            //从服务器加载图片
//            imageLoader.displayImage(Dummy.getImgURLList()[position%8], holder.imageView1, options);
//            User user = mUserController.getUser(comment.getUsername());
//            if (user != null) {
//                imageLoader.displayImage(user.getAvatarUrl(), holder.imageView1, options);
//                holder.imageView1.setOnClickListener(new AvatarOnClickListener(mContext, user.getUsername()));
//            }
            imageLoader.displayImage(comment.getAvatarUrl(), holder.imageView1, options);
            holder.imageView1.setOnClickListener(new AvatarOnClickListener(mContext, comment.getUsername()));

            //回复按钮
            holder.commentIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AddCommentActivity.class);
//                    intent.putExtra("classify", mClassify);
//                    intent.putExtra("rowId", mRowId);
                    intent.putExtra("articleId", mArticleId);
                    intent.putExtra("parentComment", position);
//                    MainActivity.this.startActivity(intent);
                    ((Activity)mContext).startActivityForResult(intent, position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mCommentEntities.size();
    }

}
