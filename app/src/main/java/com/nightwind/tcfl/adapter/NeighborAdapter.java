package com.nightwind.tcfl.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.activity.NeighborActivity;
import com.nightwind.tcfl.activity.ProfileActivity;
import com.nightwind.tcfl.bean.Neighbor;
import com.nightwind.tcfl.tool.BaseTools;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by wind on 2015/1/12.
 */
public class NeighborAdapter extends RecyclerView.Adapter<NeighborAdapter.ViewHolder> {
    private final Context mContext;
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    private OnItemClickListener mOnItemClickListener;
    private final LayoutInflater mInflater;
    private final List<Neighbor> mNeighborList;

    public NeighborAdapter(Context context, List<Neighbor> neighborListList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mNeighborList = neighborListList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.neighbor_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Neighbor neighbor = mNeighborList.get(position);
        holder.username.setText(neighbor.getUsername());
        holder.sign.setText(neighbor.getSign());
//        double distance = DistanceUtil.getDistance(mLatLng, new LatLng(neighbor.getLatitude(), neighbor.getLongitude()));
        double distance = neighbor.getDistance();
        String strDistance = String.format("%.2f km", distance/1000);
        holder.distance.setText(strDistance);
        holder.diffTime.setText(BaseTools.getDisplayTime(neighbor.getTime()));
        imageLoader.displayImage(neighbor.getAvatarUrl(), holder.avatar, options);
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra(ProfileActivity.ARG_USERNAME, mNeighborList.get(position).getUsername());
                mContext.startActivity(intent);
                ((NeighborActivity)mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });

        if (mOnItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mNeighborList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatar;
        private final TextView username;
        private final TextView sign;
        private final TextView distance;
        private final TextView diffTime;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            username = (TextView) itemView.findViewById(R.id.username);
            sign = (TextView) itemView.findViewById(R.id.sign);
            distance = (TextView) itemView.findViewById(R.id.distance);
            diffTime = (TextView) itemView.findViewById(R.id.diffTime);
        }
    }


    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
