package ca.ucalgary.seng300.platform.constants;

public enum TypeSession {
    CREATE_SESSION("Create_Session"),
    JOIN_SESSION("Join_Session");

    final String sessionType;
    TypeSession(String sessionType){this.sessionType = sessionType;}
}
