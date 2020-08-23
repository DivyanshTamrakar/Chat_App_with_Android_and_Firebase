package example.android.chattingapp;

import android.app.Application;
import android.content.Context;

public class GetTimeAgo extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final double DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final double WEEK_MILLIS = 7 * DAY_MILLIS;
    private static final double MONTH_MILLIS = DAY_MILLIS * 30;
    private static final double YEAR_MILLIS = WEEK_MILLIS * 52;

    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {

            double roundup = diff / MINUTE_MILLIS;
            int b = (int) (roundup);
            return b + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            double roundup = diff / HOUR_MILLIS;
            int b = (int) (roundup);
            return b + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else if (diff < 7 * DAY_MILLIS) {
            double roundup = diff / DAY_MILLIS;
            int b = (int) (roundup);
            return b + " days ago";
        } else if (diff < 2 * WEEK_MILLIS) {
            return "a week ago";
        } else if (diff < DAY_MILLIS * 30.43675) {
            double roundup = diff / WEEK_MILLIS;
            int b = (int) (roundup);
            return b + " weeks ago";
        } else if (diff < 2 * MONTH_MILLIS) {
            return "a month ago";
        } else if (diff < WEEK_MILLIS * 52.2) {
            double roundup = diff / MONTH_MILLIS;
            int b = (int) (roundup);
            return b + " months ago";
        } else if (diff < 2 * YEAR_MILLIS) {
            return "a year ago";
        } else {
            double roundup = diff / YEAR_MILLIS;
            int b = (int) (roundup);
            return b + " years ago";
        }
    }
}