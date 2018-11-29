package com.worksmart.alphafitness;

public class AppState {

    static AppState state = new AppState();

    public WorkoutService service;
    public Boolean serviceBound;
    public Workout workout;
    public AppState(){
        service = null;
        serviceBound = false;
        workout = null;
    }

}
