package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.SessionHistory;

import java.util.List;
import java.util.UUID;

public class SessionHistoryStore extends Store<SessionHistory> {
    public SessionHistoryStore(String filePath) {super(filePath);}
    //finds a session By UUID
    public List<SessionHistory> findSessionByUUID (UUID sessionIdent){return find(rec -> rec.getGloballyUniqueSessionIdentifier().equals(sessionIdent));}
    public List<SessionHistory> findSessionsByUserID(int id){return find(rec -> rec.getUserId() == id);}
    // Bill Pugh Singleton
    private static class SessionHistoryStoreHolder{
        private static final SessionHistoryStore INSTANCE = new SessionHistoryStore(Constants.SESSION_FILE_PATH);
    }
    public static SessionHistoryStore getInstance(){return SessionHistoryStoreHolder.INSTANCE;}
}
