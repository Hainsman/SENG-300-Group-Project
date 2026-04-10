package ca.ucalgary.seng300.platform.domain;

public class User extends Record {
    private String login;
    private String email;
    private String passwordHash;
    private boolean isActive;

    public User(int id, String login, String email, String passwordHash){
        super(id, User.class.getSimpleName());
        this.login = login;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isActive = true;
    }

    //getters
    public String getlogin(){return this.login;}
    public String getEmail(){return this.email;}
    public String getPasswordHash(){return this.passwordHash;}
    public boolean getIsActive(){return this.isActive;}

    //setters
    public void setlogin(String login){this.login = login;}
    public void setEmail(String email){this.email = email;}
    public void setPasswordHash(String passwordHash){this.passwordHash = passwordHash;}
    public void setIsActive(boolean isActive){this.isActive = isActive;}
}