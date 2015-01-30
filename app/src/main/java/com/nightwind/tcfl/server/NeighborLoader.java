package com.nightwind.tcfl.server;

import android.content.Context;

import com.nightwind.tcfl.bean.Neighbor;
import com.nightwind.tcfl.controller.UserController;

import java.util.List;

/**
 * Created by wind on 2015/1/12.
 */
public class NeighborLoader extends DataLoader<List<Neighbor>> {

    public static final java.lang.String ARG_LATITUDE = "latitude";
    public static final java.lang.String ARG_LONGITUDE = "longitude";
    private final double longitude;
    private final double latitude;

    public NeighborLoader(Context context, double latitude, double longitude) {
        super(context);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public List<Neighbor> loadInBackground() {
        return new UserController(getContext()).getNeighbor(latitude, longitude);
    }
}
