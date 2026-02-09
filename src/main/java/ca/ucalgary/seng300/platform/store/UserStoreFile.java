package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.User;

import java.util.ArrayList;

public class UserStoreFile implements IUserStore {
    ArrayList<User> users = new ArrayList<User>(){{
        new User(1, "Daniel", "123");
        new User(2, "John", "456");
        new User(3, "Alex", "789");
    }};
    @Override
    public User find(String username) {
        for (int i = 0; i < users.size(); i++) {
            User userSearch = users.get(i);
            if (userSearch.getlogin().equals(username)){
                return userSearch; // if user found return user object
            }
        }
        return null; //user not found
    }

    @Override
    public void create(User user) {
        users.add(user);
        //call write to CSV
    }

    @Override
    public void delete(User user) {
        users.remove(user);
        //call write to CSV
    }

    @Override
    public void updater(User user) {

    }

    @Override
    public void writeToCSV(User user) {

    }

    @Override
    public void readFromCSV(User user) {

    }
}