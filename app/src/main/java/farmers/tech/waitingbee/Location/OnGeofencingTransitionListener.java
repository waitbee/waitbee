package farmers.tech.waitingbee.Location;

import farmers.tech.waitingbee.Location.geofencing.utils.TransitionGeofence;

/**
 * Created by mrm on 4/1/15.
 */
public interface OnGeofencingTransitionListener {
    void onGeofenceTransition(TransitionGeofence transitionGeofence);
}