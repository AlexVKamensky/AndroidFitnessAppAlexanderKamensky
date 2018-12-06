package com.worksmart.alphafitness;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.text.DecimalFormat;

public class RecordWorkoutFragment extends Fragment implements OnMapReadyCallback{

    MapView mapView;
    private GoogleMap mMap;

    TextView distanceAmountText;
    TextView durationAmountText;
    public static final int GET_LOCATION = 1;

    public static final int mapPadding = 150;

    static final String logId = "RecordWorkout";
    AlphaFtinessModel model;
    Button workoutButton;

    Polyline path;

    BoundryKeeper boundryKeeper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record_workout, container, false);
        model = AlphaFtinessModel.model;
        workoutButton = (Button) view.findViewById(R.id.workoutbutton);
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(this);

        distanceAmountText = (TextView) view.findViewById(R.id.distanceAmountText);
        durationAmountText = (TextView) view.findViewById(R.id.durationAmountText);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    GET_LOCATION);


        }
        this.setButtonLabel();
        return view;
    }

    public void setButtonLabel(){
        //Log.d(logId, "setButtonLabel called");
        if (workoutButton != null) {
            //Log.d(logId, "workoutButton not null");
            if (AppState.state.workout == null) {
                //Log.d(logId, "Workout is null");
                workoutButton.setText(R.string.record_workout_record_button_start_text);
            } else {
                //Log.d(logId, "Workout is not null");
                workoutButton.setText(R.string.record_workout_record_button_end_text);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uis =  mMap.getUiSettings();
        uis.setZoomControlsEnabled(true);
        uis.setMapToolbarEnabled(true);
        if(AppState.state.workout != null) {
            ((MainActivity) getActivity()).startWorkoutRecordUiUpdate();
        }

        //
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            LatLng startLocation = getCurrentLocation();
            mMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 18));


            //mMap.setPadding(mapPadding, mapPadding , mapPadding ,mapPadding);

            //zoom = mMap.getCameraPosition().zoom;
            //Log.d(logId, "Zoom is " + zoom);

        }


    }

    private LatLng getCurrentLocation(){
        LatLng ret = null;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return ret;

        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER , 1000, 10, (LocationListener) getActivity());
            Location current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            ret = new LatLng(current.getLatitude(), current.getLongitude());
            return ret;

            }
    }


    @Override
    public void onResume() {
        //magic needed to make map respond/alive
        mapView.onResume();
        super.onResume();
    }

    public void startUpdateDetailsUI(){
        AlphaFtinessModel.WorkoutDetails details = AlphaFtinessModel.model.getWorkoutDetails(AppState.state.workout.getId());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(details.basicdata.get(0).coordinate));
    }

    public void updateDetailsUI(){
        //Log.d(logId, "updateDetailsUI called");
        if(AppState.state.workout != null) {
            AlphaFtinessModel.WorkoutDetails details = AlphaFtinessModel.model.getWorkoutDetails(AppState.state.workout.getId());
            if (path != null) {
                path.remove();
            }
            PolylineOptions pathOptions = new PolylineOptions();
            pathOptions.width(5);
            pathOptions.color(Color.RED);
            LatLng prev = null;
            BoundryKeeper boundryKeeper;
            if(details.basicdata.size() > 0) {
                VisibleRegion region = mMap.getProjection().getVisibleRegion();
                LatLngBounds bounds = region.latLngBounds;
                LatLng northeast = bounds.northeast;
                LatLng southwest = bounds.southwest;

                boundryKeeper = new BoundryKeeper(details.basicdata.get(0).coordinate,
                        details.basicdata.get(0).coordinate);

                for (int i = 0; i < details.basicdata.size(); i++) {
                    AlphaFtinessModel.WorkoutSample sample = details.basicdata.get(i);
                    if (prev != null) {
                        boundryKeeper.updateValues(prev.latitude, prev.longitude);
                        pathOptions.add(prev, sample.coordinate);
                    }
                    prev = sample.coordinate;
                    //Log.d(logId, "The latitude is " + sample.coordinate.latitude + " the longitude is " + sample.coordinate.longitude);
                }
                if(boundryKeeper.compareBoundries(northeast, southwest) && mMap != null) {
                    // only change camera position if we left visible boundries
                    try {
                        mMap.moveCamera(boundryKeeper.getBoundries());
                    }
                    catch (Exception exception){

                    }
                }

                path = mMap.addPolyline(pathOptions);


            }

            String durationText = formatDuration(details.duration);
            String distanceText = formatDistance(details.distance);
            durationAmountText.setText(durationText);
            distanceAmountText.setText(distanceText);
        }
    }
    public static String formatDistance(double number) {
        DecimalFormat df = new DecimalFormat("0.000");
        return df.format(number).replaceAll("\\.000$", "");
    }
    public String formatDuration(long duration){
        String ret = "";
        long seconds = duration/1000;
        long minutes = seconds/60;
        seconds = seconds%60;
        long hours = minutes/60;
        minutes = minutes%60;
        if(seconds < 10){
            ret = ":0" +seconds+ret;
        }
        else{
            ret =":"+seconds+ret;
        }
        if(minutes <10){
            ret = ":0"+minutes+ret;
        }
        else {
            ret = ":"+minutes+ret;
        }
        ret = hours + ret;
        return ret;
    }


    public class BoundryKeeper{
        double maxLatitude;
        double maxLongitude;
        double minLatitude;
        double minLongitude;



        public BoundryKeeper(LatLng northeast, LatLng southwest){
            maxLatitude = northeast.latitude;
            maxLongitude = northeast.longitude;
            minLatitude = southwest.latitude;
            minLongitude = southwest.longitude;

        }

        public void updateValues(double newLat, double newLong){
            if(newLat > maxLatitude){
                maxLatitude = newLat;
            }
            if(newLat < minLatitude){
                minLatitude = newLat;
            }
            if(newLong > maxLongitude){
                maxLongitude = newLong;
            }
            if(newLong < minLongitude){
                minLongitude = newLong;
            }
        }

        public CameraUpdate getBoundries(){
            LatLng max = new LatLng(maxLatitude, maxLongitude);
            LatLng min = new LatLng(minLatitude, minLongitude);
            LatLngBounds bounds =  new LatLngBounds(min, max);

            return CameraUpdateFactory. newLatLngBounds(bounds, mapPadding);
        }

        public boolean compareBoundries(LatLng northeast, LatLng southwest){
            boolean ret = false;
            if(maxLatitude > northeast.latitude){
                //Log.d(logId, "maxLatitude " + maxLatitude +" northeast lat " + northeast.latitude);
                ret = true;
            }
            if(maxLongitude > northeast.longitude){
                //Log.d(logId, "maxLongitude " + maxLongitude + " northeast long " + northeast.longitude);
                ret = true;
            }
            if(minLatitude < southwest.latitude){
                //Log.d(logId, "minLatitude " + minLatitude + " southeast lat " + southwest.latitude);
                ret = true;
            }
            if(minLongitude < southwest.longitude){
                //Log.d(logId, "minLongitude " + minLongitude + " southeast long " + southwest.longitude);
                ret = true;
            }
            return ret;
        }
    }

}
