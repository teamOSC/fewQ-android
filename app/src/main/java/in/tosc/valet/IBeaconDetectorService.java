package in.tosc.valet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class IBeaconDetectorService extends Service {
    public IBeaconDetectorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
