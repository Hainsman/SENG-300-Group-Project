package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.Friend;

import java.util.List;

public class FriendStore extends Store<Friend> {
    @Override
    protected Class<Friend> recordType() {return null;}
    public FriendStore(String filePath){super(filePath);}

    public List<Friend> getAllFriendsOfUserId (int id){return find(rec -> rec.getUserId() == id);}
    public List<Friend> findAllFriendsById(int id){return find(rec -> rec.getFriendId() == id);}

    private static class FriendStoreHolder{
        private static final FriendStore INSTANCE = new FriendStore(Constants.FRIENDS_FILE_PATH);
    }

    public static FriendStore getInstance(){return FriendStoreHolder.INSTANCE;}
}
