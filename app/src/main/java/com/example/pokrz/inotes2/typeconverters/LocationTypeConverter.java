package com.example.pokrz.inotes2.typeconverters;

import android.location.Location;

import java.util.Locale;

import androidx.room.TypeConverter;

public class LocationTypeConverter  {

    @TypeConverter
    public static String fromLocation(Location location) {
        if (location == null) {
            return (null);
        }
        return (String.format(Locale.US, "%f,%f", location.getLatitude(), location.getLongitude()));
    }

    @TypeConverter
    public static Location toLocation(String latlon) {
        if (latlon == null) {
            return (null);
        }
        String[] pieces = latlon.split(",");
        Location result = new Location("");
        result.setLatitude(Double.parseDouble(pieces[0]));
        result.setLongitude(Double.parseDouble(pieces[1]));
        return (result);
    }

}
