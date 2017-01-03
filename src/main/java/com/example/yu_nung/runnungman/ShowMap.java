package com.example.yu_nung.runnungman;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by yu-nung on 12/31/16.
 */
public class ShowMap extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback,GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {

    MapView mapView;
    GoogleMap map;
    Marker marker;
    Location currLoc;
    LatLng mylatLng, markerLoc = null;
    boolean markerFlg = false;
    GoogleApiClient mGoogleApiClient;
    final int MIN_TIME = 1000;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map, container, false);
        mapView = (MapView) v.findViewById(R.id.mvMap);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();

        //移除位置請求服務
        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // 移除Google API 用戶端連線
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // 已經連線到Google Services
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(MIN_TIME);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Google Services連線中斷
        // int參數是連線中斷的代號
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Services連線失敗
        // ConnectionResult參數是連線失敗的資訊
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        MapsInitializer.initialize(getActivity());
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                markerFlg = false;
                markerLoc = null;
                return false;
            }
        });
        map.setOnMarkerDragListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currLoc = location;
        mylatLng = new LatLng(currLoc.getLatitude(), currLoc.getLongitude());
        if(markerLoc != null){
        ((MainActivity) getActivity()).checkDistance(mylatLng, markerLoc);
        }
    }


    @Override
    public void onMapClick(LatLng latLng) {
        if(markerFlg == false) {
            marker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("目的地")
                    .draggable(true));
            marker.showInfoWindow();
            markerFlg = true;
            markerLoc = marker.getPosition();
           ((MainActivity) getActivity()).checkDistance(mylatLng,markerLoc);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(mylatLng, 14.0f));

        }
        else {
            marker.showInfoWindow();
            Toast.makeText(getActivity().getApplicationContext(), "已經設定目的地", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {


    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        markerLoc = marker.getPosition();
        ((MainActivity) getActivity()).checkDistance(mylatLng,markerLoc);
    }
}
