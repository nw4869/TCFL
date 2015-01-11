package com.nightwind.tcfl.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by wind on 2015/1/7.
 */

public class AddImageAdapter extends RecyclerView.Adapter<AddImageAdapter.ViewHolder> {

    DisplayImageOptions options = Options.getSlideImageOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private final LayoutInflater mInflater;
    List<String> mImageUriList;
    private RecyclerView mRecyclerView;

    public AddImageAdapter(Context context, List<String> mImageUriList) {
        this.mInflater = LayoutInflater.from(context);
        this.mImageUriList = mImageUriList;
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void add(String imageUri) {
        mImageUriList.add(imageUri);
        notifyItemInserted(getItemCount() - 1);
        mRecyclerView.smoothScrollToPosition(getItemCount() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.add_image_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        imageLoader.displayImage(mImageUriList.get(position), holder.imageView, options);
        //如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRecyclerView = null;
    }

    @Override
    public int getItemCount() {
        return mImageUriList.size();
    }
}