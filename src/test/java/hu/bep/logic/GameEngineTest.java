package hu.bep.logic;


import hu.bep.logic.state.GameState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Game engine")
class GameEngineTest {
    private static final String length3 = "ban";
    private static final String length4 = "bana";
    private static final String length5 = "banaa";
    private static final String length6 = "banaan";
    private static final String length7 = "backend";

    private static final String specialChars1 = "back-end";

    private static final String wordWithSpace = "su pe";
    private static final String wordWithNumbers = "3huis";

    private static final String wordToGuess_5letters = "broer";
    private static final String wordToGuess_5letters_same = "broer";

    @Test
    @DisplayName("Woord niet geraden")
    void notSameWord(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start(wordToGuess_5letters);
        assertFalse(gameEngine.wordGuessed(length3));
    }

    @Test
    @DisplayName("Woord geraden")
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
    @DisplayName("Characters met spatie moeten incorrect teruggeven")
    void charWithSpace(){
        GameEngine gameEngine = new GameEngine();

        //word to guess = 3huis
        gameEngine.start(wordWithSpace);

        gameEngine.roundController(wordWithSpace);

        assertEquals("s: CORRECT u: CORRECT  : CORRECT p: CORRECT e: CORRECT ", gameEngine.getFeedbackWord());
    }

    @Test
    @DisplayName("Woord met een cijfer erin moet INCORRECT teruggeven")
    void charWithNumberIncorrect(){
        GameEngine gameEngine = new GameEngine();

        //word to guess = huise
        gameEngine.start("huise");

        //Input word 3huis
        gameEngine.roundController(wordWithNumbers);

        assertEquals("3: INCORRECT h: PRESENT u: PRESENT i: PRESENT s: PRESENT ", gameEngine.getFeedbackWord());
    }

    @Test
    @DisplayName("Als er een verkeerde lengte wordt meegegeven dan de levelstate moet er een false returnen")
    void gameCantStartWithWrongWordLength(){
        GameEngine gameEngine = new GameEngine();

        //word to guess = papier
        boolean started = gameEngine.start("papier");

        assertFalse(started);
    }

    @Test
    @DisplayName("De game kan gestart worden als de juiste woord lengte word meegegeven")
    void gameCanStartWithCorrectWordLength(){
        GameEngine gameEngine = new GameEngine();

        //word to guess = astma
        gameEngine.start("astma");

        boolean score = gameEngine.getScore() == 0;
        boolean gameState = gameEngine.getGameState() == GameState.PLAYING;
        boolean guessesLeft = gameEngine.getGivenWord().length() > 0;
        boolean gameStarted = score && gameState && guessesLeft;

        assertTrue(gameStarted);
    }

    @Test
    @DisplayName("Als woord geraden is dan wordguessed == true")
    void wordGuessedReturnsTrue(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");
        gameEngine.roundController("hallo");

        assertTrue(gameEngine.isWordGuessed());
    }

    @Test
    @DisplayName("Woord in een keer geraden is 50 punten")
    void firstGuessCorrect(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");
        gameEngine.roundController("hallo");

        assertSame(50, gameEngine.getScore());
    }

    @Test
    @DisplayName("Woord 2 keer fout en daarna goed is 40 punten")
    void secondGuessCorrect(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");
        gameEngine.roundController("mamaa");
        gameEngine.roundController("hallo");

        assertSame(40, gameEngine.getScore());
    }

    @Test
    @DisplayName("Woord 3 keer fout en daarna goed is 30 punten")
    void thirdGuessCorrect(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");
        gameEngine.roundController("wekke");
        gameEngine.roundController("pecht");
        gameEngine.roundController("hallo");

        assertSame(30, gameEngine.getScore());
    }

    @Test
    @DisplayName("Woord 4 keer fout en daarna goed is 20 punten")
    void fourthGuessCorrect(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");
        gameEngine.roundController("astma");
        gameEngine.roundController("asiel");
        gameEngine.roundController("adres");
        gameEngine.roundController("hallo");

        assertSame(20, gameEngine.getScore());
    }

    @Test
    @DisplayName("Woord 5 keer fout en daarna goed is 10 punten")
    void fifthGuessCorrect(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");

        gameEngine.roundController("pallo");
        gameEngine.roundController("tatto");
        gameEngine.roundController("mallo");
        gameEngine.roundController("pralo");
        gameEngine.roundController("hallo");

        assertSame(10, gameEngine.getScore());
    }

    @Test
    @DisplayName("Het woord nooit geraden")
    void notGuessedRightWord(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");

        gameEngine.roundController("pallo");
        gameEngine.roundController("tatto");
        gameEngine.roundController("mallo");
        gameEngine.roundController("pralo");
        gameEngine.roundController("astma");
        gameEngine.roundController("appel");

        assertSame(GameState.LOST, gameEngine.getGameState());
    }

    @Test
    @DisplayName("Woord is geraden moet weer op false nadat een niewe ronde begint")
    void afterWordGuessChangeVariable(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("adres");
        assertFalse(gameEngine.isWordGuessed());

        gameEngine.roundController("adres");
        assertTrue(gameEngine.isWordGuessed());


        gameEngine.nextRound("hallo");
        assertFalse(gameEngine.isWordGuessed());
    }

    @Test
    @DisplayName("Game is niet gestart geeft false")
    void gameNotStarted(){
        GameEngine gameEngine = new GameEngine();
        assertFalse(gameEngine.gameStarted());
    }

    @Test
    @DisplayName("Game is niet gestart getGameInfo geeft start = false terug")
    void gameNotStartedGameInfo(){
        GameEngine gameEngine = new GameEngine();

        String gameInfo = gameEngine.getGameInfo();

        assertEquals("{\"start\":false}", gameInfo);
    }


    @Test
    @DisplayName("Game is gestart en geeft true terug")
    void gameStarted(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("astma");
        assertTrue(gameEngine.gameStarted());
    }

    @Test
    @DisplayName("Game start geeft een woord lengte van 5 terug")
    void lengthFiveWordAtStart(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("papie");

        assertSame(5, gameEngine.getRightLengthByGameState());
    }

    @Test
    @DisplayName("Volgende ronde geeft een woord lengte van 6 terug")
    void lengthSixWordAtNextRound(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("papie");
        gameEngine.roundController("papie");
        gameEngine.nextRound("troela");

        assertSame(6, gameEngine.getRightLengthByGameState());
    }

    @Test
    @DisplayName("Game start geeft een woord lengte van 7 terug")
    void lengthSeveneWordAtFinal(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("papie");

        gameEngine.roundController("papie");
        gameEngine.nextRound("troela");

        gameEngine.roundController("troela");
        gameEngine.nextRound("sevenen");

        assertSame(7, gameEngine.getRightLengthByGameState());
    }

    @Test
    @DisplayName("Na het raden van de laatste woord in de laatste ronde moet je een WIN krijgen")
    void getWinAfterWinning(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("appel");
        gameEngine.roundController("appel");

        gameEngine.nextRound("anders");
        gameEngine.roundController("anders");

        gameEngine.nextRound("afstand");
        gameEngine.roundController("afstand");

        assertSame(GameState.WON, gameEngine.getGameState());
    }

}
