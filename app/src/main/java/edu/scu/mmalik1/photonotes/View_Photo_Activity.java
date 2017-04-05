package edu.scu.mmalik1.photonotes;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;


public class View_Photo_Activity extends AppCompatActivity {
    private MediaPlayer mp;
    Intent i = null;
    boolean play = false;
    SeekBar sb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i = getIntent();
        setContentView(R.layout.activity_view__photo_);
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
        TextView notename = (TextView) findViewById(R.id.text);
        notename.setText(i.getStringExtra("text"));
        ImageView notephoto = (ImageView) findViewById(R.id.imageView2);
        assert notephoto != null;
        notephoto.setImageURI(Uri.parse(i.getStringExtra("photo")));
        String voice = i.getStringExtra("voice");
        if(voice.equals("na")) {
            final Button play = (Button) findViewById(R.id.play);
            play.setEnabled(false);
            final Button pause = (Button) findViewById(R.id.pause);
            pause.setEnabled(false);
            sb = (SeekBar) findViewById(R.id.seekBar);
            sb.setEnabled(false);
        }
        else {
            sb = (SeekBar) findViewById(R.id.seekBar);
            mp =MediaPlayer.create(this,Uri.parse(voice));
            sb.setMax(mp.getDuration());
            updateSeekBar();
            final Button play = (Button) findViewById(R.id.play);

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp.start();
                }
            });

            final Button pause = (Button) findViewById(R.id.pause);

            pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mp.pause();
                }
            });
        }
        Button btnReturn = (Button) findViewById(R.id.btnreturn);
        if (btnReturn != null) {
            btnReturn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            });
        }
        Button locButton = (Button)findViewById(R.id.loc);
        if(!i.getStringExtra("lat").equals("0")) {
            if (locButton != null) {
                locButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent map = new Intent(v.getContext(), MapViewActivity.class);
                        map.putExtra("lat", i.getStringExtra("lat"));
                        map.putExtra("lon", i.getStringExtra("lon"));
                        startActivity(map);
                    }
                });

            }
        }
        else
        {
            locButton.setEnabled(false);
        }
    }


    private void updateSeekBar()
    {
        sb.setProgress(mp.getCurrentPosition());
        sb.postDelayed(thr, 1000);
    }

    Runnable thr = new Runnable() {
        @Override
        public void run()
        {
            updateSeekBar();
        }
    };

    @Override
    public void onPause()
    {
        super.onPause();
        if(mp!=null)
        {
            if(mp.isPlaying())
                mp.pause();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(mp!=null)
        {
            if(mp.isPlaying())
                mp.pause();
        }
    }

}