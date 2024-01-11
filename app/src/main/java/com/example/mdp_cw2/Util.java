package com.example.mdp_cw2;

public class Util {
    public static String secondsToTimeStamp(int duration) {
        int minutes = duration / 60;
        int seconds = duration - (minutes * 60);

        if (seconds < 10) {
            // leading 0 for single-digit seconds
            return minutes + ":0" + seconds;
        }

        // else no need for leading 0
        return minutes + ":" + seconds;
    }
}
