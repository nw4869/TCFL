package com.nightwind.tcfl.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.SDKInitializer;
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.gc.materialdesign.views.ProgressBarCircularIndetermininate;
import com.nightwind.tcfl.R;
import com.nightwind.tcfl.adapter.NeighborAdapter;
import com.nightwind.tcfl.bean.Neighbor;
import com.nightwind.tcfl.controller.UserController;
import com.nightwind.tcfl.server.NeighborLoader;

import java.util.ArrayList;
import java.util.List;

public class NeighborActivity extends BaseActivity {

    private static final int LOAD_NEIGHBOR = 0;
    private static final String TAG = "NeighborActivity";
    public LocationClient mLocationClient;
    public LatLng mLatLng;
    public MyLocationListener mMyLocationListener;
    private List<Neighbor> mNeighborList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private NeighborAdapter mAdapter;
    private ProgressBarCircularIndetermininate mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    int getLayoutResID() {
        return R.layout.activity_neighbor;
//        return R.layout.distance_test_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NeighborAdapter(this, mNeighborList);
        mAdapter.setOnItemClickListener(new NeighborAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(NeighborActivity.this, ProfileActivity.class);
                intent.putExtra(ProfileActivity.ARG_USERNAME, mNeighborList.get(position).getUsername());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        mProgressBar = (ProgressBarCircularIndetermininate) findViewById(R.id.progressBar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mLocationClient != null /*&& mLocationClient.isStarted()*/) {
                    mLocationClient.start();
//                    mLocationClient.requestLocation();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.load_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //gpsTest
//        gpsTest();

        initLocation();

    }


    private void gpsTest() {

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS模块正常", Toast.LENGTH_SHORT) .show();
        } else {
            Toast.makeText(this, "请开启GPS！", Toast.LENGTH_SHORT).show();
            return ;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateView(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateView(location);
                Log.d(TAG, "onLocationChanged()");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                updateView(null);
                Log.d(TAG, "onStatusChanged()");
            }

            @Override
            public void onProviderEnabled(String provider) {
                updateView(locationManager.getLastKnownLocation(provider));
                Log.d(TAG, "onProviderEnabled()");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled()");
            }
        });

    }

    private void updateView(Location location) {
        TextView rst = (TextView) findViewById(R.id.textView1);
        if (location != null) {
            StringBuffer sb = new StringBuffer();
            sb.append("实时的位置信息：\n经度：");
            sb.append(location.getLongitude());
            sb.append("\n纬度：");
            sb.append(location.getLatitude());
            sb.append("\n高度：");
            sb.append(location.getAltitude());
            sb.append("\n速度：");
            sb.append(location.getSpeed());
            sb.append("\n方向：");
            sb.append(location.getBearing());
            sb.append("\n精度：");
            sb.append(location.getAccuracy());
            sb.append("\n时间");
            sb.append(location.getTime());
            System.out.println("---------------GPS-location -----------------");
            System.out.println(sb.toString());
            rst.setText(sb.toString());
        } else {
            // 如果传入的Location对象为空则清空EditText
            rst.setText("");
        }
    }

    public void onBtnRefreshClick(View v) {
        gpsTest();
    }

    public void onBtnClick(View v) {
        EditText et_latitude = (EditText) findViewById(R.id.editText1);
        EditText et_longitude = (EditText) findViewById(R.id.editText2);
        TextView tv_result = (TextView) findViewById(R.id.textView);
        double latitude = Double.valueOf(String.valueOf(et_latitude.getText()));
        double longitude = Double.valueOf(String.valueOf(et_longitude.getText()));

        double distance = DistanceUtil.getDistance(new LatLng(latitude, longitude), new LatLng(25.316277, 110.425037));
        Log.d("NeighborActivity", "distance = " + distance);
        tv_result.setText(String.valueOf(distance));
    }

    private void initLocation() {
        // 百度地图在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(getApplicationContext());

        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
//        mLocationClient.requestLocation();
        mLocationClient.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {

            mLocationClient.stop();

            //Receive Location
            StringBuilder sb = new StringBuilder(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\ndirection : ");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append(location.getDirection());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
            }
            double distance = DistanceUtil.getDistance(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(25.316277, 110.425037));
            sb.append("\ndistance : ").append(distance);
            Log.d("BaiduLocationApiDem", String.valueOf(distance));
//            mTextView.setText(sb.toString());
            Log.i("BaiduLocationApiDem", sb.toString());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean ok = new UserController(getApplicationContext()).updateLocation(location.getLatitude(), location.getLongitude());
                    Log.d("NeighborActivity", "update to server = " + ok);
                    Bundle bundle = new Bundle();
                    bundle.putDouble(NeighborLoader.ARG_LATITUDE, location.getLatitude());
                    bundle.putDouble(NeighborLoader.ARG_LONGITUDE, location.getLongitude());
                    getSupportLoaderManager().restartLoader(LOAD_NEIGHBOR, bundle, new LoadNeighborCallbacks());
                }
            }).start();

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_neighbor, menu);
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
            mLocationClient.start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadNeighborCallbacks implements android.support.v4.app.LoaderManager.LoaderCallbacks<List<Neighbor>> {
        @Override
        public Loader<List<Neighbor>> onCreateLoader(int id, Bundle args) {
            return new NeighborLoader(getApplicationContext(), args.getDouble(NeighborLoader.ARG_LATITUDE), args.getDouble(NeighborLoader.ARG_LONGITUDE));
        }

        @Override
        public void onLoadFinished(Loader<List<Neighbor>> loader, List<Neighbor> data) {
            if (data != null) {
                mNeighborList.clear();
                mNeighborList.addAll(data);
                if (data.size() == 0) {
                    Toast.makeText(getApplicationContext(), "暂无附近的人", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.load_failed, Toast.LENGTH_SHORT).show();
            }
            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onLoaderReset(Loader<List<Neighbor>> loader) {

        }
    }
}
