package farmers.tech.waitingbee.Location.activity;

import android.content.Context;

import com.google.android.gms.location.DetectedActivity;

import farmers.tech.waitingbee.Location.OnActivityUpdatedListener;
import farmers.tech.waitingbee.Location.activity.config.ActivityParams;
import farmers.tech.waitingbee.Location.utils.Logger;

/**
 * Created by mrm on 3/1/15.
 */
public interface ActivityProvider {
    void init(Context context, Logger logger);

    void start(OnActivityUpdatedListener listener, ActivityParams params);

    void stop();

    DetectedActivity getLastActivity();
}
