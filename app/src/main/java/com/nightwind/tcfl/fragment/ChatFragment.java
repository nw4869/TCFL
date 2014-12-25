package com.nightwind.tcfl.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.adapter.ChatAdapter;
import com.nightwind.tcfl.bean.ChatMsg;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.ChatMsgController;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.server.ChatMsgLoader;
import com.nightwind.tcfl.server.UserLoader;
import com.nightwind.tcfl.server.UsersLoader;
import com.nightwind.tcfl.tool.BaseTools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
//    private static final String ARG_UID1 = "uid1";
//    private static final String ARG_UID2 = "uid2";
    private static final String ARG_USERNAME1 = "username1";
    private static final String ARG_USERNAME2 = "username2";
    private static final int LOAD_USERS = 0;
    private static final int LOAD_CHAT_MSG = 1;

//    private int mUid1;
//    private int mUid2;

    private List<ChatMsg> mMsgs;

    //recyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button mbtnSend;
    private EditText mEditTextContent;
    private UserController mUserController;
    private ChatMsgController mChatMsgController;
    private String mUsername1;
    private String mUsername2;
    private User mUser1;
    private User mUser2;
//    private SwipeRefreshLayout mSwipeLayout;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static ChatFragment newInstance(int uid1, int uid2) {
//        ChatFragment fragment = new ChatFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_UID1, uid1);
//        args.putInt(ARG_UID2, uid2);
//        fragment.setArguments(args);
//        return fragment;
//    }
    public static ChatFragment newInstance(String username1, String username2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME1, username1);
        args.putString(ARG_USERNAME2, username2);
        fragment.setArguments(args);
        return fragment;
    }

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mUid1 = getArguments().getInt(ARG_UID1);
//            mUid2 = getArguments().getInt(ARG_UID2);
            mUsername1 = getArguments().getString(ARG_USERNAME1);
            mUsername2 = getArguments().getString(ARG_USERNAME2);
        }
        mUserController = new UserController(getActivity());
        mChatMsgController = new ChatMsgController(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initData();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        mEditTextContent = (EditText) rootView.findViewById(R.id.et_sendmessage);
        mbtnSend = (Button) rootView.findViewById(R.id.btn_send);
        mbtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.chat_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.generateLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mRecyclerView.setLayoutManager(mLayoutManager);


//        //下拉刷新
//        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
//        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
//        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refreshList();
//            }
//        });
//
//        if (mMsgs == null) {
//            mSwipeLayout.setRefreshing(true);
//        }

        return rootView;
    }

    private void refreshList() {

        getLoaderManager().restartLoader(LOAD_CHAT_MSG, getArguments(), new ChatMsgLoaderCallbacks());
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //防止点到下面一层Fragment
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        mRecyclerView.scrollToPosition(mMsgs.size() - 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUserController.closeDB();
        mChatMsgController.closeDB();
    }

    //    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        ((FriendsActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        ((FriendsActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    }

    private void initData() {

        getLoaderManager().initLoader(LOAD_USERS, getArguments(), new UserLoaderCallbacks());

//        mMsgs = mChatMsgController.getMsg(mUid2);
    }

    public class UserLoaderCallbacks implements LoaderManager.LoaderCallbacks<User[]> {

        @Override
        public Loader<User[]> onCreateLoader(int id, Bundle args) {
            return new UsersLoader(getActivity(), args.getString(ARG_USERNAME1), args.getString(ARG_USERNAME2));
        }

        @Override
        public void onLoadFinished(Loader<User[]> loader, User[] data) {
            mUser1 = data[0];
            mUser2 = data[1];
            mMsgs = mChatMsgController.getMsg(mUser2.getUid());
//            updateUI();
            getLoaderManager().restartLoader(LOAD_CHAT_MSG, getArguments(), new ChatMsgLoaderCallbacks());
        }

        @Override
        public void onLoaderReset(Loader<User[]> loader) {

        }
    }

    public class ChatMsgLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<ChatMsg>> {

        @Override
        public Loader<List<ChatMsg>> onCreateLoader(int id, Bundle args) {
            return new ChatMsgLoader(getActivity(), args.getString(ARG_USERNAME2));
        }

        @Override
        public void onLoadFinished(Loader<List<ChatMsg>> loader, List<ChatMsg> data) {
            mChatMsgController.setMsg(mUser2.getUid(), data);
            mMsgs = data;
            updateUI();
//            mSwipeLayout.setRefreshing(false);
        }

        @Override
        public void onLoaderReset(Loader<List<ChatMsg>> loader) {

        }
    }

    synchronized private void updateUI() {

//        mAdapter = new ChatAdapter(getActivity(), mMsgs, mUid1, mUid2);
        mAdapter = new ChatAdapter(getActivity(), mMsgs, mUser1, mUser2);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mMsgs.size() - 1);
    }


//    //获取日期
//    private String getDate() {
//        Calendar c = Calendar.getInstance();
//        String year = String.valueOf(c.get(Calendar.YEAR));
//        String month = String.valueOf(c.get(Calendar.MONTH));
//        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
//        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
//        String mins = String.valueOf(c.get(Calendar.MINUTE));
//        StringBuffer sbBuffer = new StringBuffer();
//        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins);
//        return sbBuffer.toString();
//    }

    private void send()
    {
        String contString = mEditTextContent.getText().toString();
        if (contString.length() > 0)
        {
            ChatMsg entity = new ChatMsg();
//            entity.setDate(getDate());
            entity.setDate(BaseTools.getCurrentDateTime());
            entity.setName("");
            entity.setMsgType(false);
            entity.setContent(contString);
            mMsgs.add(entity);
            mAdapter.notifyDataSetChanged();
            mEditTextContent.setText("");
            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

            new SendMsgTask().execute(mUsername2, contString);
        }
    }

    public class SendMsgTask extends AsyncTask<String, Void, Integer> {

        private String msg;

        @Override
        protected Integer doInBackground(String... params) {
            ChatMsgController cmc = new ChatMsgController(getActivity());
            msg = params[1];
            return cmc.sendMsg(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == -1) {
                Toast.makeText(getActivity(), msg + "发送失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            refreshList();
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideSoftInput() {
        //收起键盘
        if (mEditTextContent != null && getActivity() != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(mEditTextContent.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
