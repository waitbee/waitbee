package farmers.tech.waitingbee.Location;

import java.util.List;

import farmers.tech.waitingbee.Location.geocoding.utils.LocationAddress;

/**
 * Created by mrm on 4/1/15.
 */
public interface OnGeocodingListener {
    void onLocationResolved(String name, List<LocationAddress> results);
}