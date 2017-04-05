package monika.malik.com.officefit;

import android.graphics.Bitmap;

import java.io.Serializable;


public class Notes implements Serializable {
    private String text;
    private Bitmap bmp;
    public Notes( String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }


}
