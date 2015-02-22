package com.nightwind.tcfl.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.nightwind.tcfl.Auth;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.bean.User;
import com.nightwind.tcfl.controller.ArticleController;
import com.nightwind.tcfl.controller.ChatMsgController;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.fragment.ArticleRecyclerFragment;
import com.nightwind.tcfl.fragment.PersonCenterFragment;
import com.nightwind.tcfl.tool.Options;
import com.nightwind.tcfl.widget.PagerSlidingTabStrip;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends ActionBarActivity implements PersonCenterFragment.OnFragmentInteractionListener{
    private static final int REQUEST_REGISTRATION = 1;
    private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ShareActionProvider mShareActionProvider;
	private PagerSlidingTabStrip mPagerSlidingTabStrip;
	private ViewPager mViewPager;
	private Toolbar mToolbar;
    private HashMap<Integer, Fragment> mFragments;
    //图片下载选项
    DisplayImageOptions options = Options.getListOptions();
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public static final int REQUEST_ADD_ARTICLE_BASE = 10000;
    public static final int REQUEST_LOGIN = 0;
    AsyncTask<String, Void, String> mRefreshAvatarTask = new RefreshAvatarTask();
    class RefreshAvatarTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            final User selfUser = new UserController(getApplicationContext()).getSelfUser();
            if (selfUser != null) {
                return selfUser.getAvatarUrl();
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String avatarUrl) {
            super.onPostExecute(avatarUrl);
            //初始化Drawer头像
            ImageView avatar = (ImageView) mDrawerLayout.findViewById(R.id.avatar);
            String selfUserName = new Auth(getApplicationContext()).getUsername();
            if (selfUserName != null) {
                imageLoader.displayImage(avatarUrl, avatar, options);
            }

        }
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                alphaAnimation.setDuration(500);
                imageView.startAnimation(alphaAnimation);
                imageView.setVisibility(View.GONE);

                SharedPreferences sp = getSharedPreferences("welcome", MODE_PRIVATE);
                boolean welcome = sp.getBoolean("welcome", true);
                if (welcome) {
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
            }
        }, 1000);



		initViews();
	}

	private void initViews() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setVisibility(View.VISIBLE);
		// toolbar.setLogo(R.drawable.ic_launcher);
		mToolbar.setTitle(R.string.app_name);// 标题的文字需在setSupportActionBar之前，不然会无效
		// toolbar.setSubtitle("副标题");
		setSupportActionBar(mToolbar);

		/* 这些通过ActionBar来设置也是一样的，注意要在setSupportActionBar(toolbar);之后，不然就报错了 */
		// getSupportActionBar().setTitle("标题");
		// getSupportActionBar().setSubtitle("副标题");
		// getSupportActionBar().setLogo(R.drawable.ic_launcher);
//		getSupportActionBar().setLogo(R.drawable.kenan);

		/* 菜单的监听可以在toolbar里设置，也可以像ActionBar那样，通过下面的两个回调方法来处理 */
		mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.action_settings:
					Toast.makeText(MainActivity.this, "action_settings", Toast.LENGTH_SHORT).show();
					break;
				case R.id.action_share:
					Toast.makeText(MainActivity.this, "action_share", Toast.LENGTH_SHORT).show();
					break;
                //添加文章
                case R.id.action_add_article:
                    int classify = mViewPager.getCurrentItem();
                    Intent intent = new Intent(MainActivity.this, AddArticleActivity.class);
                    intent.putExtra("classify", classify);
//                    MainActivity.this.startActivity(intent);
                    MainActivity.this.startActivityForResult(intent, REQUEST_ADD_ARTICLE_BASE + classify);
//                    MainActivity.this.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
//                    Toast.makeText(MainActivity.this, "action_add_article", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_registration:
                    Intent intent1 = new Intent(MainActivity.this, RegistrationActivity.class);
                    MainActivity.this.startActivityForResult(intent1, REQUEST_REGISTRATION);
                    break;

				default:
					break;
				}
				return true;
			}
		});
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		/* findView */
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
//                R.string.drawer_close);
		mDrawerToggle.syncState();
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
        mFragments = new HashMap<>();
		mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		mPagerSlidingTabStrip.setViewPager(mViewPager);
		mPagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
//				colorChange(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});


		initTabsValue();
	}

	/**
	 * mPagerSlidingTabStrip默认值配置
	 * 
	 */
	private void initTabsValue() {
		// 底部游标颜色
//		mPagerSlidingTabStrip.setIndicatorColor(Color.BLUE);
		mPagerSlidingTabStrip.setIndicatorColor(Color.parseColor("#DD2C00"));
        // tab的分割线颜色
		mPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
		// tab背景
//		mPagerSlidingTabStrip.setBackgroundColor(Color.parseColor("#4876FF"));
		mPagerSlidingTabStrip.setBackgroundColor(Color.parseColor("#FF3D00"));
		// tab底线高度
		mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				1, getResources().getDisplayMetrics()));
		// 游标高度
		mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				5, getResources().getDisplayMetrics()));
		// 选中的文字颜色
		mPagerSlidingTabStrip.setSelectedTextColor(Color.BLACK);
		// 正常文字颜色
		mPagerSlidingTabStrip.setTextColor(Color.WHITE);
	}

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        if (!mRefreshAvatarTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            mRefreshAvatarTask = new RefreshAvatarTask();
            mRefreshAvatarTask.execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }



    //	/**
