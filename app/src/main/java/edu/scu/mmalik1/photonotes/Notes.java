package edu.scu.mmalik1.photonotes;

import java.io.Serializable;

/**
 * Created by abhimanyusingh on 5/5/16.
 */
public class Notes implements Serializable {
    private String text;
    private String filename;
    private String locLat;
    private String locLong;
    private String recVoice;
    public Notes( String text, String filename, String locLat, String locLong, String Voice){
        this.text = text;
        this.filename = filename;
        this.locLat = locLat;
        this.locLong = locLong;
        this.recVoice = Voice;
    }

    public String getText() {
        return text;
    }
    public String getFilename() {
        return filename;
    }
    public String getlocLat() {
        return locLat;
    }
    public String getLocLong() {
        return locLong;
    }
    public String getRecVoice() { return recVoice;}

}
