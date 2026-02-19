package ca.ucalgary.seng300.platform.domain;

public class User extends Record {
    private String login;
    private String passwordHash;

    public User(int id, String login, String passwordHash){
        super(id);
        this.login = login;
        this.passwordHash = passwordHash;
    }

    //getters
    public String getlogin(){return this.login;}
    public String getPasswordHash(){return this.passwordHash;}

    //setters
    public void setlogin(String login){this.login = login;}
    public void setPasswordHash(String passwordHash){this.login = passwordHash;}

}