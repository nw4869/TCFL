package com.nightwind.tcfl.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.activity.FriendsActivity;
import com.nightwind.tcfl.activity.LoginActivity;
import com.nightwind.tcfl.activity.MainActivity;
import com.nightwind.tcfl.activity.MyArticleActivity;
import com.nightwind.tcfl.activity.MyCollectionActivity;
import com.nightwind.tcfl.activity.NeighborActivity;
import com.nightwind.tcfl.activity.PikachuActivity;
import com.nightwind.tcfl.activity.ProfileActivity;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.activity.RegistrationActivity;
import com.nightwind.tcfl.activity.ReviewListActivity;
import com.nightwind.tcfl.activity.TestActivity;
import com.nightwind.tcfl.activity.TranslateActivity;
import com.nightwind.tcfl.tool.localDB.LocalDBTestActivity;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonCenterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonCenterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonCenterFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";

    // TODO: Rename and change types of parameters
    private int mPosition;

    private Auth mAuth;

    private RelativeLayout menu0;
    private RelativeLayout menu1;
    private RelativeLayout menu2;
    private RelativeLayout menu3;
    private RelativeLayout menu4;
    private RelativeLayout menu5;
    private RelativeLayout menu6;
    private RelativeLayout menu7;
    private RelativeLayout menuExit;

    private TextView mTvExit;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Parameter 1.
     * @return A new instance of fragment PersonCenterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonCenterFragment newInstance(int position) {
        PersonCenterFragment fragment = new PersonCenterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public PersonCenterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_POSITION, 0);
        }

        mAuth = new Auth(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_person_center, container, false);

        ImageView head = (ImageView) v.findViewById(R.id.avatar);
        menu0 = (RelativeLayout) v.findViewById(R.id.menu0);
        menu1 = (RelativeLayout) v.findViewById(R.id.menu1);
        menu2 = (RelativeLayout) v.findViewById(R.id.menu2);
        menu3 = (RelativeLayout) v.findViewById(R.id.menu3);
        menu4 = (RelativeLayout) v.findViewById(R.id.menu4);
        menu5 = (RelativeLayout) v.findViewById(R.id.menu5);
        menu6 = (RelativeLayout) v.findViewById(R.id.menu6);
        menu7 = (RelativeLayout) v.findViewById(R.id.menu7);
        menuExit = (RelativeLayout) v.findViewById(R.id.menu_exit);
        mTvExit = (TextView) menuExit.findViewById(R.id.exit);

        head.setOnClickListener(this);
        menu0.setOnClickListener(this);
        menu1.setOnClickListener(this);
        menu2.setOnClickListener(this);
        menu3.setOnClickListener(this);
        menu4.setOnClickListener(this);
        menu5.setOnClickListener(this);
        menu6.setOnClickListener(this);
        menu7.setOnClickListener(this);
        menuExit.setOnClickListener(this);
        if (mAuth.isLogin()) {
            mTvExit.setText("退出当前账号");
        } else {
            mTvExit.setText("登录");
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAuth.isLogin()) {
            mTvExit.setText("退出当前账号");
        } else {
            mTvExit.setText("登录");
        }
    }

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


    @Override
    public void onClick(View v) {
        int id;
        switch (v.getId()) {
            case R.id.avatar:
            case R.id.menu0:
                id = 0;
                break;
            case R.id.menu1:
                id = 1;
                break;
            case R.id.menu2:
                id = 2;
                break;
            case R.id.menu3:
                id = 3;
                break;
            case R.id.menu4:
                id = 4;
                break;
            case R.id.menu5:
                id = 5;
                break;
            case R.id.menu6:
                id = 6;
                break;
            case R.id.menu7:
                id = 7;
                break;
            case R.id.menu_exit:
                id = 8;
                break;
            default:
                id = -1;
        }
        onListPressed(id);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onListPressed(int id) {
        if (mListener != null) {
            mPosition = id;
            //尚未登录
            if (!mAuth.isLogin()) {
                //弹出登录界面
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_LOGIN);

                getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

            } else {

                if (id == 0) {
                    //我的资料
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                } else if (id == 1/* || id == 2*/) {
                    Intent intent = new Intent(getActivity(), FriendsActivity.class);
                    if (id == 1) {
                        //所有好友
                        intent.putExtra("online", false);
                    } else {
                        //在线好友
                        intent.putExtra("online", true);
                    }
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                } else if (id == 2) {
                    Intent intent = new Intent(getActivity(), NeighborActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                } else if (id == 3) {
                    //我的帖子
                    Intent intent = new Intent(getActivity(), MyArticleActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                } else if (id == 4) {
                    //我的收藏
                    Intent intent = new Intent(getActivity(), MyCollectionActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                } else if (id == 5) {
                    //皮卡丘
                    Intent intent = new Intent(getActivity(), PikachuActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                } else if (id == 6) {
                    //找活动
                    Intent intent = new Intent(getActivity(), ReviewListActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                } else if (id == 7) {
                    //翻译
                    Intent intent = new Intent(getActivity(), TranslateActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);

                } else if(id == 8) {
                    if (!mAuth.isLogin()) {
                        //登录
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    } else {
                        //退出
                        mAuth.logout(mHandler);
                    }
                } else {
                    mListener.onFragmentInteraction(id);
                }
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //退出登录成功
            if(msg.what == Auth.MSG_LOGOUT_SUCCESS) {
                Toast.makeText(getActivity(), "退出登录成功", Toast.LENGTH_SHORT).show();
                mTvExit.setText("登录");
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.REQUEST_LOGIN) {
            if (resultCode == LoginActivity.RESULT_SUCCESS) {
                //密码登陆成功
                mTvExit.setText("退出当前账号");
                if (mPosition != 8) {
                    onListPressed(mPosition);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        public void onFragmentInteraction(int id);
    }

}
