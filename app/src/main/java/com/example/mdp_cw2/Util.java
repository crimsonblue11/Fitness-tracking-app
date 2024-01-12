/**
 * Utility class.
 */

package com.example.mdp_cw2;

public class Util {

    /**
     * Static method to convert duration in seconds to a timestamp, e.g. "4:34" (4 minutes, 34 secs)
     * @param duration Duration in seconds
     * @return Converted timestamp
     */
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
