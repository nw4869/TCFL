package com.nightwind.tcfl.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.adapter.ChatAdapter;
import com.nightwind.tcfl.bean.ChatMsgEntity;
import com.nightwind.tcfl.tool.Dummy;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    private static final String ARG_UID1 = "uid1";
    private static final String ARG_UID2 = "uid2";

    private int mUid1;
    private int mUid2;

    private ArrayList<ChatMsgEntity> mMsgs;

    //recyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button mbtnSend;
    private EditText mEditTextContent;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uid1 Parameter 1.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(int uid1, int uid2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_UID1, uid1);
        args.putInt(ARG_UID2, uid2);
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
            mUid1 = getArguments().getInt(ARG_UID1);
            mUid2 = getArguments().getInt(ARG_UID2);
        }
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ChatAdapter(getActivity(), mMsgs, mUid1, mUid2);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mRecyclerView.scrollToPosition(mMsgs.size() - 1);
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
        mMsgs = Dummy.getMsg(mUid1, mUid2);
    }


    //获取日期
    private String getDate() {
        Calendar c = Calendar.getInstance();
        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins);
        return sbBuffer.toString();
    }

    private void send()
    {
        String contString = mEditTextContent.getText().toString();
        if (contString.length() > 0)
        {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(getDate());
            entity.setName("");
            entity.setMsgType(false);
            entity.setText(contString);
            mMsgs.add(entity);
            mAdapter.notifyDataSetChanged();
            mEditTextContent.setText("");
            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        }
    }
}
