package ca.ucalgary.seng300.game.core;

public interface Game {

    //not sure how these variables could actually be set up given that
    //variables in an interface have to be final
    //and the things implementing this interface will have different
    //versions of both RuleValidator and GameState
    GameState gameState = null;
    RuleValidator validator = null;


    //probably returns the whole GameState class like getGameState,
    //but it has to create it first rather than just using what is in GameState
    //probably also does things like set the correct RuleValidator
    //also probably does some of the functions of a constructor
    GameState startGame(Object Player1, Object Player2);

    //would validate then apply an action to gameState
    void handleAction(PlayerAction playerAction);

    //will probably return the whole GameState class type
    GameState getGameState();

    //not sure what this should return
    Object getGameUpdate();

    //could probably be made to not have the abstract keyword
    //if the State enum existed, it might be able to check that
    //though may not be possible due to GameState not being initialized yet
    boolean isGameOver();


}
