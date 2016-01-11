package com.openetizen.cevysays.opennews.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.openetizen.cevysays.opennews.R;
import com.openetizen.cevysays.opennews.fragments.GalleryFragment;
import com.openetizen.cevysays.opennews.fragments.MyGalleryFragment;
import com.openetizen.cevysays.opennews.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class UploadPhotoActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    private Toolbar mToolbar;
    ProgressDialog prgDialog;
    //    private Spinner spinnerAlbum;
    private SharedPreferences sharedPreferences;
    private Bundle extras;
    EditText photoDesc;
    ImageButton imageContent;
    private String image_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        sharedPreferences = getSharedPreferences(LoginActivity.MyPREFERENCES, MODE_PRIVATE);
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Mohon menunggu...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        extras = getIntent().getExtras();
        photoDesc = (EditText) findViewById(R.id.photoDesc);
        imageContent = (ImageButton) findViewById(R.id.imageButton);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.myPrimaryDarkColor));
        }

//        spinnerAlbum = (Spinner) findViewById(R.id.spinner);
//// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.category, android.R.layout.simple_spinner_item);
//// Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//// Apply the adapter to the spinner
//        spinnerAlbum.setAdapter(adapter);
//        spinnerAlbum.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);


    }

    public void imageUpload(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the IntentprgDialog.show();


        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void uploadPhoto() {

        File myFile = new File(image_url);
        String Description = photoDesc.getText().toString();
        RequestParams params = new RequestParams();
        params.put("album[user_id]", sharedPreferences.getInt("loginUserID", 0));
        params.put("photo[album_id]", extras.getInt("album_ID"));
//        params.put("photo[description]", photoDesc.getText().toString());
        params.put("photo[description]", Description);

//        if (Utility.isNotNull(Description) && Utility.isNotNull(myFile, this)) {
        if (Utility.isNotNull(Description)) {
            if (Utility.isNotNull(myFile, this)) {
                prgDialog.show();

                try {
                    params.put("photo[photo]", new File(image_url));
//                params.put("photo[description]", Description);
                } catch (FileNotFoundException e) {
                }


                AsyncHttpClient client = new AsyncHttpClient();
                client.post("http://openetizen.com/api/v1/albums/" + extras.getInt("album_ID") + "/photos", params, new AsyncHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                prgDialog.hide();
                                try {
                                    JSONObject obj = new JSONObject(new String(responseBody));
                                    if (obj.getString("status").equalsIgnoreCase("success")) {
                                        Log.e("result", obj.toString());
                                        Toast.makeText(getApplicationContext(), "Foto berhasil diunggah!", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(UploadPhotoActivity.this, PhotosActivity.class);
                                        i.putExtra("album_ID", extras.getInt("album_ID"));
                                        i.putExtra("Fragment", "MyGallery");
                                        i.putExtra("album_Name", extras.getString("album_Name"));
                                        startActivity(i);
                                    } else {
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

        } else {
            Toast.makeText(getApplicationContext(), "Deskripsi foto tidak boleh kosong!", Toast.LENGTH_LONG).show();
        }
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
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
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
            uploadPhoto();
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
                UploadPhotoActivity.super.onBackPressed();
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
