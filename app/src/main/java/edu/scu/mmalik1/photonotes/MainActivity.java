package edu.scu.mmalik1.photonotes;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

public class MainActivity extends AppCompatActivity {
    private List<Notes> notes;
    private Db_Helper Db_Helper;
    private static final int REQUEST_CODE = 0x11;
    RecyclerView rv;
    LinearLayoutManager llm;
    NotesAdapter na = null;
    ItemTouchHelper.Callback cb;
    ItemTouchHelper help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (SCREEN_ORIENTATION_PORTRAIT);
        String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.RECORD_AUDIO",
                "android.permission.READ_EXTERNAL_STORAGE"};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE); // without sdk version check
        //db helper push and close
        Db_Helper = new Db_Helper(this);
        Db_Helper.open();
        notes = new ArrayList<Notes>();
        notes = Db_Helper.retriveNoteDetails();
        Db_Helper.close();
        if(notes!=null)
        System.out.println(notes.size());
        if(notes !=null) {
            rv = (RecyclerView) findViewById(R.id.listView);
            llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);
            na = new NotesAdapter(notes,getApplicationContext());
            rv.setAdapter(na);
            cb = new TouchHelper(na);
            help = new ItemTouchHelper(cb);
            help.attachToRecyclerView(rv);
        }

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action_bar, menu);
        return true;
    }
@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //respond to menu item selection
        switch (item.getItemId()) {
            case R.id.add:
                startActivity(new Intent(this, AddPhotoTextActivity.class));
            break;
            case R.id.uninstall:
                Uri packageURI = Uri.parse("package:"+MainActivity.class.getPackage().getName());
                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                startActivity(uninstallIntent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
    return true;
    }

    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestart()
    {
        System.out.println("restart called");
        super.onRestart();
        Db_Helper = new Db_Helper(this);
        Db_Helper.open();
        notes = new ArrayList<Notes>();
        notes = Db_Helper.retriveNoteDetails();
        Db_Helper.close();
        if(na==null)
        {
            rv = (RecyclerView) findViewById(R.id.listView);
            llm = new LinearLayoutManager(this);
            rv.setLayoutManager(llm);
            na = new NotesAdapter(notes,getApplicationContext());
            rv.setAdapter(na);
            cb = new TouchHelper(na);
            help = new ItemTouchHelper(cb);
            help.attachToRecyclerView(rv);
        }
        if(notes !=null) {
            for(Notes n:notes)
            {
                System.out.println("test "+n.getlocLat()+" "+n.getLocLong());
            }
            na.changeItem(notes);
            na.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if(grantResults.length<1)
                return;
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(getApplicationContext(), "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}