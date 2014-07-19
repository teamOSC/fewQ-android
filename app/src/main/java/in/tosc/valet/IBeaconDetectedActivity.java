package in.tosc.valet;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by omerjerk on 19/7/14.
 */
public class IBeaconDetectedActivity extends Activity {

    private static final String URL= "http://tosc.in:8080/customer_in";

    private ProgressBar progressBar;
    private Context context;
    private RelativeLayout errorLayout;
    private RelativeLayout successLayout;
    private ListView mainListView;

    JSONArray itemsArray = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibeacon_detected);
        context = getApplicationContext();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
        successLayout = (RelativeLayout) findViewById(R.id.success_layout);
        mainListView = (ListView) findViewById(R.id.main_list_view);

        new GetDataTask().execute();
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(URL);
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("email", Utils.getEmail(context)));
            parameters.add(new BasicNameValuePair("beacon_id", "123456789"));
            HttpResponse httpResponse;
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(parameters));
                httpResponse = httpClient.execute(httpPost);
                return EntityUtils.toString(httpResponse.getEntity());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(result);
                itemsArray = object.getJSONArray("items");
                String type = object.getString("type");
                successLayout.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
                errorLayout.setVisibility(View.VISIBLE);
                TextView errorView = (TextView) findViewById(R.id.error_text_view);
                errorView.setText("Something derped!");
            }
        }
    }

    private class ListDataAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public ListDataAdapter (Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return itemsArray.length();
        }

        @Override
        public JSONObject getItem(int i){
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflater.inflate(R.layout.row_main_list_view, viewGroup, false);

            }
            return null;
        }
    }

    //TODO: implement the viewholder
    private static class ViewHolder {
        //TextView title
    }
}
