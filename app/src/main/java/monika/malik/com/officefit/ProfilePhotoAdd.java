package monika.malik.com.officefit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class ProfilePhotoAdd extends AppCompatActivity {
    private static boolean firstboot = false;
    private static int REQUEST_IMAGE_CAPTURE = 1888;
    Uri uriForFile = null,lasturi  = null;
    EditText name ;
    EditText age,steps;
    DBHelper mDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo_add);
        name = (EditText)findViewById(R.id.name);
        age = (EditText)findViewById(R.id.age);
        steps = (EditText)findViewById(R.id.editText2);
        //get firstboot
        firstboot = getFirstBoot();
        ImageView photoHolder = (ImageView)findViewById(R.id.imageView);
        String selectedImageUriStr = mDBHelper.retriveValue("image");
        if(selectedImageUriStr != null)
            lasturi = Uri.parse(selectedImageUriStr);

        if(selectedImageUriStr!=null)
            photoHolder.setImageURI(lasturi);

        if(!firstboot)
        {
            Button nextOrReturn = (Button)findViewById(R.id.button3);
            String next = "DONE";
            nextOrReturn.setText(next.toCharArray(),0,next.length());
            nextOrReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lasturi!=null)
                    {
                        mDBHelper = new DBHelper(getApplicationContext());
                        mDBHelper.updateorInsertValue("image",lasturi.toString());
                    }
                    if(savedata()) {
                        finish();
                    }
                }
            });
            name.setText(mDBHelper.retriveValue("name").toCharArray(),0,
                    mDBHelper.retriveValue("name").length());
            age.setText(mDBHelper.retriveValue("age").toCharArray(),0,
                    mDBHelper.retriveValue("age").length());
            steps.setText(mDBHelper.retriveValue("stepgoal").toCharArray(),0,
                    mDBHelper.retriveValue("stepgoal").length());
        }
        else
        {
            Button nextOrReturn = (Button)findViewById(R.id.button3);
            String next = "NEXT";
            nextOrReturn.setText(next.toCharArray(),0,next.length());
            nextOrReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lasturi!=null)
                    {
                        mDBHelper = new DBHelper(getApplicationContext());
                        mDBHelper.updateorInsertValue("image",lasturi.toString());
                    }

                    if(savedata()) {
                        mDBHelper.updateorInsertValue("boot", "na");
                        mDBHelper.updateorInsertValue("waterswitch","yes");
                        mDBHelper.updateorInsertValue("stepswitch","yes");
                        mDBHelper.updateorInsertValue("generalswitch","yes");
                        mDBHelper.updateorInsertValue("luntchswitch","yes");
                        mDBHelper.updateorInsertValue("water","1");
                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    boolean getFirstBoot()
    {
        mDBHelper = new DBHelper(getApplicationContext());
        return mDBHelper.firstBoot();
    }

    public void getImage(View view)
    {
        takePicture();
    }

    public void takePicture() {
        String imageFileName = "JPEG";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        try {
            File image  = null;
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            uriForFile = Uri.fromFile(image);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            lasturi = uriForFile;
            ImageView img = (ImageView)findViewById(R.id.imageView);
            img.setImageURI(lasturi);

            // Save a file: path for use with ACTION_VIEW intents
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(lasturi);
            this.sendBroadcast(mediaScanIntent);

        }
    }
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }

    private boolean savedata()
    {
        if(name.getText().toString().length()<1)
        {
            Toast.makeText(getApplicationContext(), "Enter a valid name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(age.getText().toString().length()<1) {
            Toast.makeText(getApplicationContext(), "Enter a valid age", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(steps.getText().toString().length()<1) {
            Toast.makeText(getApplicationContext(), "Enter a valid step count", Toast.LENGTH_SHORT).show();
            return false;
        }
        mDBHelper.updateorInsertValue("name",name.getText().toString());
        mDBHelper.updateorInsertValue("age",age.getText().toString());
        mDBHelper.updateorInsertValue("stepgoal",steps.getText().toString());

        return true;
    }
}
