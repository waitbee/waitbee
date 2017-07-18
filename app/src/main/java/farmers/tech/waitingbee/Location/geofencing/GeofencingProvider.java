package farmers.tech.waitingbee.Location.geofencing;

import android.content.Context;

import java.util.List;

import farmers.tech.waitingbee.Location.OnGeofencingTransitionListener;
import farmers.tech.waitingbee.Location.geofencing.model.GeofenceModel;
import farmers.tech.waitingbee.Location.utils.Logger;

/**
 * Created by mrm on 20/12/14.
 */
public interface GeofencingProvider {
    void init(Context context, Logger logger);

    void start(OnGeofencingTransitionListener listener);

    void addGeofence(GeofenceModel geofence);

    void addGeofences(List<GeofenceModel> geofenceList);

    void removeGeofence(String geofenceId);

    void removeGeofences(List<String> geofenceIds);

    void stop();

}
