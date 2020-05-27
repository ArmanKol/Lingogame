package hu.bep.logic;

import hu.bep.logic.state.GameState;
import hu.bep.logic.state.LevelState;

public class GameEngine {
    private String givenWord;
    private int guessesLeft;
    private int score;
    private Enum gameState, levelState;


    public GameEngine(){

    }



    public void start(){
        gameState = GameState.PLAYING;
        levelState= LevelState.FIVE_LETTER_WORD;
        guessesLeft = 5;
        score = 0;
    }

}
