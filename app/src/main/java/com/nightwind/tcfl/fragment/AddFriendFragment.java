package com.nightwind.tcfl.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.tool.Dummy;
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
    private static final String ARG_UID1 = "uid1";

    private int mUid1;

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


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uid1 Parameter 1.
     * @return A new instance of fragment AddFriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFriendFragment newInstance(int uid1) {
        AddFriendFragment fragment = new AddFriendFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_UID1, uid1);
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
            mUid1 = getArguments().getInt(ARG_UID1);
        }
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
                User user = search(queryUsername);
                showResult(user);
            }
        });

        mIVAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String queryUsername = String.valueOf(mEtQueryUsername.getText());
                User selfUser = Dummy.getSelfUser();
                User friend = Dummy.getUser(queryUsername);
                if (selfUser.addFriend(friend.getUid())) {
                    Toast.makeText(getActivity(), "添加好友成功", Toast.LENGTH_SHORT).show();
                    mListener.onFragmentInteraction(true);
                } else {
                    Toast.makeText(getActivity(), "您已添加该好友", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mResult.setVisibility(View.GONE);

        return v;
    }

    private void showResult(User user) {
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
            imageLoader.displayImage(user.getAvaterUrl(), mIVAvatar, options);

        }
    }

    private User search(String queryUsername) {
        return Dummy.getUser(queryUsername);
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

}
