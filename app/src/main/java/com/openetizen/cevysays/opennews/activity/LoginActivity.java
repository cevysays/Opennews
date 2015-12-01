package com.openetizen.cevysays.opennews.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.util.Utility;
import com.parse.LogInCallback;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import cz.msebera.android.httpclient.Header;


public class LoginActivity extends ActionBarActivity {
    // Progress Dialog Object
    ProgressDialog prgDialog;
    // Error Msg TextView Object
    TextView errorMsg;
    // Email Edit View Object
    EditText emailUser;
    // Passwprd Edit View Object
    EditText pwdUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
        }

//        TextView help = (TextView) findViewById(R.id.help);
//        help.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Starting a new Intent for about
//                Intent i = new Intent(LoginActivity.this, DeveloperActivity.class);
//
//                startActivity(i);
//
//            }
//        });

        TextView forgotPwd = (TextView) findViewById(R.id.forgot_password);
        forgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"cs@openetizen.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Lupa password");
                email.putExtra(Intent.EXTRA_TEXT, "tolong kirim ulang kata sandi");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Pilih aplikasi untuk mengirim email :"));
            }
        });

//        Button login = (Button) findViewById(R.id.login_button);
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent i = new Intent(LoginActivity.this, MainActivityUser.class);
////                startActivity(i);
//
//
//            }
//        });


        //Login with loopJ

        // Find Error Msg Text View control by ID
//        errorMsg = (TextView)findViewById(R.id.login_error);
        // Find Email Edit View control by ID
        emailUser = (EditText) findViewById(R.id.txtUserEmail);
        // Find Password Edit View control by ID
        pwdUser = (EditText) findViewById(R.id.txtUserPwd);
        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Mohon menunggu...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);


    }

    public void loginUser(View view) {
//        // Get Email Edit View Value
//        String email = emailUser.getText().toString();
//        // Get Password Edit View Value
//        String password = pwdUser.getText().toString();
//        // Instantiate Http Request Param Object
//        RequestParams params = new RequestParams();
//        // When Email Edit View and Password Edit View have values other than Null
//        if (Utility.isNotNull(email) && Utility.isNotNull(password)) {
//            // When Email entered is Valid
//            if (Utility.validate(email)) {
//                // Put Http parameter username with value of Email Edit View control
//                params.put("email", email);
//                // Put Http parameter password with value of Password Edit Value control
//                params.put("password", password);
//                // Invoke RESTful Web Service with Http parameters
//                invokeWS(params);
//            }
//            // When Email is invalid
//            else {
//                Toast.makeText(getApplicationContext(), "Alamat email tidak benar", Toast.LENGTH_LONG).show();
//            }
//        }
//        // When any of the Edit View control left blank
//        else {
//            Toast.makeText(getApplicationContext(), "Alamat email dan password tidak boleh kosong", Toast.LENGTH_LONG).show();
//        }
        prgDialog.show();
        ParseUser.logInInBackground(emailUser.getText().toString(), pwdUser.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                prgDialog.dismiss();
                if (user != null) {
                    // Hooray! The user is logged in.
                    Toast.makeText(getApplicationContext(), "Login berhasil", Toast.LENGTH_LONG).show();

                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.putExtra("login_name",emailUser.getText().toString());
                            startActivity(i);
                } else {
                    Log.e("ParseException",e.toString());
                    // Signup failed. Look at the ParseException to see what happened.
                    Toast.makeText(getApplicationContext(), "Alamat email dan kata sandi tidak cocok", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Method that performs RESTful webservice invocations
     *
     * @param params
     */
    public void invokeWS(RequestParams params) {
        // Show Progress Dialog
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("openetizen.com/api/v1/users/create", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
                prgDialog.hide();
                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(String.valueOf(responseBody));
                    Log.v("Response", String.valueOf(responseBody));
                    // When the JSON response has status boolean value assigned with true
                    if (obj.getBoolean("status")) {
                        Toast.makeText(getApplicationContext(), "Selamat datang!", Toast.LENGTH_LONG).show();
                        // Navigate to Home screen
                        navigatetoMainActivity();
                    }
                    // Else display error message
                    else {
                        errorMsg.setText(obj.getString("error_msg"));
                        Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.e("ERROR", "Response");


                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // Hide Progress Dialog
                prgDialog.hide();
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * Method which navigates from Login Activity to Home Activity
     */
    public void navigatetoMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
