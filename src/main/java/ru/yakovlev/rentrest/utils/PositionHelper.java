package ru.yakovlev.rentrest.utils;

public class PositionHelper {
    // Haversine formula
    public static Double calcDiffCoordinatesInMeters(Double lat1, Double lon1, Double lat2, Double lon2){
        Double r = 6378.137;
        Double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        Double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        Double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * Math.sin(dLon/2) * Math.sin(dLon/2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double d = r * c;
        return d * 1000;
    }
}
