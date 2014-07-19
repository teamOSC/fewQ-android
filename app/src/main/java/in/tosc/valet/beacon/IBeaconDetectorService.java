package in.tosc.valet.beacon;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import in.tosc.valet.LoggedInActivity;
import in.tosc.valet.R;
import in.tosc.valet.Utils;

public class IBeaconDetectorService extends Service implements IBeaconConsumer {

    private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);

    private static final String TAG = "IBeaconDetectorService";

    boolean beaconDetected = false;

    public IBeaconDetectorService() {
    }

    @Override
    public void onCreate() {
        iBeaconManager.bind(this);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //iBeaconManager.bind(this);
        Log.d(TAG, "Service Started");
        return START_STICKY;
    }

    @Override
    public void onIBeaconServiceConnect() {

        Log.d("omerjerk", "onIBeaconServiceConnect");

        iBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
                if (!beaconDetected) {
                    beaconDetected = true;
                    Iterator<IBeacon> iterator = iBeacons.iterator();
                    int minDistance = 0;
                    int minIndex = 0;
                    for (IBeacon beacon : iBeacons) {
                        if (minDistance > beacon.getRssi()) {
                            minDistance = beacon.getRssi();
                            ++minIndex;
                        }
                    }
                    while (iterator.hasNext()) {
                        IBeacon iBeacon = iterator.next();
                        if (minDistance == iBeacon.getRssi()) {
                            new NotifyServerTask().execute(new IBeacon[] {iBeacon});
                        }
                    }

                }
            }

        });

        iBeaconManager.setMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(Region region) {
                Log.d("omerjerk", "First time saw the IBeacon");
                //new NotifyServerTask().execute(new Region[] {region});
            }

            @Override
            public void didExitRegion(Region region) {
                Log.d("omerjerk", "Stopped seeing the IBeacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.e("BeaconDetactorService", "didDetermineStateForRegion:" + state);
            }
        });

        try {
            iBeaconManager.startMonitoringBeaconsInRegion(new Region("myRangingUniqueId", null, 1, 1));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            iBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        iBeaconManager.unBind(this);
        super.onDestroy();

    }

    private class NotifyServerTask extends AsyncTask<IBeacon, Void, String> {

        @Override
        protected String doInBackground(IBeacon... iBeacon) {
            try {
                final HttpClient client = new DefaultHttpClient();
                String url = "http://tosc.in:8080/customer_in?email=";
                url += URLEncoder.encode(Utils.getEmail(IBeaconDetectorService.this), "UTF-8");
                url += "&beacon_id=";
                url += URLEncoder.encode(iBeacon[0].getProximityUuid().toString().toUpperCase()
                        + "," + iBeacon[0].getMajor() + "," + iBeacon[0].getMinor(), "UTF-8");
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
        protected void onPostExecute (String response) {
            Log.d(TAG, "RESULT == " + response);
        }
    }

    private static void generateNotification(Context context, String message) {

        Intent launchIntent = new Intent(context, LoggedInActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                0,
                new NotificationCompat.Builder(context).setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_launcher).setTicker(message)
                        .setContentTitle(context.getString(R.string.app_name)).setContentText(message)
                        .setContentIntent(PendingIntent.getActivity(context, 0, launchIntent, 0)).setAutoCancel(true)
                        .build()
        );

    }
}
