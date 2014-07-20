package in.tosc.valet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    private Context mContext;

    public static final String PREFS_FILE = "PREFS_FILE";
    public static final String PREFS_REG_NAME = "PREFS_REG_NAME";
    public static final String PREFS_REG_PHONE = "PREFS_REG_PHONE";
    public static final String PREFS_REG_EMAIL = "PREFS_REG_EMAIL";
    public static final String PREFS_REG_PROFILE = "PREFS_REG_PROFILE";
    public static final String PREFS_REG_IS_REGISTERED = "PREFS_REG_IS_REGISTERED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mContext = this;

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

                if ((registerString[0].length()<1)
                        || (registerString[1].length()<1)
                        || (registerString[2].length()<1)
                        || (registerString[3].length()<1)) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("Argh you cheat !!!")
                            .setMessage("Jeez, you can't get away without" +
                                    " properly filling in your details." +
                                    " We need your details at least once to" +
                                    " be able to help you avoid queues in future.").show();
                } else {
                    RegisterTask registerTask = new RegisterTask();
                    registerTask.execute(registerString);
                }

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
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    SharedPreferences.Editor ed = getSharedPreferences(PREFS_FILE, MODE_PRIVATE).edit();
                    ed.putString(PREFS_REG_NAME, s[0]);
                    ed.putString(PREFS_REG_PHONE, s[1]);
                    ed.putString(PREFS_REG_EMAIL, s[2]);
                    ed.putString(PREFS_REG_PROFILE, s[3]);
                    ed.putBoolean(PREFS_REG_IS_REGISTERED, true);

                    Intent si = new Intent(getApplicationContext(), BeaconDetectionService.class);
                    startService(si);
                    Toast.makeText(getBaseContext(), "Successfully registered", Toast.LENGTH_SHORT);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }



}
