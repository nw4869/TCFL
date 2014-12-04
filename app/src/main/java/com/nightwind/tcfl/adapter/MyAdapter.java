package com.nightwind.tcfl.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.activity.ContentActivity;
import com.nightwind.tcfl.bean.MyListItem;
import com.nightwind.tcfl.fragment.MyRecyclerFragment;
import com.nightwind.tcfl.tool.Constants;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements ViewPager.OnPageChangeListener {
    private static Context mContext;
    private final int mType;

    private MyListItem[] mListItems;

    /**
     * ViewPager
     */
    private ViewPager viewPager;

    /**
     * 装点点的ImageView数组
     */
    private ImageView[] tips;

    /**
     * 装ImageView数组
     */
    private ImageView[] mSlideImageViews;

    /**
     * 点View
     */
    private ViewGroup group;

    /**
     * 图片资源id
     */
    private int[] imgIdArray = { R.drawable.bg01, R.drawable.conan2, R.drawable.conan3, R.drawable.conan4 };
//            R.drawable.conan5, R.drawable.conan6, R.drawable.conan7, R.drawable.conan8 };


    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    DisplayImageOptions slideImageOptions = Options.getSlideImageOptions();
//    protected ImageLoader slideImageLoader = ImageLoader.getInstance();

	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	public static class ViewHolder extends RecyclerView.ViewHolder {
		// each data item is just a string in this case
        public View v;

        public int id;
        public LinearLayout mListItemLayout;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public TextView mTextView4;
        public ImageView mImageView;

        public FrameLayout mSlideImageLayout;
        public ViewPager mImageViewPager;
        public ViewGroup mPointsViewGroup;
        public View divider;

		public ViewHolder(View v) {
			super(v);
            this.v = v;
            mListItemLayout = (LinearLayout) v.findViewById(R.id.list_item);
            mTextView1 = (TextView) v.findViewById(R.id.textView1);
            mTextView2 = (TextView) v.findViewById(R.id.textView2);
            mTextView3 = (TextView) v.findViewById(R.id.textView3);
            mTextView4 = (TextView) v.findViewById(R.id.textView4);
            mImageView = (ImageView) v.findViewById(R.id.imageView);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(mContext, "onClick " + id, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, ContentActivity.class);
                    intent.putExtra("id", id);
                    mContext.startActivity(intent);
                    ((Activity)mContext).overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                }
            });

            mSlideImageLayout = (FrameLayout) v.findViewById(R.id.slideImage);
            mImageViewPager = (ViewPager) v.findViewById(R.id.viewPager);
            mPointsViewGroup = (ViewGroup) v.findViewById(R.id.viewGroup);
            divider = v.findViewById(R.id.divider);

        }
	}

    public int getType() {
        return mType;
    }

	// Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, MyListItem[] listItems, int type) {
        mContext = context;
        mListItems = listItems;
        mType = type;
    }

	// Create new views (invoked by the layout manager)
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// create a new view
		System.out.println("onCreateViewHolder");
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_item_view, parent, false);
		// set the view's size, margins, paddings and layout parameters
		ViewHolder vh = new ViewHolder(v);
		return vh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element
		System.out.println("onBindViewHolder");

        if (mType == MyRecyclerFragment.TYPE_WITH_SLIDE_IMAGE && position == 0) {
            holder.mListItemLayout.setVisibility(View.GONE);
            holder.mSlideImageLayout.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);

            viewPager = holder.mImageViewPager;
            group = holder.mPointsViewGroup;
            group.removeAllViews();

            //将点点加入到ViewGroup中
            tips = new ImageView[imgIdArray.length];
            for(int i=0; i<tips.length; i++){

                ImageView imageView = new ImageView(mContext);
                imageView.setLayoutParams(new LayoutParams(10, 10));
                tips[i] = imageView;
                if(i == 0){
                    tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
                }else{
                    tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                }

                //用LinearLayout包装一下实现padding
                LinearLayout linerLayout = new LinearLayout(mContext);
                LinearLayout.LayoutParams linerLayoutParames = new LinearLayout.LayoutParams(
                        10,
                        10,
                        1);
                linerLayout.setPadding(10, 0, 10, 0);
                linerLayout.addView(imageView, linerLayoutParames);

//                group.addView(imageView);
                group.addView(linerLayout);
            }
            //将图片装载到数组中
            mSlideImageViews = new ImageView[imgIdArray.length];
            for(int i=0; i< mSlideImageViews.length; i++){
                ImageView imageView = new ImageView(mContext);
//                imageView.setBackgroundResource(imgIdArray[i]);
                //从服务器加载图片
//                slideImageLoader.displayImage(Constants.getSlideImgURLList()[i % 8], imageView, slideImageOptions);
                imageLoader.displayImage(Constants.getSlideImgURLList()[i % 8], imageView, slideImageOptions);
                mSlideImageViews[i] = imageView;
            }
            //设置Adapter
            viewPager.setAdapter(new SlideImageAdapter());
            //设置监听，主要是设置点点的背景
            viewPager.setOnPageChangeListener(this);
            //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
            viewPager.setCurrentItem((mSlideImageViews.length) * 100);



        } else {
            holder.mListItemLayout.setVisibility(View.VISIBLE);
            holder.mSlideImageLayout.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);

            holder.id = position;
            holder.mTextView1.setText(mListItems[position].getTitle());
            holder.mTextView2.setText(mListItems[position].getNewsAbstract());
            holder.mTextView3.setText(mListItems[position].getUsername() + "  " + mListItems[position].getDateTime());
            holder.mTextView4.setText(mListItems[position].getCommentNum() + " reply");
//        holder.mImageVIew.setImageBitmap(mListItems[position].getImg());
            //从服务器加载图片
            imageLoader.displayImage(Constants.getImgURLList()[position % 8], holder.mImageView, options);
        }

	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
        return mListItems.length;
	}


    public class SlideImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mSlideImageViews[position % mSlideImageViews.length]);
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mSlideImageViews[position % mSlideImageViews.length]);
            return mSlideImageViews[position % mSlideImageViews.length];
        }
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        setImageBackground(arg0 % mSlideImageViews.length);
    }

    /**
     * 设置选中的tip的背景
     * @param selectItems
     */
    private void setImageBackground(int selectItems){
        for(int i=0; i<tips.length; i++){
            if(i == selectItems){
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            }else{
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

}
