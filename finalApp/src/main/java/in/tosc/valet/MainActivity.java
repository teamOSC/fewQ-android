package in.tosc.valet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

import java.util.Collection;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSharedPreferences(RegisterActivity.PREFS_FILE, MODE_PRIVATE).getBoolean(RegisterActivity.PREFS_REG_IS_REGISTERED, false)) {
            setContentView(R.layout.activity_main);
            Log.d("TOSC", "inflating registered layout");
        } else {
            Log.d("TOSC", "inflating unregistered layout");
            setContentView(R.layout.activity_main_unregistered);
        }
    }

    public void goToRegisterScreen (View v) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.action_register:
            {
                Intent i = new Intent(this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
            break;
            case R.id.action_startservice:
            {
                Intent si = new Intent(this, BeaconDetectionService.class);
                startService(si);
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }


}
