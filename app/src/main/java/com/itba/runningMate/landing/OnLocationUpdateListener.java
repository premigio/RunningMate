package com.itba.runningMate.landing;

import com.google.android.gms.maps.model.LatLng;

public interface OnLocationUpdateListener {

    void onLocationUpdate(LatLng newLocation);

}
