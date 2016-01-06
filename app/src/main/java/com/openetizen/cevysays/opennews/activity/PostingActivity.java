package com.openetizen.cevysays.opennews.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.models.CategoryOneItem;
import com.openetizen.cevysays.opennews.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class PostingActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    private Toolbar mToolbar;

    // Progress Dialog Object
    ProgressDialog prgDialog;
    // Error Msg TextView Object
    TextView errorMsg;
    EditText titleArticle;
    Spinner spinnerCat;
    EditText contentArticle;
    ImageButton imageContent;

    boolean isEdit = false;

    int article_ID = 0;

    Bundle extras;

    private ArrayList<CategoryOneItem> dataCatOne = new ArrayList<>();
    private ArrayList<String> image = new ArrayList<String>();
    private ArrayList<String> url = new ArrayList<String>();
    private ArrayList<String> title = new ArrayList<String>();
    private ArrayList<String> created_at = new ArrayList<String>();
    private ArrayList<String> username = new ArrayList<String>();
    private ArrayList<String> content = new ArrayList<String>();
    private ArrayList<String> category_cd = new ArrayList<String>();
    private ArrayList<String> article_id = new ArrayList<String>();

    private String image_url = "";

    private SharedPreferences sharedPreferences;

    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        extras = getIntent().getExtras();



        sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, MODE_PRIVATE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
        }

        spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        titleArticle = (EditText) findViewById(R.id.title_article);
        contentArticle = (EditText) findViewById(R.id.content_article);
        imageContent = (ImageButton) findViewById(R.id.imageButton);

        if(extras!=null){
            isEdit = true;
            setTitle("Sunting artikel");
            titleArticle.setText(extras.getString("title"));
            spinner.setSelection(Integer.parseInt(extras.getString("kategori").substring(extras.getString("kategori").length()-1)));
            contentArticle.setText(Html.fromHtml(extras.getString("konten")));
        }



        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Mohon menunggu...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);


    }

    public void posting() {

        String title = titleArticle.getText().toString();

        // String category = spinnerCat.getCount();
        String content = Html.toHtml(SpannableString.valueOf(contentArticle.getText().toString()));
        String category_cd = "";
        if(spinner.getSelectedItemId()==1){
            category_cd = "CATE_TP_1";
        }else if(spinner.getSelectedItemId()==2){
            category_cd = "CATE_TP_2";
        }else if(spinner.getSelectedItemId()==3){
            category_cd = "CATE_TP_3";
        }


        File myFile = new File(image_url);

        RequestParams params = new RequestParams();
        params.put("article[user_id]", sharedPreferences.getInt("loginUserID", 0));
        params.put("article[category_cd]", category_cd);
        params.put("article[title]", title);
        params.put("article[content]", content);

        if (Utility.isNotNull(title) && Utility.isNotNull(content) && Utility.isNotNull(category_cd,this) && Utility.isNotNull(myFile,this)) {


            try {
                params.put("article[image]", new File(image_url));
            } catch (FileNotFoundException e) {
            }

            JSONObject jsonPosting = new JSONObject();
            try {
                jsonPosting.put("user_id", sharedPreferences.getInt("loginUserID", 0));
                jsonPosting.put("category_cd", category_cd);
                jsonPosting.put("title", title);
                jsonPosting.put("content", content);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Invoke RESTful Web Service with Http parameters
            if (isEdit) {
                delete(Integer.parseInt(extras.getString("article_id")), jsonPosting);
            } else {
                invokeWS(params);
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Tidak boleh ada data yang kosong!", Toast.LENGTH_LONG).show();
        }
    }

    private void delete(final int article_ID, final JSONObject json) {

        prgDialog.show();

        JSONObject jsonPosting = new JSONObject();
        try {
            jsonPosting.put("article_id",article_ID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonPosting.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.delete(this, "http://openetizen.com/api/v1/articles/" + article_ID, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // Hide Progress Dialog

                try {
                    JSONObject obj = new JSONObject(new String(String.valueOf(jsonObject)));
                    Log.d("Opo", String.valueOf(jsonObject));
                    String title = titleArticle.getText().toString();

                    // String category = spinnerCat.getCount();
                    String content = Html.toHtml(SpannableString.valueOf(contentArticle.getText().toString()));
                    String category_cd = "";
                    if(spinner.getSelectedItemId()==1){
                        category_cd = "CATE_TP_1";
                    }else if(spinner.getSelectedItemId()==2){
                        category_cd = "CATE_TP_2";
                    }else if(spinner.getSelectedItemId()==3){
                        category_cd = "CATE_TP_3";
                    }


                    File myFile = new File(image_url);

                    RequestParams params = new RequestParams();
                    params.put("article[user_id]", sharedPreferences.getInt("loginUserID", 0));
                    params.put("article[article_id]", article_ID);
                    params.put("article[category_cd]", category_cd);
                    params.put("article[title]", title);
                    params.put("article[content]", content);

                    if (Utility.isNotNull(title) && Utility.isNotNull(content)) {


                        try {
                            params.put("article[image]", new File(image_url));
                        } catch (FileNotFoundException e) {
                        }

                        JSONObject jsonPosting = new JSONObject();
                        try {
                            jsonPosting.put("user_id", sharedPreferences.getInt("loginUserID", 0));
                            jsonPosting.put("article_id", article_id);
                            jsonPosting.put("category_cd", category_cd);
                            jsonPosting.put("title", title);
                            jsonPosting.put("content", content);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                            invokeWS(params);

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Tidak boleh ada data yang kosong!", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    Log.e("ERROR", "Response");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Hide Progress Dialog

                Log.e("errorResponse", errorResponse.toString() + "  " + statusCode);

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
                    Toast.makeText(getApplicationContext(), "Gagal mengungah artikel!", Toast.LENGTH_LONG).show();
                    // Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }




    public void invokeWS(RequestParams params) {

        StringEntity entity = null;

        // Show Progress Dialog
        if(!isEdit) {
            prgDialog.show();
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://openetizen.com/api/v1/articles", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        prgDialog.hide();

                        try {
                            JSONObject obj = new JSONObject(new String(responseBody));
                            Log.v("hasil",new String(responseBody));
                            if (obj.getString("status").equalsIgnoreCase("success")) {
                                Log.e("result", obj.toString());
                                if(isEdit){
                                    Toast.makeText(getApplicationContext(), "Artikel berhasil diubah!", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "Artikel berhasil diunggah!", Toast.LENGTH_LONG).show();
                                }

                                Intent i = new Intent(PostingActivity.this, MainActivity.class);
                                startActivity(i);
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
                        Log.e("errorResponse", /*responseBody.toString()*/  "  " + statusCode);

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
                            Toast.makeText(getApplicationContext(), "Gagal mengungah artikel!", Toast.LENGTH_LONG).show();
                            // Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                        }
                    }

            }

            );

        }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void imageUpload(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);




                imgDecodableString = cursor.getString(columnIndex);
                image_url = imgDecodableString;
                cursor.close();
                ImageButton image = (ImageButton) findViewById(R.id.imageButton);
                // Set the Image in ImageView after decoding the String
                image.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "Anda belum memilih foto!",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong"+"\n"+e.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_posting, menu);
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
        } else if (id == R.id.action_send) {
            posting();
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("text/plain");
//            startActivity(Intent.createChooser(intent, "Share with"));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.title_dialog_discard);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PostingActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();


    }
}