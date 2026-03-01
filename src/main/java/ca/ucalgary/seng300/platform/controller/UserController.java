package ca.ucalgary.seng300.platform.controller;

import ca.ucalgary.seng300.platform.domain.User;

public class UserController {
    public User load(String sessionId) {
        // Verify sessionId in table of cookies
        // find user in Users table using session.UserId
        // blank out password
        // return user
        return null;
    }
    public String[] save(String sessionId, User user) {
        // Verify sessionId in table of cookies
        // find user in Users table using session.UserId into userOrig


        // if user.hashPassword not empty userOrig.hashPassword = Hash(user.hashPassword)

        // if user.name != userOrig.name {
        // if new name is available? is it special chars
        //}

        // User.update(userOrig)
        return null;
    }
}
