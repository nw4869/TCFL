package com.nightwind.tcfl;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightwind.tcfl.bean.MyListItem;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private static Context mContext;

//    private String[][] mDataset;
//    private Bitmap[] mBitmaps;
    private MyListItem[] mListItems;



	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	public static class ViewHolder extends RecyclerView.ViewHolder {
		// each data item is just a string in this case
        public int id;
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;
        public TextView mTextView4;
        public ImageView mImageVIew;

		public ViewHolder(View v) {
			super(v);
            mTextView1 = (TextView) v.findViewById(R.id.textView1);
            mTextView2 = (TextView) v.findViewById(R.id.textView2);
            mTextView3 = (TextView) v.findViewById(R.id.textView3);
            mTextView4 = (TextView) v.findViewById(R.id.textView4);
            mImageVIew = (ImageView) v.findViewById(R.id.imageView);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(mContext, "onClick " + id, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, ContentActivity.class);
                    intent.putExtra("id", id);
                    mContext.startActivity(intent);
                }
            });
		}
	}

	// Provide a suitable constructor (depends on the kind of dataset)
//	public MyAdapter(Context context, String[][] myDataset, Bitmap[] bitmaps) {
//        mContext = context;
//		mDataset = myDataset;
//        mBitmaps = bitmaps;
//	}

//    public MyAdapter(Context context, MyListItem[] listItems) {
//        mContext = context;
//        mListItems = listItems;
//    }

    public MyAdapter(Context context, MyListItem[] listItems) {
        mContext = context;
        mListItems = listItems;
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
//		holder.mTextView1.setText(mDataset[0][position]);
//        holder.mTextView2.setText(mDataset[1][position]);
//        holder.mTextView3.setText(mDataset[2][position]);
//        holder.mImageVIew.setImageBitmap(mBitmaps[position]);
        holder.id = position;
        holder.mTextView1.setText(mListItems[position].getTitle());
        holder.mTextView2.setText(mListItems[position].getNewsAbstract());
        holder.mTextView3.setText(mListItems[position].getUsername() + "  " + mListItems[position].getDateTime());
        holder.mTextView4.setText(mListItems[position].getCommentNum() + " reply");
        holder.mImageVIew.setImageBitmap(mListItems[position].getImg());
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
//		return mDataset[0].length;
        return mListItems.length;
	}

}
