package ca.ucalgary.seng300.platform.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Record {
    private int id;
    private long timeCreated = System.currentTimeMillis();

    public Record(int id){this.id = id;}
    public int getID(){return this.id;}
    public long getTimeCreated() {return timeCreated;}
    public void setID(int id){this.id = id;}
    public static String getTimeCreatedReadableFormat(Long timeInMilLis){
        Date date = new Date(timeInMilLis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
