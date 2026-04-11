package ca.ucalgary.seng300.platform.domain;

public class FriendHistory extends Record{
    private int userId;
    private int friendUserId;
    private boolean friendShipActive;

    public FriendHistory(int id) {
        super(id, FriendHistory.class.getSimpleName());
    }

    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}
    public int getFriendUserId() {return friendUserId;}
    public void setFriendUserId(int friendUserId) {this.friendUserId = friendUserId;}
    public boolean isFriendShipActive() {return friendShipActive;}
    public void setFriendShipActive(boolean friendShipActive) {this.friendShipActive = friendShipActive;}
}
