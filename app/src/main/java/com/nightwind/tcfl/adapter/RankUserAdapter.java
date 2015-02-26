package com.nightwind.tcfl.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightwind.tcfl.AvatarOnClickListener;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.activity.BaseActivity;
import com.nightwind.tcfl.activity.ContestResultActivity;
import com.nightwind.tcfl.activity.NeighborActivity;
import com.nightwind.tcfl.activity.ProfileActivity;
import com.nightwind.tcfl.bean.RankUser;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by root on 2/26/15.
 */
public class RankUserAdapter extends RecyclerView.Adapter<RankUserAdapter.ViewHolder> {

    private final Context mContext;
    private final List<RankUser> mUsers;
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public RankUserAdapter(Context context, List<RankUser> users){
        mContext = context;
        mUsers = users;
    }

    @Override
    public RankUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.rank_user_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RankUserAdapter.ViewHolder holder, final int position) {
        final RankUser user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        holder.sign.setText(user.getSign());
        imageLoader.displayImage(user.getAvatarUrl(), holder.avatar, options);
        holder.rank.setText("第" + user.getRank() + "名");
        holder.cnt.setText(user.getCount() + "条");

        holder.avatar.setOnClickListener(new AvatarOnClickListener(mContext, user.getUsername()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra(ProfileActivity.ARG_USERNAME, user.getUsername());
                mContext.startActivity(intent);
                ((BaseActivity)mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView avatar;
        private final TextView username;
        private final TextView sign;
        private final TextView rank;
        private final TextView cnt;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            username = (TextView) itemView.findViewById(R.id.username);
            sign = (TextView) itemView.findViewById(R.id.sign);
            rank = (TextView) itemView.findViewById(R.id.rank);
            cnt = (TextView) itemView.findViewById(R.id.cnt);
        }
    }
}
