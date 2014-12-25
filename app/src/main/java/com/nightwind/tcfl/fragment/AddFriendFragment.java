package com.nightwind.tcfl.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.server.UserLoader;
import com.nightwind.tcfl.tool.Options;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFriendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFriendFragment extends Fragment {
    private static final java.lang.String ARG_USERNAME = "queryUsername";
    private static final int LOAD_USER = 0;
    private static final int LOAD_SELF_USER = 1;
    private static final String ARG_SELF_USER = "selfUser";


    private OnFragmentInteractionListener mListener;

    private Button mBtnSearch;
    private EditText mEtQueryUsername;
    private ViewGroup mResult;
    private TextView mTvNotFound;
    private ViewGroup mUserItem;
    private ImageView mIVAvatar;
    private TextView mTvUsername;
    private TextView mTvSign;
    private ImageView mIVAdd;

    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private UserController mUserController;
    private User mSelfUser;
    private User mQueryUser;
    private ProgressDialog mDialog;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddFriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFriendFragment newInstance() {
        AddFriendFragment fragment = new AddFriendFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AddFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        mUserController = new UserController(getActivity());

        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, new Auth(getActivity()).getUsername());
        args.putBoolean(ARG_SELF_USER, true);
        getLoaderManager().initLoader(LOAD_SELF_USER, args, new UserLoaderCallbacks());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_add_friend, container, false);



        mBtnSearch = (Button) v.findViewById(R.id.search);
        mEtQueryUsername = (EditText) v.findViewById(R.id.queryUsername);
        mResult = (ViewGroup) v.findViewById(R.id.result);
        mTvNotFound = (TextView) v.findViewById(R.id.tvNotFound);
        mUserItem = (ViewGroup) v.findViewById(R.id.userItem);
        mIVAvatar = (ImageView) v.findViewById(R.id.imageView);
        mTvUsername = (TextView) v.findViewById(R.id.username);
        mTvSign = (TextView) v.findViewById(R.id.sign);
        mIVAdd = (ImageView) v.findViewById(R.id.iv_add);

        mBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryUsername = String.valueOf(mEtQueryUsername.getText());
//                ShowResult(queryUsername);
                Bundle args = new Bundle();
                args.putString(ARG_USERNAME, queryUsername);
                getLoaderManager().restartLoader(LOAD_USER, args, new UserLoaderCallbacks());

                mDialog = new ProgressDialog(getActivity());
                mDialog.setMessage("正在查询...");
                mDialog.show();

                mResult.setVisibility(View.GONE);
            }
        });

        mIVAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String queryUsername = String.valueOf(mEtQueryUsername.getText());
//                User selfUser = mUserController.getSelfUser();
//                User friend = mUserController.getUser(queryUsername);
                User selfUser = mSelfUser;
                User friend = mQueryUser;
                if (selfUser.getUid() == friend.getUid()) {
                    Toast.makeText(getActivity(), "添加好友失败，您不能添加自己为好友", Toast.LENGTH_SHORT).show();
                }
//                else if (selfUser.addFriend(friend.getUid())) {
//                    Toast.makeText(getActivity(), "添加好友成功", Toast.LENGTH_SHORT).show();
//                    mListener.onFragmentInteraction(true);
//                } else {
//                    Toast.makeText(getActivity(), "添加好友失败，您已添加该好友", Toast.LENGTH_SHORT).show();
//                }
                 else {
                    new AddFriendTask().execute(friend.getUsername());
                }
            }
        });


        mResult.setVisibility(View.GONE);

        return v;
    }

    public class AddFriendTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("正在添加...");
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            UserController uc = new UserController(getActivity());
            return uc.addFriend(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Toast.makeText(getActivity(), "添加好友成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "添加好友失败", Toast.LENGTH_SHORT).show();
            }
            mDialog.cancel();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUserController.closeDB();
    }

    public UserController getUserController() {
        return mUserController;
    }

    public class UserLoaderCallbacks implements LoaderManager.LoaderCallbacks<User> {
        boolean mIsSelfUser = false;
        @Override
        public Loader<User> onCreateLoader(int id, Bundle args) {
            mIsSelfUser = args.getBoolean(ARG_SELF_USER, false);
            return new UserLoader(getActivity(), args.getString(ARG_USERNAME));
        }

        @Override
        public void onLoadFinished(Loader<User> loader, User user) {
            if (mIsSelfUser)
            {
                mSelfUser = user;
            } else {
                mQueryUser = user;
                ShowResult();
            }
        }

        @Override
        public void onLoaderReset(Loader<User> loader) {
            //Do nothing
        }
    }

    private void ShowResult() {
        mDialog.cancel();
        User user = mQueryUser;
        mResult.setVisibility(View.VISIBLE);
        if (user == null) {
            mTvNotFound.setVisibility(View.VISIBLE);
            mUserItem.setVisibility(View.GONE);
        } else {
            mTvNotFound.setVisibility(View.GONE);
            mUserItem.setVisibility(View.VISIBLE);
            mTvUsername.setText(user.getUsername());
            mTvSign.setText(user.getInfo());
            //服务器下载头像
            imageLoader.displayImage(user.getAvatarUrl(), mIVAvatar, options);

            hideSoftInput();
        }
    }


//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(boolean addedFriend);
    }

    public void hideSoftInput() {
        //收起键盘
        if (mEtQueryUsername != null && getActivity() != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(mEtQueryUsername.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
