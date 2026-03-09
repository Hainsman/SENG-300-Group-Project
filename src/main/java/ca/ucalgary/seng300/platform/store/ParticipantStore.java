package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.GameHistory;
import ca.ucalgary.seng300.platform.domain.Participant;

import java.util.List;

public class ParticipantStore extends Store<Participant> {
    public ParticipantStore(String filePath) {super(filePath);}
    //returns list of all games user has played
    public List<Participant> findUserParticipationInGameEvents (int id){return find(rec -> rec.getUserID() == id);}
}
