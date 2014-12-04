package com.nightwind.tcfl.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.ChatMsgEntity;
import com.nightwind.tcfl.tool.Constants;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private List<ChatMsgEntity> mData;

    private Context mContext;

    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public ChatAdapter(Context mContext, List<ChatMsgEntity> data) {
        this.mContext = mContext;
        this.mData = data;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View left;
        View right;

        ImageView userheadLeft;
        TextView chatcontentLeft;
        TextView timeLeft;

        ImageView userheadRight;
        TextView chatcontentRight;
        TextView timeRight;

        public ViewHolder(View itemView) {
            super(itemView);
            left = ((ViewGroup)itemView).getChildAt(0);
            userheadLeft = (ImageView) left.findViewById(R.id.iv_userhead);
            chatcontentLeft = (TextView) left.findViewById(R.id.tv_chatcontent);
            timeLeft = (TextView) left.findViewById(R.id.tv_sendtime);

            right = ((ViewGroup)itemView).getChildAt(1);
            userheadRight = (ImageView) right.findViewById(R.id.iv_userhead);
            chatcontentRight = (TextView) right.findViewById(R.id.tv_chatcontent);
            timeRight = (TextView) right.findViewById(R.id.tv_sendtime);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View left = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
        View right = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
        LinearLayout v = new LinearLayout(mContext);
        v.addView(left);
        v.addView(right);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    static public Date strToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMsgEntity data = mData.get(position);
        boolean near = false;
        if (position > 0) {
            ChatMsgEntity lastData = mData.get(position-1);
            Date last = strToDate(lastData.getDate());
            Date crt = strToDate(data.getDate());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(last);
            calendar.add(Calendar.MINUTE, 3);
            if(calendar.getTime().getTime() > crt.getTime()) {
                near = true;
            }
        }
        if (!data.isComMeg()) {
            holder.left.setVisibility(View.GONE);
            holder.right.setVisibility(View.VISIBLE);
            holder.chatcontentRight.setText(data.getText());
            if (near) {
                holder.timeRight.setVisibility(View.GONE);
            }
            holder.timeRight.setText(data.getDate());
            //从服务器加载图片
            imageLoader.displayImage(Constants.getImgURLList()[0], holder.userheadRight, options);
        } else {
            holder.left.setVisibility(View.VISIBLE);
            holder.right.setVisibility(View.GONE);
            holder.chatcontentLeft.setText(data.getText());
            if (near) {
                holder.timeLeft.setVisibility(View.GONE);
            }
            holder.timeLeft.setText(data.getDate());
            //从服务器加载图片
            imageLoader.displayImage(Constants.getImgURLList()[1], holder.userheadLeft, options);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}