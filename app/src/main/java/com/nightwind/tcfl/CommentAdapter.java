package com.nightwind.tcfl;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightwind.tcfl.bean.CommentItem;

/**
 * Created by wind on 2014/11/28.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private static Context mContext;

    private CommentItem[] mCommentItems;

    public CommentAdapter(Context context, CommentItem[] commentItems) {
        mContext = context;
        mCommentItems = commentItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public int id;
        public TextView TvUsername;
        public TextView TvDateTime;
        public TextView TvContent;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
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

        holder.TvUsername.setText(mCommentItems[position].getUsername());
        holder.TvDateTime.setText(mCommentItems[position].getDateTime());
        holder.TvContent.setText(mCommentItems[position].getContent());
        holder.imageView.setImageBitmap(mCommentItems[position].getImg());
    }

    @Override
    public int getItemCount() {
        return mCommentItems.length;
    }

}
