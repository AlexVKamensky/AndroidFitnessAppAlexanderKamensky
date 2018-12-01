package com.worksmart.alphafitness;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class RecordWorkoutFragment extends Fragment implements OnMapReadyCallback{

    MapView mapView;
    private GoogleMap mMap;

    TextView distanceAmountText;
    TextView durationAmountText;

    static final String logId = "RecordWorkout";
    AlphaFtinessModel model;
    Button workoutButton;

    Polyline path;

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
        LatLng startLocation = getCurrentLocation();
        //mMap.addMarker(new MarkerOptions().position(startLocation).title("Marker of Start Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation));

        //float zoom = mMap.getCameraPosition().zoom;
        //Log.d(logId, "Zoom is " + zoom);
        // Zoom is set 18 which was the reasonable compramise between 15 street view and 20 building view
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        //zoom = mMap.getCameraPosition().zoom;
        //Log.d(logId, "Zoom is " + zoom);

    }

    private LatLng getCurrentLocation(){
        LatLng ret = null;
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ret;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, (LocationListener) getActivity());
        Location current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        ret = new LatLng(current.getLatitude(), current.getLongitude());
        return ret;
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

            for (int i = 0; i < details.basicdata.size(); i++) {
                AlphaFtinessModel.WorkoutSample sample = details.basicdata.get(i);
                if (prev != null) {
                    pathOptions.add(prev, sample.coordinate);
                }
                prev = sample.coordinate;
                //Log.d(logId, "The latitude is " + sample.coordinate.latitude + " the longitude is " + sample.coordinate.longitude);
            }
            path = mMap.addPolyline(pathOptions);

            String durationText = formatDuration(details.duration);
            String distanceText = String.format("%.5g", details.distance);;
            durationAmountText.setText(durationText);
            distanceAmountText.setText(distanceText);
        }
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

}
