package ca.ucalgary.seng300.platform.domain;

public abstract class Record {
    private int id;

    public Record(int id){this.id = id;}
    public int getID(){return this.id;}
    public void setID(int id){this.id = id;}
}