//	 * 界面颜色的更改
//	 */
//	@SuppressLint("NewApi")
//	private void colorChange(int position) {
//		// 用来提取颜色的Bitmap
//		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//				SuperAwesomeCardFragment.getBackgroundBitmapPosition(position));
//		// Palette的部分
//		Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
//			/**
//			 * 提取完之后的回调方法
//			 */
//			@Override
//			public void onGenerated(Palette palette) {
//				Palette.Swatch vibrant = palette.getVibrantSwatch();
//                if (vibrant == null) {
//                    System.out.println("vibrant is null");
//                    return ;
//                }
//				/* 界面颜色UI统一性处理,看起来更Material一些 */
//				mPagerSlidingTabStrip.setBackgroundColor(vibrant.getRgb());
//				mPagerSlidingTabStrip.setTextColor(vibrant.getTitleTextColor());
//				// 其中状态栏、游标、底部导航栏的颜色需要加深一下，也可以不加，具体情况在代码之后说明
//				mPagerSlidingTabStrip.setIndicatorColor(colorBurn(vibrant.getRgb()));
//
//				mToolbar.setBackgroundColor(vibrant.getRgb());
//				if (android.os.Build.VERSION.SDK_INT >= 21) {
//					Window window = getWindow();
//					// 很明显，这两货是新API才有的。
//					window.setStatusBarColor(colorBurn(vibrant.getRgb()));
//					window.setNavigationBarColor(colorBurn(vibrant.getRgb()));
//				}
//			}
//		});
//	}

	/**
	 * 颜色加深处理
	 * 
	 * @param RGBValues
	 *            RGB的值，由alpha（透明度）、red（红）、green（绿）、blue（蓝）构成，
	 *            Android中我们一般使用它的16进制，
	 *            例如："#FFAABBCC",最左边到最右每两个字母就是代表alpha（透明度）、
	 *            red（红）、green（绿）、blue（蓝）。每种颜色值占一个字节(8位)，值域0~255
	 *            所以下面使用移位的方法可以得到每种颜色的值，然后每种颜色值减小一下，在合成RGB颜色，颜色就会看起来深一些了
	 * @return
	 */
	private int colorBurn(int RGBValues) {
		int alpha = RGBValues >> 24;
		int red = RGBValues >> 16 & 0xFF;
		int green = RGBValues >> 8 & 0xFF;
		int blue = RGBValues & 0xFF;
		red = (int) Math.floor(red * (1 - 0.1));
		green = (int) Math.floor(green * (1 - 0.1));
		blue = (int) Math.floor(blue * (1 - 0.1));
		return Color.rgb(red, green, blue);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		/* ShareActionProvider配置 */
//		mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu
//				.findItem(R.id.action_share));
//		Intent intent = new Intent(Intent.ACTION_SEND);
//		intent.setType("text/*");
//		mShareActionProvider.setShareIntent(intent);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//            case R.id.action_settings:
//                Toast.makeText(MainActivity.this, "action_settings", 0).show();
//                break;
//            case R.id.action_share:
//                Toast.makeText(MainActivity.this, "action_share", 0).show();
//                break;
//            default:
//               break;
//		}
		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onFragmentInteraction(int id) {
        Toast.makeText(this, "slider menu select id:" + id, Toast.LENGTH_SHORT).show();
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode >= REQUEST_ADD_ARTICLE_BASE && requestCode < REQUEST_ADD_ARTICLE_BASE+mViewPager.getChildCount()) {
            int classify = requestCode - REQUEST_ADD_ARTICLE_BASE;
            if (resultCode == 0) {
                //获取当前fragment
//            Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + mViewPager.getCurrentItem());
                Fragment fragment = mFragments.get(classify);
                //刷新列表
                if(fragment != null) {
                    ((ArticleRecyclerFragment)fragment).refreshList();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* ***************FragmentPagerAdapter***************** */
	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "语言", "文化历史", "风景美食", "最爱", "礼仪常识", "随机" };

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
//            if (position == 1) {
//                return ArticleRecyclerFragment.newInstance(position);
//            }
//			return SuperAwesomeCardFragment.newInstance(position);
            int type;
            if (position == 0) {
                type = ArticleRecyclerFragment.TYPE_WITH_SLIDE_IMAGE;
            } else {
                type = ArticleRecyclerFragment.TYPE_NORMAL;
            }
            Fragment fragment = ArticleRecyclerFragment.newInstance(position, type);
            mFragments.put(position, fragment);
            return fragment;
		}

	}

}
