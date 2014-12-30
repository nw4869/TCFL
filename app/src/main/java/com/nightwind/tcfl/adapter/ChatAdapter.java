package com.nightwind.tcfl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nightwind.tcfl.AvatarOnClickListener;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.ChatMsg;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.tool.BaseTools;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wind on 2014/12/4.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    //对方发来的信息
    public static int IMVT_COM_MSG = 0;
    //自己发出的信息
    public static int IMVT_TO_MSG = 1;
    private final User mUser1;
    private final User mUser2;

    private List<ChatMsg> mData;

    private Context mContext;

    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public ChatAdapter(Context mContext, List<ChatMsg> data, User user1, User user2) {
        this.mContext = mContext;
        this.mData = data;
        mUser1 = user1;
        mUser2 = user2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView content;
        TextView time;

        public ViewHolder(View itemView, int type) {
            super(itemView);

            avatar = (ImageView) itemView.findViewById(R.id.iv_userhead);
            content = (TextView) itemView.findViewById(R.id.tv_chatcontent);
            time = (TextView) itemView.findViewById(R.id.tv_sendtime);
        }
    }

    @Override
    public int getItemViewType(int position) {

        return mData.get(position).getComMeg();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        if (viewType == ChatMsg.MSG_IS_COMING) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
        }

        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ChatMsg data = mData.get(position);
        boolean near = false;
        if (position > 0) {
            ChatMsg lastData = mData.get(position-1);
            Date last = BaseTools.strToDate(lastData.getDate());
            Date crt = BaseTools.strToDate(data.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(last);
            calendar.add(Calendar.MINUTE, 3);
            if(calendar.getTime().getTime() > crt.getTime()) {
                near = true;
            }
        }
        holder.content.setText(data.getContent());
        holder.content.setTextIsSelectable(true);
        holder.content.setSelectAllOnFocus(true);
        if (near) {
            holder.time.setVisibility(View.GONE);
        }
        holder.time.setText(data.getDate());
        User user;
        if (holder.getItemViewType() == ChatMsg.MSG_IS_COMING) {
            user = mUser2;
        } else {
            user = mUser1;
        }
        //从服务器加载图片
        if (user != null) {
            imageLoader.displayImage(user.getAvatarUrl(), holder.avatar, options);
            holder.avatar.setOnClickListener(new AvatarOnClickListener(mContext, user.getUsername()));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
