package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.FriendHistory;
import java.util.List;

public class FriendHistoryStore extends Store<FriendHistory> {
    protected FriendHistoryStore(String filePath) {super(filePath);}

    //get all friends of a user inactive and inactive
    public List<FriendHistory> getAllFriendsByUserId(int id){return find(rec -> rec.getUserId() == id);}

    //Bill pugh singleton
    private static class FriendHistoryStoreHolder{
        private static final FriendHistoryStore INSTANCE = new FriendHistoryStore(Constants.FRIENDS_FILE_PATH);
    }

    public static FriendHistoryStore getInstance(){return FriendHistoryStoreHolder.INSTANCE;}
}
