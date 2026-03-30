package ca.ucalgary.seng300.platform.messages;

public class chatMessage {
    private String chatServer;      // which chat server the message is from
    private String userName; // required for JOIN
    private String userID;  // this client's player id
    private String content;

    public chatMessage(String dest, String Uname, String UID, String cont) {
        chatServer = dest;
        userName = Uname;
        userID = UID;
        content = cont;
    }

    public String getServ() { return chatServer; }
    public void setServ(String srv) { this.chatServer = srv; }
}
