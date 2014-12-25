package com.nightwind.tcfl.fragment;

import android.annotation.TargetApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nightwind.tcfl.adapter.ArticleAdapter;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.Article;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.server.ArticleAbstractsLoader;

import java.util.ArrayList;


public class ArticleRecyclerFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final java.lang.String ARG_CLASSIFY = "classify";
    private static final java.lang.String ARG_BEGIN_PAGE = "beginPage";
    private static final java.lang.String ARG_END_PAGE = "endPage";
    private static final String ARG_TYPE = "type";

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_WITH_SLIDE_IMAGE = 1;
    public static final int TYPE_COLLECTION = 2;
    public static final int TYPE_MY_ARTICLE = 3;


    private int position;
    private int type;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

//    ArrayList<NewsEntity> newsList = new ArrayList<NewsEntity>();

    //数据集
    ArrayList<Article> mArticleEntities = new ArrayList<Article>();

    private static final int[] drawables = {R.drawable.conan1, R.drawable.conan2, R.drawable.conan3, R.drawable.conan4,
            R.drawable.conan5, R.drawable.conan6, R.drawable.conan7, R.drawable.conan8};
    private UserController mUserController;
    private int beginPage;
    private int endPage;
    private boolean firstLoadData = true;
    private int lastPosition = 0;
    private int lastOffset = 0;
    private SwipeRefreshLayout mSwipeLayout;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyCardFragment.
     */
    public static ArticleRecyclerFragment newInstance(int position, int type) {
        ArticleRecyclerFragment fragment = new ArticleRecyclerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CLASSIFY, position);
        args.putInt(ARG_BEGIN_PAGE, 0);
        args.putInt(ARG_END_PAGE, 1);
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * 用于我的收藏，我的帖子，type必须为TYPE_COLLECTION 或 TYPE_MY_ARTICLE
     *
     * @param type
     * @return
     */
    public static ArticleRecyclerFragment newInstance(int type) {
        ArticleRecyclerFragment fragment = new ArticleRecyclerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    public ArticleRecyclerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_CLASSIFY, 0);
            type = getArguments().getInt(ARG_TYPE);
            beginPage = getArguments().getInt(ARG_BEGIN_PAGE, 0);
            endPage = getArguments().getInt(ARG_END_PAGE, 1);
        }
        initData();
    }

    private void initData() {

        Bundle args = new Bundle();
        args.putInt(ARG_CLASSIFY, position);
        args.putInt(ARG_BEGIN_PAGE, beginPage);
        args.putInt(ARG_END_PAGE, endPage);
        args.putInt(ARG_TYPE, type);
        getLoaderManager().initLoader(type, args, new ArticleAbstractsLoaderCallbacks());


//        if (type == TYPE_NORMAL || type == TYPE_WITH_SLIDE_IMAGE) {
//
//            ArticleController articleController = new ArticleController(getActivity());
//            mArticleEntities = articleController.getMyListItem(position);
//        } else if (type == TYPE_COLLECTION) {
////            mArticleEntities.clear();
////            ArrayList<Integer> collectionList = Dummy.getCollectionList();
////            for (Integer collectionId: collectionList) {
////                Article article = Dummy.getArticle(collectionId);
////                mArticleEntities.add(article);
////            }
//            mArticleEntities = ArticleController.getCollectionList();
//        } else if (type == TYPE_MY_ARTICLE) {
//            mArticleEntities = ArticleController.getMyArticleList();
//        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_recycler, container, false);
        mUserController = new UserController(getActivity());

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView = (RecyclerView) v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();
                //lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
//                    loadPage(currentQueryMap);
                    //自动加载
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //记录位置
                    View topView = mLayoutManager.getChildAt(0);          //获取可视的第一个view
                    if (topView != null) {                                  //没有元素的时候topView为null
                        lastOffset = topView.getTop();                         //获取与该view的顶部的偏移量
                        lastPosition = mLayoutManager.getPosition(topView);  //得到该View的数组位置
//                    Log.d("onScroll", "position = " + lastPosition);
                    }
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

        });

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //下拉刷新
        mSwipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        mSwipeLayout.setRefreshing(true);

        // specify an adapter (see also next example)

//        final int NUM_ITEM = 30;
//        Article[] listItems = new Article[NUM_ITEM];
//        for (int i = 0; i < NUM_ITEM; i++) {
//
//            Article listItem = new Article();
//
//            listItem.setTitle("Title " + (i + 1));
//            listItem.setNewsAbstract("Abstract " + (i + 1));
//            listItem.setUsername("UserName " + (i + 1));
//            listItem.setDate((i+1) + "min ago");
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

        updateUI();

        return v;
    }


    //异步加载回调
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public class ArticleAbstractsLoaderCallbacks implements LoaderManager.LoaderCallbacks<ArrayList<Article>> {
        @Override
        public Loader<ArrayList<Article>> onCreateLoader(int id, Bundle args) {
            return new ArticleAbstractsLoader(getActivity(), args.getInt(ARG_CLASSIFY), args.getInt(ARG_BEGIN_PAGE), args.getInt(ARG_END_PAGE), args.getInt(ARG_TYPE));
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Article>> loader, ArrayList<Article> data) {
            mArticleEntities = data;
            if (mArticleEntities != null) {
                updateUI();
                //mLayoutManager.scrollToPosition(lastPosition);
                //这样更精确
                ((LinearLayoutManager) mLayoutManager).scrollToPositionWithOffset(lastPosition, lastOffset);
            }
            firstLoadData = false;
            mSwipeLayout.setRefreshing(false);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Article>> loader) {
            //Do nothing
        }
    }

    private void updateUI() {
//        mAdapter = new ArticleAdapter(getActivity(), myDataset, bitmaps);
//        mAdapter = new ArticleAdapter(getActivity(), listItems);
        if (type == TYPE_COLLECTION || type == TYPE_MY_ARTICLE) {
            mAdapter = new ArticleAdapter(getActivity(), mArticleEntities, type);
        } else {
            mAdapter = new ArticleAdapter(getActivity(), mArticleEntities, type, position);
        }
        if (mAdapter != null) {
            mRecyclerView.setAdapter(mAdapter);
        }

    }


    @Override
    public void onResume() {
        if (!firstLoadData) {
//            refreshList();
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUserController.closeDB();
    }



    //        final int NUM_ITEM = 30;
//        //加载bitmap
//        for (int i = 0; i < NUM_ITEM; i++) {
//            Bitmap bitmap;
//            if (i < 8) {
//                bitmap = toRoundBitmap(BitmapFactory.decodeResource(getResources(), drawables[i % 8]));
//            } else {
////                bitmap = listItems[i % 8].getImg();
//                bitmap = mArticleEntities.get(i%8).getImg();
//            }
////            listItem.setImg(bitmap);
//            mArticleEntities.get(i).setImg(bitmap);
//        }
    public void refreshList() {
        Bundle args = new Bundle();
        args.putInt(ARG_CLASSIFY, position);
        args.putInt(ARG_BEGIN_PAGE, beginPage);
        args.putInt(ARG_END_PAGE, endPage);
        args.putInt(ARG_TYPE, type);
        getLoaderManager().restartLoader(type, args, new ArticleAbstractsLoaderCallbacks());

        mAdapter.notifyDataSetChanged();
    }
}
