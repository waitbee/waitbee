package farmers.tech.waitingbee.Location.location;

import android.content.Context;
import android.location.Location;

import farmers.tech.waitingbee.Location.OnLocationUpdatedListener;
import farmers.tech.waitingbee.Location.location.config.LocationParams;
import farmers.tech.waitingbee.Location.utils.Logger;

/**
 * Created by mrm on 20/12/14.
 */
public interface LocationProvider {
    void init(Context context, Logger logger);

    void start(OnLocationUpdatedListener listener, LocationParams params, boolean singleUpdate);

    void stop();

    Location getLastLocation();

}
