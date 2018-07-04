package com.reveautomation.revesmartsecuritykit_online.alluiactivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.reveautomation.revesmartsecuritykit_online.R;
import com.reveautomation.revesmartsecuritykit_online.db.SessionManager;
import com.reveautomation.revesmartsecuritykit_online.example1_scanning.DashboardActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.reveautomation.revesmartsecuritykit_online.Constraints.HttpUrl;
import static com.reveautomation.revesmartsecuritykit_online.Constraints.LOGINDIR;
import static com.reveautomation.revesmartsecuritykit_online.Constraints.TASKSDIR;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    SessionManager session;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    String name1;
    String apikey;
    String email;
    String password;
    // Creating Volley RequestQueue.
            RequestQueue requestQueue;
    // Create string variable to hold the EditText Value.
    // Creating Progress dialog.
    ProgressDialog progressDialog;

    // Storing server url into String variable.


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        // Session Manager
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        session = new SessionManager(getApplicationContext());
        // Creating Volley newRequestQueue .
        requestQueue = Volley.newRequestQueue(LoginActivity.this);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }
        _loginButton.setEnabled(false);
       // new JsonTask().execute("http://reveautomation.com/ble/Downloads/test.txt");
         email = _emailText.getText().toString();
         password = _passwordText.getText().toString();
         UserLogin();
        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        //onLoginSuccess();
                        // onLoginFailed();
                     //   progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }
    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        session.createLoginSession(name1,email,apikey);
        //ListSubGW();
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
         email = _emailText.getText().toString();
         password = _passwordText.getText().toString();
      //  if (name.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(name).matches())
            if (email.isEmpty()){
            _emailText.setError("enter username");
            valid = false;
        } else {
            _emailText.setError(null);
        }
        if (password.isEmpty()) {
            _passwordText.setError("enter password");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }



    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }
                    return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONArray jsonarray = null;
            try {
                jsonarray = new JSONArray(result);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobj = jsonarray.getJSONObject(i);
                    String rname = jsonobj.getString("name");
                    String rpassword = jsonobj.getString("password");
                    Log.d("rname "+rname,"name "+email);
                    Log.d("rpass "+rpassword,"pass "+password);
                    if (email.equals(rname)&& password.equals(rpassword)){
                        Log.d("rpass ","Success login");
                         onLoginSuccess();
                        progressDialog.dismiss();
                        return;
                    }
                }
            }catch (Exception e){

            }
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
                onLoginFailed();
                //  pd.dismiss();
            }
           // txtJson.setText(result);

        }
    }

    public void UserLogin() {

        // Showing progress dialog at user registration time.
        progressDialog.setMessage("Please Wait");
        progressDialog.show();

        // Creating string request with post method.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl+LOGINDIR,
                ServerResponse -> {
                    // Hiding the progress dialog after all task complete.
                    progressDialog.dismiss();
                     Log.d("Server Responce",ServerResponse);
                    // Matching server responce message to our text.
                    try {
                        JSONObject jsonObject = new JSONObject(ServerResponse);

                    JSONArray array = new JSONArray();
                            array.put(jsonObject);
                            for(int i = 0; i < array.length(); i++) {
                                JSONObject c = null;
                                try {
                                    c = array.getJSONObject(i);
                                    Boolean error = c.getBoolean("error");
                                    if (error == false) {
                                        String name = c.getString("name");
                                        String apiKey = c.getString("apiKey");
                                        apikey = apiKey;
                                        name1 = name;
                                        Log.d("Name ",name);
                                        onLoginSuccess();
                                    }else {
                                        onLoginFailed();
                                        Log.d("Name ","Else part");
                                    }
                                    Log.d("Server Responce 22",ServerResponse);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onLoginFailed();
                    }

                },
                volleyError -> {

                    // Hiding the progress dialog after all task complete.
                    progressDialog.dismiss();
                    onLoginFailed();
                    // Showing error message if something goes wrong.
                    Toast.makeText(LoginActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                // The firs argument should be same sa your MySQL database table columns.
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }



    public void ListSubGW() {

        // Showing progress dialog at user registration time.
      //  progressDialog.setMessage("Please Wait");
       // progressDialog.show();

        // Creating string request with post method.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, HttpUrl+TASKSDIR,
                ServerResponse -> {
                    // Hiding the progress dialog after all task complete.
                    progressDialog.dismiss();
                    Log.d("Server Responce",ServerResponse);
                    // Matching server responce message to our text.
                   /* try {
                        JSONObject jsonObject = new JSONObject(ServerResponse);

                        JSONArray array = new JSONArray();
                        array.put(jsonObject);
                        for(int i = 0; i < array.length(); i++) {
                            JSONObject c = null;
                            try {
                                c = array.getJSONObject(i);
                                Boolean error = c.getBoolean("error");
                                if (error == false) {
                                    String name = c.getString("name");
                                    String apiKey = c.getString("apiKey");
                                    apikey = apiKey;
                                    Log.d("Name ",name);
                                    onLoginSuccess();
                                }else {
                                    onLoginFailed();
                                    Log.d("Name ","Else part");
                                }
                                Log.d("Server Responce 22",ServerResponse);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/

                },
                volleyError -> {
                    // Hiding the progress dialog after all task complete.
                    progressDialog.dismiss();
                    //onLoginFailed();
                    // Showing error message if something goes wrong.
                    Toast.makeText(LoginActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("authorization", apikey);
                return params;
            }

        };
        // Creating RequestQueue.
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        // Adding the StringRequest object into requestQueue.
        requestQueue.add(stringRequest);
    }
}


