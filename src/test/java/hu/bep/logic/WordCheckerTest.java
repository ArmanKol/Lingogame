package hu.bep.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Word checker test")
class WordCheckerTest {
    private static final String length3 = "ban";

    private static final String specialChars1 = "back-end";

    private static final String wordWithSpace = "su pe";
    private static final String wordWithNumbers = "3huis";

    private static final String wordToGuess_5letters = "broer";

    @Test
    @DisplayName("Speciale characters moeten INCORRECT teruggeven.")
    void roundController_InputSpecialChars_ReturnIncorrect(){
        WordChecker wordChecker = new WordChecker();

        //word to guess = broer
        wordChecker.setWordToGuess(wordToGuess_5letters);

        wordChecker.fillHashMapWithLetters(wordToGuess_5letters, wordChecker.getCharsWordToGuess());

        //input word = back-end
        wordChecker.wordGuessed(specialChars1);

        assertEquals("b: CORRECT a: INCORRECT c: INCORRECT k: INCORRECT -: INCORRECT ", wordChecker.getFeedback());
    }

    @Test
    @DisplayName("Characters met spatie moeten incorrect teruggeven")
    void roundController_InputCharWithSpace_ReturnIncorrect(){
        WordChecker wordChecker = new WordChecker();

        //word to guess = 3huis
        wordChecker.setWordToGuess(wordWithNumbers);

        wordChecker.fillHashMapWithLetters(wordWithNumbers, wordChecker.getCharsWordToGuess());

        //input word = back-end
        wordChecker.wordGuessed(wordWithSpace);

        assertEquals("s: PRESENT u: PRESENT  : INCORRECT p: INCORRECT e: INCORRECT ", wordChecker.getFeedback());
    }

    @Test
    @DisplayName("Woord met een cijfer erin moet INCORRECT teruggeven")
    void roundController_InputNumber_ReturnIncorrect(){
        WordChecker wordChecker = new WordChecker();

        //word to guess = huise
        wordChecker.setWordToGuess("huise");

        wordChecker.fillHashMapWithLetters("huise", wordChecker.getCharsWordToGuess());

        //Input word 3huis
        wordChecker.wordGuessed(wordWithNumbers);

        assertEquals("3: INCORRECT h: PRESENT u: PRESENT i: PRESENT s: PRESENT ", wordChecker.getFeedback());
    }
}
