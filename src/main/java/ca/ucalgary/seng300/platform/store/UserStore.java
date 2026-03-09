package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.User;

import java.util.List;

public class UserStore extends Store<User> {
    public UserStore(String filePath) {super(filePath);}
    public List<User> findByUserName(String username) {return find(rec -> rec.getlogin().equals(username));}
    public List<User> findByUserEmail(String email) {return find(rec -> rec.getEmail().equals(email));}
}
