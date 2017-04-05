package monika.malik.com.officefit;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepSensor;
    static float steps = 20;
    static float water = 1;
    static float stepGoal = 1000;
    static float waterGoal = 8;
    private static final int REQUEST_CODE = 0x11;
    private static long initialTrigger = 0;
    DBHelper mDBHelper;
    private BroadcastReceiver receiver;
    private static final String ALARM_NOTIFICATION = "com.monika.malik.NOTIFICATION";
    private static final String ALARM_WAKEUP= "com.monika.malik.WAKEUP";
    RecyclerView rv;
    LinearLayoutManager llm;
    NotesAdapter na = null;
    TextToSpeech tts = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_CALENDAR",
                "android.permission.WRITE_CALENDAR"};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE); // without sdk version check
        initializeTTS();
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        boolean firstBoot = getFirstBoot(); //get first boot here
        if(!firstBoot ) {
            //register steps listerner
            registerStepsListener();
            //read data from db
            setContentView(R.layout.activity_main);
            stepGoal = Float.parseFloat(mDBHelper.retriveValue("stepgoal").toString());
            water = Integer.valueOf(mDBHelper.retriveValue("water").toString());
            updatestepProgress();
            updatewaterProgress();
            registerListeners();
            scheduleDayAlarms();
            populateToDo();
        }
        else
        {
            registerListeners();
            Intent intent = new Intent(this, ProfilePhotoAdd.class);
            startActivity(intent);

        }

    }

    void registerStepsListener() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    void updatestepProgress()
    {
        ProgressBar mStepsProgress = (ProgressBar) findViewById(R.id.stepsprogress);
        TextView stepsProgessText = (TextView)findViewById(R.id.stepprogresstext);
        String text = String.valueOf((int)steps)+" / "+String.valueOf((int)stepGoal);
        stepsProgessText.setText(text.toCharArray(),0,text.length());
        int progress = (int)((steps * 100.0f) /stepGoal);
        ObjectAnimator stepsAnimation = ObjectAnimator.ofInt(mStepsProgress, "progress",progress);
        stepsAnimation.setDuration(700);
        stepsAnimation.setInterpolator(new DecelerateInterpolator());
        stepsAnimation.start();
        mDBHelper.updateorInsertValue("steps",String.valueOf((int)steps));
        if(progress>=100)
            showCompletionStep();
    }

    void updatewaterProgress()
    {
        ProgressBar mWaterProgress = (ProgressBar) findViewById(R.id.waterprogress);
        TextView waterProgessText = (TextView)findViewById(R.id.waterprogresstext);
        String text = String.valueOf((int)water)+" / "+String.valueOf((int)waterGoal);
        waterProgessText.setText(text.toCharArray(),0,text.length());
        int progress = (int)((water * 100.0f) /waterGoal);
        ObjectAnimator waterAnimation = ObjectAnimator.ofInt(mWaterProgress, "progress", progress);
        waterAnimation.setDuration(700);
        waterAnimation.setInterpolator(new DecelerateInterpolator());
        waterAnimation.start();
        mDBHelper.updateorInsertValue("water",String.valueOf((int)water));
        if(water>=waterGoal)
            showCompletionWater();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            steps = (int) event.values[0];
            updatestepProgress();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor s, int i)
    {

    }

    private boolean getFirstBoot()
    {
        mDBHelper = new DBHelper(getApplicationContext());
        return mDBHelper.firstBoot();
    }

    public void addwater(View v)
    {
        water+=1;
        updatewaterProgress();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reminder:
                Intent intent = new Intent(this,Reminder.class);
                startActivity(intent);
                return true;

            case R.id.profile_setting:
                Intent intentp = new Intent(this,ProfilePhotoAdd.class);
                startActivity(intentp);
                return true;

            case R.id.action_uninstall:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            case R.id.action_notification:
                Intent intentn = new Intent(this,notifications.class);
                startActivity(intentn);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if(grantResults.length<1)
                return;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                boolean firstBoot = getFirstBoot(); //get first boot here
                if(!firstBoot) {
                    //register steps listerner
                    registerStepsListener();
                    //read data from db
                    setContentView(R.layout.activity_main);
                    stepGoal = Float.parseFloat(mDBHelper.retriveValue("stepgoal").toString());
                    water = Integer.valueOf(mDBHelper.retriveValue("water").toString());
                    updatestepProgress();
                    updatewaterProgress();
                    registerListeners();
                    registerdayintent();
                    scheduleDayAlarms();
                    populateToDo();
                }
                else
                {
                    registerListeners();
                    registerdayintent();
                    Intent intent = new Intent(this, ProfilePhotoAdd.class);
                    startActivity(intent);

                }
            } else {
                Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void registerListeners()
    {
        System.out.println("Listeners added");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ALARM_NOTIFICATION);
        filter.addAction(ALARM_WAKEUP);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(ALARM_NOTIFICATION)) {
                    NotificationGenerator nG = new NotificationGenerator(getApplicationContext(),
                            R.drawable.water_drop,R.drawable.steps,R.drawable.prohibit,
                            R.drawable.lunch, R.drawable.eye);
                    nG.createNotification();
                }
                if(intent.getAction().equals(ALARM_WAKEUP)) {
                    registerdayintent();
                    clearIntentForDay();
                }

            }
        };
        registerReceiver(receiver, filter);
    }

    private void scheduleDayAlarms()
    {
        initialTrigger = System.currentTimeMillis() + 10000;
        AlarmManager am=(AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ALARM_NOTIFICATION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        am.cancel(pendingIntent);

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                initialTrigger,1000  * 60 * 60 * 1,pendingIntent);
    }

    private  void registerdayintent()
    {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int hr = 23 - hour;
        int mn = 60 - min+20;
        System.out.println("scheduled for "+((hr * 60) + mn)*60*1000);
        AlarmManager am=(AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ALARM_WAKEUP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pendingIntent);
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                System.currentTimeMillis() +(hr * 60 + mn)*60*1000, pendingIntent);

    }

    private void clearIntentForDay()
    {
        //clear db for step notifications
        mDBHelper.removeValue("s1");
        mDBHelper.removeValue("s2");
        mDBHelper.removeValue("s3");
        mDBHelper.removeValue("s4");
        mDBHelper.removeValue("s5");
        mDBHelper.removeValue("s6");
        mDBHelper.removeValue("s7");

        //clear db for water notifications
        mDBHelper.removeValue("w1");
        mDBHelper.removeValue("w2");
        mDBHelper.removeValue("w3");
        mDBHelper.removeValue("w4");
        mDBHelper.removeValue("w5");
        mDBHelper.removeValue("w6");
        mDBHelper.removeValue("w7");

        //clear lunch value
        mDBHelper.removeValue("l1");

        //clear eyewash table
        mDBHelper.removeValue("e1");
        mDBHelper.removeValue("e2");
        mDBHelper.removeValue("e3");
        mDBHelper.removeValue("e4");

        //clear reminder
        mDBHelper.removeValue("r1");

        //reset water to 1
        mDBHelper.updateorInsertValue("water","1");
        //reste steps
        mDBHelper.updateorInsertValue("steps","1");

        //clear db for stretch
        mDBHelper.removeValue("sr1");
        mDBHelper.removeValue("sr2");
        mDBHelper.removeValue("sr3");
    }

    private void populateToDo()
    {
        List<Notes> notes =  new ArrayList<Notes>();;
        Cursor cursor = null;
        try {
            cursor = getApplicationContext().getContentResolver()
                    .query(CalendarContract.Events.CONTENT_URI,
                            new String[]{CalendarContract.Calendars._ID}, null, null, null);

            if(!cursor.moveToFirst())
            {
                System.out.println("No calendar");
                return;
            }
            long calID = cursor.getLong(0);
            String[] proj =
                    new String[]{
                            Events.DTSTART,
                            Events.DTEND,
                            Events.TITLE};
            cursor = null;
            cursor = getApplicationContext().
                    getContentResolver().query(Events.CONTENT_URI,
                    proj,
                    Events.SELF_ATTENDEE_STATUS+"="+Events.STATUS_CONFIRMED,
                    null,
                    "dtstart ASC");

        }catch(SecurityException e)
        {

        }
        if(cursor.getCount()<1)
            return;

        cursor.moveToFirst();
        do{
            if(Long.valueOf(cursor.getString(cursor.getColumnIndex(Events.DTSTART))) > System.currentTimeMillis()) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(Long.valueOf(cursor.getString(cursor.getColumnIndex(Events.DTSTART))));
                Notes n = new Notes(cursor.getString(cursor.getColumnIndex(Events.TITLE))+"  at "+cal.getTime().toString());
                notes.add(n);
            }
        } while(cursor.moveToNext());
        rv = (RecyclerView) findViewById(R.id.listView);
        llm = new LinearLayoutManager(this);
        if(notes.size()>0) {

            rv.setLayoutManager(llm);
            na = new NotesAdapter(notes,getApplicationContext());
            rv.setAdapter(na);
        }
    }

    private void showCompletionStep()
    {
        FragmentManager fm = getSupportFragmentManager();
        fragmentdialogstep frm = new fragmentdialogstep();
        frm.show(fm,"Congrats");
        String speak = "Congrats you have reached you goal steps";
        if(tts != null)
        {
            String proceed = mDBHelper.retriveValue("generalswitch");
            if (!proceed.equals("no"))
                tts.speak(speak,TextToSpeech.QUEUE_ADD,null,"");
        }
    }
    private void showCompletionWater()
    {
        FragmentManager fm = getSupportFragmentManager();
        fragmentdialogwater frm = new fragmentdialogwater();
        frm.show(fm,"Congrats");
        String speak = "Congrats you have reached you Water goal";
        if(tts != null)
        {
            String proceed = mDBHelper.retriveValue("generalswitch");
            if (!proceed.equals("no"))
                tts.speak(speak, TextToSpeech.QUEUE_ADD,null,"");
        }
    }

    void initializeTTS()
    {
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR)
                    tts.setLanguage(Locale.ENGLISH);
            }
        });
    }
}
