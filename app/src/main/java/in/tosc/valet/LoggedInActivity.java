package in.tosc.valet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;

import in.tosc.valet.beacon.IBeaconDetectorService;

public class LoggedInActivity extends Activity implements IBeaconConsumer {

    //private BeaconServiceUtility beaconUtill = null;
    //private IBeaconManager iBeaconManager = IBeaconManager.getInstanceForApplication(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        //beaconUtill = new BeaconServiceUtility(this);
        //beaconUtill = new BeaconServiceUtility(this);
        startService(new Intent(this, IBeaconDetectorService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //beaconUtill.onStart(iBeaconManager, this);
    }

    @Override
    protected void onStop() {
        //beaconUtill.onStop(iBeaconManager, this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logged_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startTempActivity(View v) {
        //startActivity(new Intent(this, IBeaconDetectedActivity.class));
    }

    @Override
    public void onIBeaconServiceConnect() {

    }
}
