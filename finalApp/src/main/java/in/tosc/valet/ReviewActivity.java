package in.tosc.valet;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EncodingUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import in.tosc.valet.R;

public class ReviewActivity extends Activity {

    String beaconId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        beaconId = getIntent().getStringExtra("beacon_id");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.review, menu);
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

    public void submitReview (View view) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                Toast.makeText(ReviewActivity.this, "Review Submitted", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }

    private class SubmitReviewTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            String url = "http://tosc.in:8080/feedback?email=";
            try {
                url += URLEncoder.encode(Utils.getEmail(ReviewActivity.this), "UTF-8");
                url += "&beacon_id=";
                url += URLEncoder.encode(beaconId);
                url += "&rating";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            HttpClient httpClient = new DefaultHttpClient();

            return null;
        }
    }
}
