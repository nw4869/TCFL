package com.nightwind.tcfl.activity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nightwind.tcfl.R;
import com.nightwind.tcfl.widget.ColorAnimationView;

public class WelcomeActivity extends FragmentActivity {

    private static final int[] resource = new int[]{R.drawable.welcome1, R.drawable.welcome4,
            R.drawable.welcome3, R.drawable.welcome4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
//        getWindow().setFlags(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);  //设置全屏

        setContentView(R.layout.activity_welcome);

        getSharedPreferences("welcome", MODE_PRIVATE).edit().putBoolean("welcome", false).apply();

        ColorAnimationView colorAnimationView = (ColorAnimationView) findViewById(R.id.ColorAnimationView);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == resource.length) {
                    return new Fragment();
                }
                final MyFragment myFragment = new MyFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                myFragment.setArguments(bundle);
                return myFragment;
            }

            @Override
            public int getCount() {
                return resource.length + 1;
            }
        };
        viewPager.setAdapter(adapter);

        colorAnimationView.setmViewPager(viewPager, resource.length);
        colorAnimationView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.d("TAG", "onPageScrolled position = " + position + " positionOffset = " + positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
//                Log.d("TAG","onPageSelected position = " + position);
                if (position == resource.length) {
                    finish();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.d("TAG","onPageScrollStateChanged");
            }
        });
    }


    public static class MyFragment extends Fragment {
//        private int position;

        public MyFragment() {}

//        public MyFragment(int position) {
//            this.position = position;
//        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            int position = getArguments().getInt("position");

            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(resource[position]);
            return imageView;
        }
    }

}
