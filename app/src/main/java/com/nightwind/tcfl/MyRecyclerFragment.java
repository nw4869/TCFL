package com.nightwind.tcfl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nightwind.tcfl.bean.MyListItem;
import com.nightwind.tcfl.tool.Constants;

import java.util.ArrayList;


public class MyRecyclerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_POSITION = "position";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int position;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

//    ArrayList<NewsEntity> newsList = new ArrayList<NewsEntity>();

    ArrayList<MyListItem> myListItems = new ArrayList<MyListItem>();

    private static final int[] drawables = { R.drawable.conan1, R.drawable.conan2, R.drawable.conan3, R.drawable.conan4,
            R.drawable.conan5, R.drawable.conan6, R.drawable.conan7, R.drawable.conan8 };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyRecyclerFragment newInstance(int position) {
        MyRecyclerFragment fragment = new MyRecyclerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public MyRecyclerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_recycler, container, false);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)

//        final int NUM_ITEM = 30;
//        MyListItem[] listItems = new MyListItem[NUM_ITEM];
//        for (int i = 0; i < NUM_ITEM; i++) {
//
//            MyListItem listItem = new MyListItem();
//
//            listItem.setTitle("Title " + (i + 1));
//            listItem.setNewsAbstract("Abstract " + (i + 1));
//            listItem.setUsername("UserName " + (i + 1));
//            listItem.setDateTime((i+1) + "min ago");
//            listItem.setCommentNum((i + 1));
//
//            Bitmap bitmap;
//            if (i < 8) {
//                bitmap = toRoundBitmap(BitmapFactory.decodeResource(getResources(), drawables[i % 8]));
//            } else {
////                bitmap = bitmaps[i%8];
//                bitmap = listItems[i % 8].getImg();
//            }
//            listItem.setImg(bitmap);
//            listItems[i] = listItem;
//        }


//        mAdapter = new MyAdapter(getActivity(), myDataset, bitmaps);
//        mAdapter = new MyAdapter(getActivity(), listItems);
        mAdapter = new MyAdapter(getActivity(), myListItems.toArray(new MyListItem[myListItems.size()]));
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    /**
     * 转换图片成圆形
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
        final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }


    private void initData() {
//        newsList = Constants.getNewsList();

//        myListItems = Constants.getMyListItem();

        final int NUM_ITEM = 30;

        myListItems = Constants.getMyListItem();

        for (int i = 0; i < NUM_ITEM; i++) {
//            MyListItem listItem = new MyListItem();
//
//            listItem.setTitle("Title " + (i + 1));
//            listItem.setNewsAbstract("Abstract " + (i + 1));
//            listItem.setUsername("UserName " + (i + 1));
//            listItem.setDateTime((i+1) + "min ago");
//            listItem.setCommentNum((i + 1));

            Bitmap bitmap;
            if (i < 8) {
                bitmap = toRoundBitmap(BitmapFactory.decodeResource(getResources(), drawables[i % 8]));
            } else {
//                bitmap = listItems[i % 8].getImg();
                bitmap = myListItems.get(i%8).getImg();
            }
//            listItem.setImg(bitmap);

            myListItems.get(i).setImg(bitmap);

//            //评论：
//            Random random = new Random();
//            int n = random.nextInt(10);
//            for (int j = 0; j < n; j++) {
//                CommentItem commentItem = new CommentItem();
//                commentItem.setUsername("User" + random.nextInt(10));
//                commentItem.setDateTime(random.nextInt(10) + "min age");
//                commentItem.setContent("Hello World" + random.nextInt(10));
//                commentItem.setImg(myListItems.get(random.nextInt(NUM_ITEM)).getImg());
//            }


//            listItems[i] = listItem;
//            myListItems.add(listItem);
        }
    }
}
