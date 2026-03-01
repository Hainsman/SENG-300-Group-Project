package ca.ucalgary.seng300.platform.domain;

public abstract class Record {
    private int id;
    private long timeCreated = System.currentTimeMillis();

    public Record(int id){this.id = id;}
    public int getID(){return this.id;}
    public long getTimeCreated() {return timeCreated;}
    public void setID(int id){this.id = id;}


}
