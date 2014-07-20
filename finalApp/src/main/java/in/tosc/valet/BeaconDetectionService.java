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

public class BeaconDetectionService extends Service implements IBeaconConsumer {
    private static final String TAG = "BeaconDetectionService";
    private IBeaconManager iBeaconManager;

    PendingIntent pi = null;

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
                new NotifyServerTask().execute(new Region[] {region});
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

    private class NotifyServerTask extends AsyncTask<Region, Void, String> {

        @Override
        protected String doInBackground(Region... region) {
            try {
                final HttpClient client = new DefaultHttpClient();
                String url = "http://tosc.in:8080/customer_in?email=";
                url += URLEncoder.encode("omerjerk@gmail.com");
                url += "&beacon_id=";
                url += URLEncoder.encode(region[0].getProximityUuid().toString().toUpperCase()
                        + "," + region[0].getMajor() + "," + region[0].getMinor(), "UTF-8");
                final HttpGet httpGet = new HttpGet(url);
                HttpResponse response = client.execute(httpGet);
                return EntityUtils.toString(response.getEntity());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            Log.d(TAG, "RESULT == " + response);
            try {
                Bundle data = new Bundle();
                data.putString("data", response);
                JSONArray jsonArray = new JSONArray(response);
                Intent launchIntent = new Intent(BeaconDetectionService.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                pi = PendingIntent.getActivity(BeaconDetectionService.this, 0,
                        launchIntent, PendingIntent.FLAG_UPDATE_CURRENT, data);
                generateNotification(BeaconDetectionService.this, "");
            } catch (JSONException e) {
                e.printStackTrace();
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
