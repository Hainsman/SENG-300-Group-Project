package ca.ucalgary.seng300.platform.domain;

public class Friend extends Record{
    private int userId;
    private int friendId;

    protected Friend(int id, int userId, int friendId) {
        super(id, Friend.class.getSimpleName());
        this.userId = userId;
        this.friendId = friendId;
    }
    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}
    public int getFriendId() {return friendId;}
    public void setFriendId(int friendId) {this.friendId = friendId;}
}
