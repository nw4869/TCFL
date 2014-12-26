package com.nightwind.tcfl.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.tool.BaseTools;

public class TranslateActivity extends BaseActivity {

    @Override
    int getLayoutResID() {
        return R.layout.activity_translate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_translate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String[] LANG_PAR_LIST = {"auto", "zh", "en", "jp", "kor", "spa", "fra", "th", "ara", "ru", "pt", "yue", "wyw", "zh", "de", "it"};
        private static final String[] LANG_LIST = {"自动检测", "中文", "英语", "日语", "韩语", "西班牙语", "法语", "泰语", "阿拉伯语", "俄罗斯语", "葡萄牙语", "粤语", "文言文", "白话文", "德语", "意大利语"};
        private static final String ARG_LANG_SRC_ID = "arg_lang_src_id";
        private static final String ARG_LANG_DST_ID = "arg_lang_dst_id";

        private int langSrcId = 0, langDstId = 0;

        private EditText mEtSource;
        private EditText mEtDestination;
        private ButtonRectangle mBtnTranslate;
        private ButtonRectangle mBtnExchange;
        private ProgressDialog mDialog;
        private ButtonRectangle mBtnLangDst;
        private ButtonRectangle mBtnLangSrc;
        private PopupMenu mPopupMenuSrc;
        private PopupMenu mPopupMenuDst;

        public PlaceholderFragment() {
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putInt(ARG_LANG_SRC_ID, langSrcId);
            outState.putInt(ARG_LANG_DST_ID, langSrcId);
            super.onSaveInstanceState(outState);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (savedInstanceState != null) {
                langSrcId = savedInstanceState.getInt(ARG_LANG_SRC_ID);
                langDstId = savedInstanceState.getInt(ARG_LANG_DST_ID);
            }
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_translate, container, false);


            mEtSource = (EditText) rootView.findViewById(R.id.editTextSource);
            mEtDestination = (EditText) rootView.findViewById(R.id.editTextDestination);
            mBtnLangSrc = (ButtonRectangle) rootView.findViewById(R.id.button_source);
            mBtnExchange = (ButtonRectangle) rootView.findViewById(R.id.button_exchange);
            mBtnLangDst = (ButtonRectangle) rootView.findViewById(R.id.button_destination);
            mBtnTranslate = (ButtonRectangle) rootView.findViewById(R.id.button_translate);

            mBtnLangSrc.setText(LANG_LIST[langSrcId]);
            mBtnLangDst.setText(LANG_LIST[langDstId]);

            mPopupMenuSrc = new PopupMenu(getActivity(), mBtnLangSrc);
            mPopupMenuDst = new PopupMenu(getActivity(), mBtnLangDst);
            mPopupMenuSrc.getMenu().clear();
            mPopupMenuDst.getMenu().clear();
            int i = 0;
            for (String lang: LANG_LIST) {
                mPopupMenuSrc.getMenu().add(Menu.NONE, i, Menu.NONE, lang);
                mPopupMenuDst.getMenu().add(Menu.NONE, i++, Menu.NONE, lang);
            }
            mPopupMenuSrc.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    langSrcId = item.getItemId();
                    mBtnLangSrc.setText(LANG_LIST[langSrcId]);
                    return true;
                }
            });
            mPopupMenuDst.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    langDstId = item.getItemId();
                    mBtnLangDst.setText(LANG_LIST[langDstId]);
                    return false;
                }
            });

            mBtnLangSrc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupMenuSrc.show();
                }
            });
            mBtnLangDst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupMenuDst.show();
                }
            });
            mBtnExchange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //交换语言和文本框
                    Editable tmp = mEtSource.getText();
                    mEtSource.setText(mEtDestination.getText());
                    mEtDestination.setText(tmp);
                    int tmpLang = langSrcId;
                    langSrcId = langDstId;
                    langDstId = tmpLang;
                    mBtnLangSrc.setText(LANG_LIST[langSrcId]);
                    mBtnLangDst.setText(LANG_LIST[langDstId]);
                }
            });
            mBtnTranslate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startTranslate();
                }
            });

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_translate, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.action_translate) {
                startTranslate();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void startTranslate() {
            String src = LANG_PAR_LIST[langSrcId];
            String dst = LANG_PAR_LIST[langDstId];
            String query = String.valueOf(mEtSource.getText());
            if (query.trim().length() > 0) {
                new TranslateTask().execute(src, dst, query);
            } else {
                Toast.makeText(getActivity(), "输入不能为空", Toast.LENGTH_SHORT).show();
            }
        }

        class TranslateTask extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mDialog = new ProgressDialog(getActivity());
                mDialog.setMessage("正在翻译，请稍后...");
                mDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                return BaseTools.translate(params[0], params[1], params[2]);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mEtDestination.setText(s);
                mDialog.cancel();
                if (s.length() == 0) {
                    Toast.makeText(getActivity(), "翻译失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
