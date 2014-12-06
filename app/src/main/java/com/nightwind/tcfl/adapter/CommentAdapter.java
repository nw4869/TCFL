package com.nightwind.tcfl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
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
import com.nightwind.tcfl.bean.ArticleEntity;
import com.nightwind.tcfl.bean.CommentEntity;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.tool.Dummy;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by wind on 2014/11/28.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private static Context mContext;

    private ArticleEntity mArticleEntity;
    private ArrayList<CommentEntity> mCommentEntities;

    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public CommentAdapter(Context context, ArticleEntity articleEntity) {
        mContext = context;
        mArticleEntity = articleEntity;
        mCommentEntities = articleEntity.getCommentEntities();
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
        public TextView TvContent;
        public ImageView imageView1;

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
            TvContent = (TextView) itemView.findViewById(R.id.comment);
            imageView1 = (ImageView) itemView.findViewById(R.id.imageView);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.id = position;

        if (position == 0) {
            holder.authItem.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);
            holder.commentItem.setVisibility(View.GONE);

            holder.title0.setText(mArticleEntity.getTitle());
            holder.username0.setText(mArticleEntity.getUsername());
            holder.dateTime0.setText(mArticleEntity.getDateTime());
            //从服务器加载图片
//            imageLoader.displayImage(Dummy.getImgURLList()[position % 8], holder.imageView1, options);
            User user = Dummy.getUser(mArticleEntity.getUsername());
            if (user != null) {
                imageLoader.displayImage(user.getAvaterUrl(), holder.imageView0, options);
                holder.imageView0.setOnClickListener(new AvatarOnClickListener(mContext, user.getUsername()));
            }

            //test:ContentLayout
            TextView tvContent = new TextView(mContext);
            tvContent.setText(mArticleEntity.getContent());
            tvContent.setTextColor(Color.BLACK);
            tvContent.setTextSize(16);
            holder.contentLayout.removeAllViews();
            holder.contentLayout.addView(tvContent);

        } else {
            holder.authItem.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
            holder.commentItem.setVisibility(View.VISIBLE);

            holder.TvUsername.setText(mCommentEntities.get(position).getUsername());
            holder.TvDateTime.setText(mCommentEntities.get(position).getDateTime());
            holder.TvContent.setText(mCommentEntities.get(position).getContent());
//        holder.imageView1.setImageBitmap(mCommentEntities[position].getImg());
            //从服务器加载图片
//            imageLoader.displayImage(Dummy.getImgURLList()[position%8], holder.imageView1, options);
            User user = Dummy.getUser(mCommentEntities.get(position).getUsername());
            if (user != null) {
                imageLoader.displayImage(user.getAvaterUrl(), holder.imageView1, options);
                holder.imageView1.setOnClickListener(new AvatarOnClickListener(mContext, user.getUsername()));
            }
        }

    }

    @Override
    public int getItemCount() {
        return mCommentEntities.size();
    }

}
