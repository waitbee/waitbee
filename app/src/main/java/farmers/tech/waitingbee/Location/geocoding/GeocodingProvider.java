package farmers.tech.waitingbee.Location.geocoding;

import android.content.Context;
import android.location.Location;

import farmers.tech.waitingbee.Location.OnGeocodingListener;
import farmers.tech.waitingbee.Location.OnReverseGeocodingListener;
import farmers.tech.waitingbee.Location.utils.Logger;

/**
 * Created by mrm on 20/12/14.
 */
public interface GeocodingProvider {
    void init(Context context, Logger logger);

    void addName(String name, int maxResults);

    void addLocation(Location location, int maxResults);

    void start(OnGeocodingListener geocodingListener, OnReverseGeocodingListener reverseGeocodingListener);

    void stop();

}
