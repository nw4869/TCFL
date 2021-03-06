package com.nightwind.tcfl.fragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ProgressBarCircularIndetermininate;
import com.nightwind.tcfl.adapter.FriendsAdapter;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.server.FriendsLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ONLINE = "online";
//    private static final java.lang.String ARG_SELF_USERNAME = "self_username";
    private static final int LOAD_FRIENDS = 0;

    // TODO: Rename and change types of parameters
    private boolean mOnline;

    //数据集
    private List<User> mFriendsList;

    //recyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeLayout;
    private ButtonFloat mButtonFloatAdd;
    private ProgressBarCircularIndetermininate mProgressBar;
    private TextView mEmpty;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param online Parameter 1.
     * @return A new instance of fragment FriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendsFragment newInstance(boolean online) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_ONLINE, online);
        fragment.setArguments(args);
        return fragment;
    }

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOnline = getArguments().getBoolean(ARG_ONLINE);
        }

        initData();
    }

    private void initData() {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_ONLINE, mOnline ? 1 : 0);
        getLoaderManager().restartLoader(LOAD_FRIENDS, bundle, new FriendsLoaderCallbacks());
    }

    //异步加载回调
    public class FriendsLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<User>> {
        @Override
        public Loader<List<User>> onCreateLoader(int id, Bundle args) {
            return new FriendsLoader(getActivity(), args.getInt(ARG_ONLINE));
        }

        @Override
        public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
            if (data != null)  {
                //mLayoutManager.scrollToPosition(lastPosition);
                //这样更精确
//                ((LinearLayoutManager) mLayoutManager).scrollToPositionWithOffset(lastPosition, lastOffset);
                mFriendsList.clear();
                mFriendsList.addAll(data);
                if (data.size() == 0) {
                    mEmpty.setVisibility(View.VISIBLE);
                } else {
                    mEmpty.setVisibility(View.GONE);
                }
                updateUI();
            } else {
                Toast.makeText(getActivity(), R.string.load_failed, Toast.LENGTH_SHORT).show();
            }
//            firstLoadData = false;
//            mSwipeLayout.setRefreshing(false);
            mSwipeLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoaderReset(Loader<List<User>> loader) {
            //Do nothing
        }
    }

    private void updateUI() {
//        mAdapter = new FriendsAdapter(getActivity(), mFriendsList);
//        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mProgressBar = (ProgressBarCircularIndetermininate) rootView.findViewById(R.id.progressBarCircularIndetermininate);

        mEmpty = (TextView) rootView.findViewById(R.id.tv_empty);

        //ButtonFloat
        mButtonFloatAdd = (ButtonFloat) rootView.findViewById(R.id.buttonFloatAdd);
        mButtonFloatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddFriend();
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.friends_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //下拉刷新
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        if (mFriendsList == null) {
            mSwipeLayout.setRefreshing(true);
        }
        mFriendsList = new ArrayList<>();
        mAdapter = new FriendsAdapter(getActivity(), mFriendsList);
        mRecyclerView.setAdapter(mAdapter);

//        updateUI();

        return rootView;
    }

    private void toAddFriend() {
        mListener.onFragmentInteractionAddFriend();
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
////        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_friends, menu);
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
//            ((FriendsActivity)mListener).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
//        ((FriendsActivity)mListener).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String msg);

        public void onFragmentInteractionAddFriend();

    }

    public void refreshList() {
        initData();
    }

}
