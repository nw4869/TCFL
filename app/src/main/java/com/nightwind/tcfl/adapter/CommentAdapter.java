package com.nightwind.tcfl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.CommentItem;
import com.nightwind.tcfl.bean.MyListItem;
import com.nightwind.tcfl.tool.Constants;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by wind on 2014/11/28.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private static Context mContext;

    private MyListItem mMyListItem;
    private CommentItem[] mCommentItems;

    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public CommentAdapter(Context context, MyListItem myListItem) {
        mContext = context;
        mMyListItem = myListItem;
        mCommentItems = myListItem.getCommentItems();
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
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            authItem = (RelativeLayout) itemView.findViewById(R.id.authItem);
            title0 = (TextView) itemView.findViewById(R.id.title0);
            username0 = (TextView) itemView.findViewById(R.id.username0);
            dateTime0 = (TextView) itemView.findViewById(R.id.datetime0);
            imageView0 = (ImageView) itemView.findViewById(R.id.head);
            contentLayout = (LinearLayout) itemView.findViewById(R.id.contentLayout0);
            divider = itemView.findViewById(R.id.divider);

            commentItem = (RelativeLayout) itemView.findViewById(R.id.commentItem);
            TvUsername = (TextView) itemView.findViewById(R.id.username);
            TvDateTime = (TextView) itemView.findViewById(R.id.datetime);
            TvContent = (TextView) itemView.findViewById(R.id.comment);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);

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

            holder.title0.setText(mMyListItem.getTitle());
            holder.username0.setText(mMyListItem.getUsername());
            holder.dateTime0.setText(mMyListItem.getDateTime());
            //从服务器加载图片
            imageLoader.displayImage(Constants.getImgURLList()[position % 8], holder.imageView, options);

            //test:ContentLayout
            TextView tvContent = new TextView(mContext);
            tvContent.setText(mMyListItem.getContent());
            holder.contentLayout.addView(tvContent);

        } else {
            holder.authItem.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
            holder.commentItem.setVisibility(View.VISIBLE);

            holder.TvUsername.setText(mCommentItems[position].getUsername());
            holder.TvDateTime.setText(mCommentItems[position].getDateTime());
            holder.TvContent.setText(mCommentItems[position].getContent());
//        holder.imageView.setImageBitmap(mCommentItems[position].getImg());
            //从服务器加载图片
            imageLoader.displayImage(Constants.getImgURLList()[position%8], holder.imageView, options);
        }

    }

    @Override
    public int getItemCount() {
        return mCommentItems.length;
    }

}
