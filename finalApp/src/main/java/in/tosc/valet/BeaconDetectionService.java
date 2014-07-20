package in.tosc.valet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.Region;

public class BeaconDetectionService extends Service implements IBeaconConsumer {
    private IBeaconManager iBeaconManager;
    public BeaconDetectionService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        iBeaconManager = IBeaconManager.getInstanceForApplication(getApplicationContext());
        if (!iBeaconManager.isBound(this)) {
            iBeaconManager.bind(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (iBeaconManager.isBound(this)) {
            iBeaconManager.unBind(this);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onIBeaconServiceConnect() {
        iBeaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i("TOSC", "didEnterRegion");

            }

            @Override
            public void didExitRegion(Region region) {
                Log.i("TOSC", "didExitRegion");

            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i("TOSC", "didDetermineStateForRegion");

            }
        });
        try {
            iBeaconManager.startMonitoringBeaconsInRegion(new Region("someId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
