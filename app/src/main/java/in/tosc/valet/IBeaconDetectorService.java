package in.tosc.valet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.radiusnetworks.ibeacon.IBeaconConsumer;

public class IBeaconDetectorService extends Service implements IBeaconConsumer {
    public IBeaconDetectorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onIBeaconServiceConnect() {
        
    }
}
