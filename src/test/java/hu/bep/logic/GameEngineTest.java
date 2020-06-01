package hu.bep.logic;


import hu.bep.logic.state.LevelState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Game engine")
public class GameEngineTest {
    private static final String length3 = "ban";
    private static final String length4 = "bana";
    private static final String length5 = "banaa";
    private static final String length6 = "banaan";
    private static final String length7 = "backend";

    private static final String specialChars1 = "back-end";
    private static final String specialChars2 = "kom-kom";
    private static final String specialChars3 = "kénkon";

    private static final String wordWithSpace = "su per";
    private static final String wordWithNumbers = "3huizen";

    private static final String wordToGuess_5letters = "broer";
    private static final String wordToGuess_5letters_same = "broer";

    @Test
    @DisplayName("Not the same word")
    void notSameWord(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start(wordToGuess_5letters);
        assertFalse(gameEngine.wordGuessed(length3));
    }

    @Test
    @DisplayName("The same word")
    void sameWord(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start(wordToGuess_5letters);
        assertTrue(gameEngine.wordGuessed(wordToGuess_5letters_same));
    }

    @Test
    @DisplayName("3 letter word")
    void threeLetterWord(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start(wordToGuess_5letters);
        assertFalse(gameEngine.wordGuessed(length3));
    }

    @Test
    @DisplayName("4 letter word")
    void fourLetterWord(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start(wordToGuess_5letters);
        assertFalse(gameEngine.wordGuessed(length4));
    }

    @Test
    @DisplayName("5 letter word")
    void fiveLetterWord(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start(wordToGuess_5letters);
        assertFalse(gameEngine.wordGuessed(length5));
    }

    @Test
    @DisplayName("6 letter word")
    void sixLetterWord(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start(wordToGuess_5letters);
        assertFalse(gameEngine.wordGuessed(length6));
    }

    @Test
    @DisplayName("7 letter word")
    void sevenLetterWord(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start(wordToGuess_5letters);
        assertFalse(gameEngine.wordGuessed(length7));
    }

    @Test
    @DisplayName("Speciale characters moeten INCORRECT teruggeven.")
    void specialCharsNoException(){
        GameEngine gameEngine = new GameEngine();

        //word to guess = broer
        gameEngine.start(wordToGuess_5letters);

        //input word = back-end
        gameEngine.roundController(specialChars1);


        assertEquals("b: CORRECT a: INCORRECT c: INCORRECT k: INCORRECT -: INCORRECT ", gameEngine.getFeedbackWord());
    }

    @Test
    @DisplayName("Als het te raden woord 6 characters heeft moet de levelstate == LevelState.SIX_LETTER_WORD")
    void gameStatewith6LetterWord(){
        GameEngine gameEngine = new GameEngine();

        //word to guess = papier
        gameEngine.start("papier");

        assertTrue(gameEngine.getLevelState() == LevelState.SIX_LETTER_WORD);
    }
}
