package in.tosc.valet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransactionActivity extends Activity {

    private static final String TAG = "TransactionActivity";

    JSONObject jsonObject = null;

    LayoutInflater mInflater = null;

    LinearLayout CouponsParentLayout = null;
    LinearLayout historyParentLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        CouponsParentLayout = (LinearLayout) findViewById(R.id.coupons_parent);
        historyParentLayout = (LinearLayout) findViewById(R.id.history_parent);
        mInflater = LayoutInflater.from(this);
        String response = getIntent().getStringExtra("data");
        try {
            Log.d("omerjerk", "Response  = " + response);
            jsonObject = new JSONObject(response);
            JSONArray couponsArray = jsonObject.getJSONArray("items");
            JSONArray historyArray = jsonObject.getJSONArray("history");
            for (int i =0; i < historyArray.length(); ++i) {
                JSONObject mObject = historyArray.getJSONObject(i);
                LinearLayout historyLayout = (LinearLayout) mInflater.inflate(R.layout.row_history_layout, null);
                TextView name = (TextView) historyLayout.findViewById(R.id.name);
                name.setText(mObject.getString("name"));
                TextView date = (TextView) historyLayout.findViewById(R.id.date);
                date.setText(mObject.getString("date"));
                historyParentLayout.addView(historyLayout);
            }
            String token = jsonObject.getString("token");
            TextView tokenTextView = (TextView) findViewById(R.id.token);
            tokenTextView.setText(token);
            for (int i = 0; i < couponsArray.length(); ++i) {
                JSONObject mObject = couponsArray.getJSONObject(i);
                LinearLayout couponsLayout = (LinearLayout) mInflater.inflate(R.layout.row_coupon_layout, null);
                ImageView mImageView = (ImageView) couponsLayout.findViewById(R.id.coupon_preview);
                TextView couponTitle = (TextView) couponsLayout.findViewById(R.id.coupon_title);
                if (couponTitle != null)
                    couponTitle.setText(mObject.getString("title"));

                TextView couponDesc = (TextView) couponsLayout.findViewById(R.id.coupon_description);
                couponDesc.setText(mObject.getString("text"));
                // but for brevity, use the ImageView specific builder...
                Ion.with(mImageView)
                        .load(mObject.getString("image"));
                CouponsParentLayout.addView(couponsLayout);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.transaction, menu);
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
}
