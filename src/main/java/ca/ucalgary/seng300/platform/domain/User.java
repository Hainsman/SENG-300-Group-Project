package ca.ucalgary.seng300.platform.domain;

public class User {
    private int id;
    private String login;
    private String passwordHash;

    public User(int id, String login, String passwordHash){
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
    }

    //getters
    public int getID(){return this.id;}
    public String getlogin(){return this.login;}
    public String getPasswordHash(){return this.passwordHash;}

    //setters
    public void setID(int id){this.id = id;}
    public void setlogin(String login){this.login = login;}
    public void setPasswordHash(String passwordHash){this.login = passwordHash;}

}