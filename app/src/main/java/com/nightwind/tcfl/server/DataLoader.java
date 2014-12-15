package com.nightwind.tcfl.server;

import android.annotation.TargetApi;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Build;

import com.nightwind.tcfl.bean.User;

import java.util.ArrayList;

/**
 * Created by wind on 2014/12/14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class DataLoader<D> extends AsyncTaskLoader<D>{
    private D mData;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(D data) {
        mData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }
}
