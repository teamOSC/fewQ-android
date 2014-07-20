package in.tosc.valet;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

public class BeaconDetectionService extends Service implements IBeaconConsumer {
    private static final String TAG = "BeaconDetectionService";
    private IBeaconManager iBeaconManager;

    private IBeacon nearestBeacon = null;
    private double leastDist = 6.0;

    boolean beaconDetected = false;
    PendingIntent pi = null;

    private String lastUuid = "";

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
                Intent intent = new Intent(BeaconDetectionService.this, ReviewActivity.class);
                if (nearestBeacon != null) {
                    intent.putExtra("beacon_id", nearestBeacon.getProximityUuid());
                } else {
                    intent.putExtra("beacon_id", "1");
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i("TOSC", "didDetermineStateForRegion");

            }
        });
        try {iBeaconManager.startMonitoringBeaconsInRegion(new Region("someId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        iBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
                for (IBeacon ib : iBeacons) {
                    if ((ib.getAccuracy() <= leastDist) && (ib.getAccuracy()>0)) {
                        Log.d ("TOSC", "accuracy is better, attaching new beacon");
                        leastDist = ib.getAccuracy();
                        nearestBeacon = ib;
                    }
                }
                if (leastDist <= 4.0) {
                    if (!lastUuid.equals(nearestBeacon.getProximityUuid())) {
                        (new NotifyServerTask()).execute(nearestBeacon);
                        lastUuid = nearestBeacon.getProximityUuid();
                    }
                }
            }
        });
        try {
            iBeaconManager.startRangingBeaconsInRegion(new Region("someId2", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class NotifyServerTask extends AsyncTask<IBeacon, Void, String> {

        @Override
        protected String doInBackground(IBeacon... iBeacons) {
            try {
                final HttpClient client = new DefaultHttpClient();
                String url = "http://tosc.in:8080/customer_in?email=";
                url += URLEncoder.encode("omerjerk@gmail.com");
                url += "&beacon_id=";
                Log.d("TOSC", iBeacons[0].getProximityUuid());
                url += URLEncoder.encode(iBeacons[0].getProximityUuid().toUpperCase()
                        + "," + iBeacons[0].getMajor() + "," + iBeacons[0].getMinor(), "UTF-8");
                final HttpGet httpGet = new HttpGet(url);
                HttpResponse response = client.execute(httpGet);
                return EntityUtils.toString(response.getEntity());
            } catch (UnsupportedEncodingException e) {
                Log.e("TOSC", "UnsupportedEncodingException in doInBackground", e);
            } catch (IOException e) {
                Log.e("TOSC", "IOException in doInBackground", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d(TAG, "RESULT == " + response);
            try {
                Bundle data = new Bundle();
                data.putString("data", response);
                Intent launchIntent = new Intent(BeaconDetectionService.this, TransactionActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                launchIntent.putExtra("data", response);
                pi = PendingIntent.getActivity(BeaconDetectionService.this, 0,
                        launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                generateNotification(BeaconDetectionService.this, "Welcome to our store!");
            } catch (Exception e) {
                Log.e("TOSC", "jsonexception in postexecute", e);
            }

        }
    }

    private void generateNotification(Context context, String message) {

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                0,
                new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_launcher).setTicker(message)
                        .setContentTitle(context.getString(R.string.app_name)).setContentText(message)
                        .setContentIntent(pi).setAutoCancel(true)
                        .build()
        );
    }
}
