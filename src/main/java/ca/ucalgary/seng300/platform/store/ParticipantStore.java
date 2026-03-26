package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.constants.Constants;
import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.domain.Participant;

import java.util.List;

public class ParticipantStore extends Store<Participant> {
    public ParticipantStore(String filePath) {super(filePath);}
    //returns list of all games user has played
    public List<Participant> findUserParticipationInGameEvents (int id){return find(rec -> rec.getUserID() == id);}
    //Bill Pugh Singleton
    private static class ParticipantStoreHolder{
        private static final ParticipantStore INSTANCE = new ParticipantStore(Constants.PARTICIPANTS_FILE_PATH);
    }
    public static ParticipantStore getInstance() {return ParticipantStoreHolder.INSTANCE;}
}
