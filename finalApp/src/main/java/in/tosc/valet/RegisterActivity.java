package in.tosc.valet;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import in.tosc.valet.R;

public class RegisterActivity extends Activity {

    private EditText nameEdTxt;
    private EditText phoneEdTxt;
    private EditText emailEdTxt;
    private EditText addressEdTxt;

    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEdTxt = (EditText) findViewById(R.id.register_field_name);
        phoneEdTxt = (EditText) findViewById(R.id.register_field_phone);
        emailEdTxt = (EditText) findViewById(R.id.register_field_email);
        addressEdTxt = (EditText) findViewById(R.id.register_field_address);

        submitButton = (Button) findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] registerString = new String[4];
                registerString[0] = nameEdTxt.getText().toString();
                registerString[1] = phoneEdTxt.getText().toString();
                registerString[2] = emailEdTxt.getText().toString();
                registerString[3] = addressEdTxt.getText().toString();
                RegisterTask registerTask = new RegisterTask();
                registerTask.execute(registerString);
            }
        });


    }


    public class RegisterTask extends AsyncTask <String, Integer, Integer> {


        @Override
        protected Integer doInBackground(String... s) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://tosc.in:8080/user_register");
            List<NameValuePair> postPairs = new ArrayList<NameValuePair>(4);
            postPairs.add(new BasicNameValuePair("name", s[0]));
            postPairs.add(new BasicNameValuePair("email", s[2]));
            postPairs.add(new BasicNameValuePair("phone", s[1]));
            postPairs.add(new BasicNameValuePair("profile", s[3]));

            try {
                httppost.setEntity(new UrlEncodedFormEntity(postPairs));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                HttpResponse httpResponse = httpClient.execute(httppost);
                Log.d("TOSC", httpResponse.getStatusLine().getStatusCode() + httpResponse.getEntity().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }



}
