package com.nightwind.tcfl.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.exception.AuthenticationException;
import com.nightwind.tcfl.exception.EncryptException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegistrationActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final int REGISTRATION_NOT_LOGIN = -2;
        private static final int REGISTRATION_AUTH_FAILED = -2;
        private TextView mTextView;
        private RegistrationTask mRegistrationTask = new RegistrationTask();
        private Button mButton;
        private Activity mActivity;
        private Context mContext;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_registration, container, false);

            mTextView = (TextView) rootView.findViewById(R.id.textView);

            mButton = (Button) rootView.findViewById(R.id.button);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registration();
                }
            });

            mButton.setClickable(false);

            //get days
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    final int days = getDays();
//                    mTextView.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mTextView.setText("已连续签到" + days + "天");
//                        }
//                    });
//                }
//            }).start();
            new RegistrationTask().execute(UserController.REGISTRATION_GET);

            return rootView;
        }

//        private int getDays() {
//            int rst = 0;
//            try {
//                rst =  new UserController(mContext).getRegistrationDays();
//            } catch (AuthenticationException e) {
//                e.printStackTrace();
//            }
//            return rst;s
//        }

        private void registration() {
            if (mRegistrationTask.getStatus().equals(AsyncTask.Status.PENDING)) {
                mRegistrationTask.execute(UserController.REGISTRATION_POST);
            } else if (mRegistrationTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                mRegistrationTask = new RegistrationTask();
                mRegistrationTask.execute(UserController.REGISTRATION_POST);
            }
        }

        class RegistrationTask extends AsyncTask<Integer, Void, String> {

            private int type;

            @Override
            protected String doInBackground(Integer... params) {
                type = params[0];
                int ret = 0;
                JSONObject jo = null;
                try {
                    String rst = new UserController(getActivity()).registration(type);
                    jo = new JSONObject(rst);
                } catch (AuthenticationException e) {
                    ret = -2;
                } catch (EncryptException e) {
                    ret = -3;
                } catch (IOException e) {
                    ret = -4;
                } catch (JSONException e) {
                    ret = -1;
                }
                if (jo == null) {
                    jo = new JSONObject();
                }
                try {
                    jo.put("errorCode",ret);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return jo.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                JSONObject jo;
                int errorCode = -1;
                boolean isSigned = false;
                int days = 0;
                String lastTime = "";
                try {
                    jo = new JSONObject(result);
                    errorCode = jo.getInt("errorCode");
                    isSigned = jo.getBoolean("isSigned");
                    days = jo.getInt("days");
                    if (type == UserController.REGISTRATION_GET) {
                        lastTime = jo.getString("lastTime");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (errorCode == 0) {
                    if (isSigned) {
                        mButton.setText("已签到");
                        mButton.setClickable(false);
                        if(type == UserController.REGISTRATION_POST) {
                            Toast.makeText(mContext, "今天您已签到!", Toast.LENGTH_SHORT).show();
                        }
                    } else if (type == UserController.REGISTRATION_POST) {
                        Toast.makeText(mContext, "签到成功!", Toast.LENGTH_SHORT).show();
                    } else if (type == UserController.REGISTRATION_GET) {
                        mButton.setText("签到");
                        mButton.setClickable(true);
                    }
                    String msg = "已连续签到" + days + "天";
                    if (type == UserController.REGISTRATION_GET) {
                        if (lastTime == null || lastTime.length() < 1) {
                            msg += ",上次签到: - ";
                        } else {
                            msg += ",上次签到:" + lastTime.substring(0, Math.min(10, lastTime.length()));
                        }
                    }
                    mTextView.setText(msg);
                } else if (errorCode == REGISTRATION_NOT_LOGIN) {
                    Toast.makeText(mContext, "请先登录或重新登录", Toast.LENGTH_SHORT).show();
                }  else {
                    Toast.makeText(mContext, "签到失败，请检查网络后重试", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mActivity = activity;
            mContext = mActivity.getApplicationContext();
        }

        @Override
        public void onDetach() {
            super.onDetach();
            mActivity = null;
        }
    }



}
