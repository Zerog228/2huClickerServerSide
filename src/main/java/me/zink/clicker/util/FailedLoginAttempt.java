package me.zink.clicker.util;

import java.util.HashSet;
import java.util.Set;

public class FailedLoginAttempt {
    public static final int MAX_ATTEMPTS = 6;
    public static final long EXPIRATION_TIME = 1000 * 60 * 60;

    private long initial_timestamp = 0;
    private Set<Long> timestamps = new HashSet<>(10);

    public FailedLoginAttempt addOrCreate(FailedLoginAttempt attempt, long timestamp){
        if(attempt != null && !attempt.expired(timestamp)){
            initial_timestamp = attempt.initial_timestamp;
            timestamps = attempt.timestamps;
        }else{
            initial_timestamp = timestamp;
        }
        timestamps.add(timestamp);
        return this;
    }

    public boolean exceedLimit(){
        return timestamps.size() > MAX_ATTEMPTS;
    }

    public boolean expired(long current_time){
        return initial_timestamp + EXPIRATION_TIME < current_time;
    }

    public boolean blocked(long current_time){
        return exceedLimit() && !expired(current_time);
    }

}
