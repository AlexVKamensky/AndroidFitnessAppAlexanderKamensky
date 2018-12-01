package com.worksmart.alphafitness;

public class Workout {
    private Integer id;
    private Integer totalCalories;
    private Integer time;
    private double distance;
    private Long startTime;

    public static Integer idCount=0;


    public Workout(){
        this.id = idCount;
        idCount = idCount +1;
        AlphaFtinessModel.model.profile.workouts.add(this);
    }

    public Workout(Integer id, Long startTime, Integer time, Integer totalCalories, double distance){
        this.id = id;
        this.startTime = startTime;
        this.totalCalories = totalCalories;
        this.time = time;
        this.distance = distance;
        AlphaFtinessModel.model.profile.workouts.add(this);
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public void setTotalCalories(Integer totalCalories) {
        this.totalCalories = totalCalories;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Integer getTotalCalories() {
        return totalCalories;
    }

    public Integer getTime() {
        return time;
    }

    public double getDistance() {
        return distance;
    }

    public Long getStartTime(){
        return startTime;
    }
    public Integer getId() {
        return id;
    }
}
