package com.worksmart.alphafitness;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RecordWorkoutFragment extends Fragment implements OnMapReadyCallback{

    MapView mapView;
    private GoogleMap mMap;

    static final String logId = "RecordWorkout";
    AlphaFtinessModel model;
    Button workoutButton;



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

        LatLng startLocation = new LatLng(37.562401, -122.049792);
        mMap.addMarker(new MarkerOptions().position(startLocation).title("Marker of Start Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation));

        //float zoom = mMap.getCameraPosition().zoom;
        //Log.d(logId, "Zoom is " + zoom);
        // Zoom is set 18 which was the reasonable compramise between 15 street view and 20 building view
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        //zoom = mMap.getCameraPosition().zoom;
        //Log.d(logId, "Zoom is " + zoom);

    }

    @Override
    public void onResume() {
        //magic needed to make map respond/alive
        mapView.onResume();
        super.onResume();
    }
}
