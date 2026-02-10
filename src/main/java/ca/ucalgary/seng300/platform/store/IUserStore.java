package ca.ucalgary.seng300.platform.store;

import ca.ucalgary.seng300.platform.domain.User;

public interface IUserStore {
    User find(String username);
    void create(User user);
    void delete(User user);
    void updater(User user);
}