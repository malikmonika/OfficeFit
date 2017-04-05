package edu.scu.mmalik1.photonotes;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;


public class AddPhotoTextActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,SensorEventListener {
    private Db_Helper dbHelper;
    static final int REQUEST_IMAGE_CAPTURE = 1888;
    Uri uriForFile = null,lasturi  = null;
    Uri uriForVoiceFile = null;
    boolean cameraresult=false;
    boolean voiceresult=false;

    Button saveClick,recordButton;
    Button capture;
    TextView txtLat;
    GoogleApiClient mGoogleApiClient = null;
    Location loc = null;
    Notes note;
    ObjectAnimator anim;
    boolean enablePlay = false;

    private static String mAudioFile = null;
    private MediaRecorder mRecorder = null;

    Bitmap bitmap,newBitmap;
    Canvas canvas;
    ImageView imgView;
    Paint p;
    float dx,dy,ux,uy;
    SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    Matrix m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new Db_Helper(getApplicationContext());
        setContentView(R.layout.activity_add_photo_text);
        setRequestedOrientation (SCREEN_ORIENTATION_PORTRAIT);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        recordButton = (Button)findViewById(R.id.voice);
        recordButton.setText("Record");
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        recordButton.setOnClickListener(new View.OnClickListener() {
            boolean mStartRecording = true;
            @Override
            public void onClick(View v) {
                if (mStartRecording) {
                    recordButton.setText("Stop");

                    startRecording();
                    Toast.makeText(getApplicationContext(),
                            "Recording started", Toast.LENGTH_LONG).show();

                } else {
                    recordButton.setText("Record");
                    stopRecording();
                    Toast.makeText(getApplicationContext(),
                            "Audio successfully recorded", Toast.LENGTH_LONG).show();

                }
                mStartRecording = !mStartRecording;

            }
        });

        imgView = (ImageView)findViewById(R.id.imageView);
        txtLat = (TextView) findViewById(R.id.txtLatLng);
        Button b = (Button) findViewById(R.id.pl);
        b.setEnabled(false);
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        final EditText writenote = (EditText)findViewById(R.id.input);
        saveClick = (Button) findViewById(R.id.save);
        saveClick.setClickable(false);
        saveClick.setEnabled(false);
        capture = (Button) findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v){
                takePicture();
            }
        });

        saveClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraresult == true ){
                    if (writenote.getText().toString().length() < 1) {
                        Toast.makeText(AddPhotoTextActivity.this, "Enter a Valid Note", Toast.LENGTH_SHORT).show();
                    } else {
                        // picture details saved with location
                        String audiouri = "na";
                        String lloc="0";
                        String llan="0";
                        if(loc !=null)
                        {
                            lloc = String.valueOf(loc.getLatitude());
                            llan = String.valueOf(loc.getLongitude());
                        }
                        bitmap = null;
                        if(uriForVoiceFile != null)
                            audiouri = uriForVoiceFile.toString();

                        try {
                            File f = new File(lasturi.getPath());
                            f.delete();
                            FileOutputStream fmp = new FileOutputStream(lasturi.getPath());
                            newBitmap.compress(Bitmap.CompressFormat.JPEG,80,fmp);
                        }catch(Exception e)
                        {

                        }
                        note = new Notes(writenote.getText().toString(),
                                lasturi.toString(),lloc ,llan
                                ,audiouri);
                        dbHelper.open();
                        dbHelper.insertNotesDetails(note);
                        finish();
                    }
                }
            }
        });
        mGoogleApiClient.connect();
    }


    public void takePicture() {
        String imageFileName = "JPEG";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image  = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        uriForFile = Uri.fromFile(image);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }


    public void onRecordVoice() {
        // recording audio
        String voiceFileName = "3GP";
        File storageDir = Environment.getExternalStorageDirectory();
        File voicefile  = null;
        try {
            voicefile = File.createTempFile(
                    voiceFileName,  /* prefix */
                    ".3gp",         /* suffix */
                    storageDir      /* directory */
            );

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        mAudioFile = voicefile.getAbsolutePath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            saveClick.setClickable(true);
            saveClick.setEnabled(true);
            lasturi = uriForFile;
            try {
                //imgView.setOnTouchListener(this);
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(lasturi));
                newBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmap.getConfig());
                imgView.setMaxHeight(bitmap.getHeight()-200);
                imgView.setMaxWidth(bitmap.getWidth()-100);
                imgView.setImageBitmap(newBitmap);
                canvas = new Canvas(newBitmap);
                p = new Paint();
                p.setColor(Color.GREEN);
                p.setStrokeWidth(20);
                m = new Matrix();
                canvas.drawBitmap(bitmap,m,p);
                imgView.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = event.getAction();
                        switch (action) {
                            case MotionEvent.ACTION_MOVE:
                                uy = event.getY();
                                ux = event.getX();
                                canvas.drawLine(dx, dy, ux, uy, p);
                                imgView.invalidate();
                                dx = ux;
                                dy = uy;
                                break;
                            case MotionEvent.ACTION_DOWN:
                                dy = event.getY();
                                dx = event.getX();
                                break;
                            case MotionEvent.ACTION_UP:
                                uy = event.getY();
                                ux = event.getX();
                                canvas.drawLine(dx, dy, ux, uy, p);
                                imgView.invalidate();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
            } catch (Exception e)
            {

            }

            // Save a file: path for use with ACTION_VIEW intents
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(lasturi);
            this.sendBroadcast(mediaScanIntent);
            cameraresult = true;
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        try {
            loc = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if(loc!=null)
            txtLat.setText(String.valueOf(loc.getLatitude())+" , "+String.valueOf(loc.getLongitude()));

        }
        catch(SecurityException e)
        {

        }
    }

    @Override
    public void onConnectionSuspended(int a)
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult a)
    {

    }

    private void startRecording() {

        //start button flashing

        mRecorder = new MediaRecorder();
        onRecordVoice();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mAudioFile);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        //lets animate button
        anim = ObjectAnimator.ofObject(recordButton,"backgroundColor",
                new ArgbEvaluator(),0xFFFFFFFF,0xFFD22B2B);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setDuration(300);
        anim.start();
        try {
            mRecorder.prepare();

        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            Log.d("ERROR ", "IllegalStateException");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("ERROR ", "IOException");
            e.printStackTrace();
        }
        try {
            mRecorder.start();
        } catch (Exception e) {

        }
    }

    private void stopRecording() {
        mRecorder.stop();
        uriForVoiceFile = Uri.fromFile(new File(mAudioFile));
        mRecorder.release();
        mRecorder = null;
        // Save a audio file
        voiceresult = true;
        anim.setRepeatCount(0);
        recordButton.setBackgroundResource(android.R.drawable.btn_default);
        recordButton.clearAnimation();
        final Button b = (Button) findViewById(R.id.pl);
        b.setEnabled(true);
        final MediaPlayer mp = MediaPlayer.create(this,uriForVoiceFile);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mp.isPlaying())
                {
                    mp.start();
                }
                else
                {
                    mp.pause();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    public void onSensorChanged(SensorEvent se) {
        float x = se.values[0];
        float y = se.values[1];
        float z = se.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta * 0.1f; // perform low-cut filter
        float accel = Math.abs( mAccel);
        if (accel > 1.0f && lasturi != null) {
            try {
                canvas = new Canvas(newBitmap);
                imgView.setImageBitmap(newBitmap);
                canvas.drawBitmap(bitmap,m,p);
            }
            catch(Exception e)
            {

            }
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}








