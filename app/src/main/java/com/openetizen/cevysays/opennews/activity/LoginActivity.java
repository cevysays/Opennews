package com.openetizen.cevysays.opennews.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;


public class LoginActivity extends ActionBarActivity {
    // Progress Dialog Object
    ProgressDialog prgDialog;
    // Error Msg TextView Object
    TextView errorMsg;
    // Email Edit View Object
    EditText emailUser;
    // Passwprd Edit View Object
    EditText pwdUser;

    public static final String MyPREFERENCES = "MyPrefs";
    //    public static final String Email = "emailKey";
//    public static final String Password = "passwordKey";
    SharedPreferences sharedpreferences;

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

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


    }

    public void loginUser(View view) {
        // Get Email Edit View Value
        String email = emailUser.getText().toString();
        // Get Password Edit View Value
        String password = pwdUser.getText().toString();
        // Instantiate Http Request Param Object
        RequestParams params = new RequestParams();


        // When Email Edit View and Password Edit View have values other than Null
        if (Utility.isNotNull(email) && Utility.isNotNull(password)) {

            // When Email entered is Valid
            if (Utility.validate(email)) {
                // Put Http parameter username with value of Email Edit View control
                params.put("email", email);
                // Put Http parameter password with value of Password Edit Value control
                params.put("password", password);
                // Invoke RESTful Web Service with Http parameters
                invokeWS(params);
            }
            // When Email is invalid
            else {
                Toast.makeText(getApplicationContext(), "Alamat email tidak benar", Toast.LENGTH_LONG).show();
            }
        }
        // When any of the Edit View control left blank
        else {
            Toast.makeText(getApplicationContext(), "Alamat email dan password tidak boleh kosong", Toast.LENGTH_LONG).show();
        }
//        prgDialog.show();
//        ParseUser.logInInBackground(emailUser.getText().toString(), pwdUser.getText().toString(), new LogInCallback() {
//            @Override
//            public void done(ParseUser user, com.parse.ParseException e) {
//                prgDialog.dismiss();
//                if (user != null) {
//                    // Hooray! The user is logged in.
//                    Toast.makeText(getApplicationContext(), "Login berhasil", Toast.LENGTH_LONG).show();
//
//                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                    i.putExtra("login_name",emailUser.getText().toString());
//                            startActivity(i);
//                } else {
//                    Log.e("ParseException",e.toString());
//                    // Signup failed. Look at the ParseException to see what happened.
//                    Toast.makeText(getApplicationContext(), "Alamat email dan kata sandi tidak cocok", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
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
        client.post("http://openetizen.com/api/v1/sessions", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Hide Progress Dialog
                prgDialog.hide();
//                try {
//                    // JSON Object
//                    JSONObject obj = new JSONObject(String.valueOf(responseBody));
//                    Log.v("Response", String.valueOf(responseBody));
//                    // When the JSON response has status boolean value assigned with true
//                    if (obj.getBoolean("status")) {
//                        Toast.makeText(getApplicationContext(), "Selamat datang!", Toast.LENGTH_LONG).show();
//                        // Navigate to Home screen
//                        navigatetoMainActivity();
//                    }
//                    // Else display error message
//                    else {
//                        errorMsg.setText(obj.getString("error_msg"));
//                        Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
//                    }

                try {
                    JSONObject obj = new JSONObject(new String(responseBody));
                    if (obj.getString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(getApplicationContext(), "Selamat datang!", Toast.LENGTH_LONG).show();
//                        navigatetoMainActivity();
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean("login", true);
                        Log.e("Response", new String(responseBody));
                        editor.putString("loginName", obj.getJSONObject("data").getJSONObject("user").getString("email")); //nanti diganti "name" nek backend nya udah siap
                        editor.putString("loginEmail",obj.getJSONObject("data").getJSONObject("user").getString("email"));
                        editor.putString("loginPass", pwdUser.getText().toString());
                        editor.putInt("loginUserID",obj.getJSONObject("data").getJSONObject("user").getInt("id"));
                        editor.commit();

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("login_name", emailUser.getText().toString());
                        startActivity(i);
                        finish();
                    } else {
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
                    Toast.makeText(getApplicationContext(), "Alamat email dan password tidak cocok", Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected Message obtainMessage(int responseMessageId, Object responseMessageData) {
                return super.obtainMessage(responseMessageId, responseMessageData);
            }

            @Override
            public Object getTag() {
                return super.getTag();
            }

            @Override
            public void setTag(Object TAG) {
                super.setTag(TAG);
            }

            @Override
            public URI getRequestURI() {
                return super.getRequestURI();
            }

            @Override
            public void setRequestURI(URI requestURI) {
                super.setRequestURI(requestURI);
            }

            @Override
            public Header[] getRequestHeaders() {
                return super.getRequestHeaders();
            }

            @Override
            public void setRequestHeaders(Header[] requestHeaders) {
                super.setRequestHeaders(requestHeaders);
            }

            @Override
            public boolean getUseSynchronousMode() {
                return super.getUseSynchronousMode();
            }

            @Override
            public void setUseSynchronousMode(boolean sync) {
                super.setUseSynchronousMode(sync);
            }

            @Override
            public boolean getUsePoolThread() {
                return super.getUsePoolThread();
            }

            @Override
            public void setUsePoolThread(boolean pool) {
                super.setUsePoolThread(pool);
            }

            @Override
            public String getCharset() {
                return super.getCharset();
            }

            @Override
            public void setCharset(String charset) {
                super.setCharset(charset);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                super.onPreProcessResponse(instance, response);
            }

            @Override
            public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                super.onPostProcessResponse(instance, response);
            }

            @Override
            public void onRetry(int retryNo) {
                super.onRetry(retryNo);
            }

            @Override
            public void onCancel() {
                super.onCancel();
            }

            @Override
            public void onUserException(Throwable error) {
                super.onUserException(error);
            }

            @Override
            protected void handleMessage(Message message) {
                super.handleMessage(message);
            }

            @Override
            protected void sendMessage(Message msg) {
                super.sendMessage(msg);
            }

            @Override
            protected void postRunnable(Runnable runnable) {
                super.postRunnable(runnable);
            }

            @Override
            public void sendResponseMessage(HttpResponse response) throws IOException {
                super.sendResponseMessage(response);
            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public boolean equals(Object o) {
                return super.equals(o);
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public String toString() {
                return super.toString();
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
