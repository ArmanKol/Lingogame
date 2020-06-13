package hu.bep.logic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hu.bep.logic.state.GameState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Game engine")
class GameEngineTest {
    private static final String length3 = "ban";

    private static final String specialChars1 = "back-end";

    private static final String wordWithSpace = "su pe";
    private static final String wordWithNumbers = "3huis";

    private static final String wordToGuess_5letters = "broer";
    private static final String wordToGuess_5letters_same = "broer";

    @Test
    @DisplayName("Woord niet geraden")
    void wordGuessed_NotSameWord_ReturnFalse(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start(wordToGuess_5letters);

        JsonObject gameInfoBefore = JsonParser.parseString(gameEngine.getGameInfo()).getAsJsonObject();
        gameEngine.roundController(length3);
        JsonObject gameInfoAfter = JsonParser.parseString(gameEngine.getGameInfo()).getAsJsonObject();

        int guessesLeftBefore = gameInfoBefore.get("guessesleft").getAsInt();
        int guessesLeftAfter = gameInfoAfter.get("guessesleft").getAsInt();

        assertSame(guessesLeftBefore -1, guessesLeftAfter);
    }

    @Test
    @DisplayName("Woord geraden")
    void wordGuessed_SameWord_ReturnTrue(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start(wordToGuess_5letters);

        JsonObject gameInfoBefore = JsonParser.parseString(gameEngine.getGameInfo()).getAsJsonObject();
        gameEngine.roundController(wordToGuess_5letters_same);
        JsonObject gameInfoAfter = JsonParser.parseString(gameEngine.getGameInfo()).getAsJsonObject();

        int guessesLeftBefore = gameInfoBefore.get("guessesleft").getAsInt();
        int guessesLeftAfter = gameInfoAfter.get("guessesleft").getAsInt();

        assertSame(guessesLeftBefore, guessesLeftAfter);
    }

    @Test
    @DisplayName("Speciale characters moeten INCORRECT teruggeven.")
    void roundController_InputSpecialChars_ReturnIncorrect(){
        GameEngine gameEngine = new GameEngine();

        //word to guess = broer
        gameEngine.start(wordToGuess_5letters);

        //input word = back-end
        gameEngine.roundController(specialChars1);


        assertEquals("b: CORRECT a: INCORRECT c: INCORRECT k: INCORRECT -: INCORRECT ", gameEngine.getFeedbackWord());
    }

    @Test
    @DisplayName("Characters met spatie moeten incorrect teruggeven")
    void roundController_InputCharWithSpace_ReturnIncorrect(){
        GameEngine gameEngine = new GameEngine();

        //word to guess = 3huis
        gameEngine.start(wordWithSpace);

        gameEngine.roundController(wordWithSpace);

        assertEquals("s: CORRECT u: CORRECT  : CORRECT p: CORRECT e: CORRECT ", gameEngine.getFeedbackWord());
    }

    @Test
    @DisplayName("Woord met een cijfer erin moet INCORRECT teruggeven")
    void roundController_InputNumber_ReturnIncorrect(){
        GameEngine gameEngine = new GameEngine();

        //word to guess = huise
        gameEngine.start("huise");

        //Input word 3huis
        gameEngine.roundController(wordWithNumbers);

        assertEquals("3: INCORRECT h: PRESENT u: PRESENT i: PRESENT s: PRESENT ", gameEngine.getFeedbackWord());
    }

    @Test
    @DisplayName("Als er een verkeerde lengte wordt meegegeven dan de levelstate moet er een false returnen")
    void start_LevelStateDiffWordLength_ReturnFalse(){
        GameEngine gameEngine = new GameEngine();

        //word to guess = papier
        boolean started = gameEngine.start("papier");

        assertFalse(started);
    }

    @Test
    @DisplayName("De game kan gestart worden als de juiste woord lengte word meegegeven")
    void start_WordLengthCorrect_ReturnTrue(){
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
    void wordGuessed_CorrectWord_ReturnTrue(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");
        gameEngine.roundController("hallo");

        assertTrue(gameEngine.isWordGuessed());
    }

    @Test
    @DisplayName("Woord in een keer geraden is 50 punten")
    void roundController_WordGuessedFirst_FiftyPoints(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");
        gameEngine.roundController("hallo");

        assertSame(50, gameEngine.getScore());
    }

    @Test
    @DisplayName("Woord 1 keer fout en daarna goed is 40 punten")
    void roundController_WordGuessedSecond_FortyPoints(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");
        gameEngine.roundController("mamaa");
        gameEngine.roundController("hallo");

        assertSame(40, gameEngine.getScore());
    }

    @Test
    @DisplayName("Woord 2 keer fout en daarna goed is 30 punten")
    void roundController_WordGuessedThird_ThirtyPoints(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");
        gameEngine.roundController("wekke");
        gameEngine.roundController("pecht");
        gameEngine.roundController("hallo");

        assertSame(30, gameEngine.getScore());
    }

    @Test
    @DisplayName("Woord 3 keer fout en daarna goed is 20 punten")
    void roundController_WordGuessedFourth_TwentyPoints(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");
        gameEngine.roundController("astma");
        gameEngine.roundController("asiel");
        gameEngine.roundController("adres");
        gameEngine.roundController("hallo");

        assertSame(20, gameEngine.getScore());
    }

    @Test
    @DisplayName("Woord 4 keer fout en daarna goed is 10 punten")
    void roundController_WordGuessedFifth_TenPoints(){
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
    void roundController_NeverGuessed_ZeroPoints(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("hallo");

        gameEngine.roundController("pallo");
        gameEngine.roundController("tatto");
        gameEngine.roundController("mallo");
        gameEngine.roundController("pralo");
        gameEngine.roundController("astma");
        gameEngine.roundController("appel");

        assertSame(0, gameEngine.getScore());
        assertSame(GameState.LOST, gameEngine.getGameState());
    }

    @Test
    @DisplayName("Woord is geraden moet weer op false nadat een niewe ronde begint")
    void isWordGuessed_AfterGuessed_ReturnFalse(){
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
    void gameStarted_Not_ReturnFalse(){
        GameEngine gameEngine = new GameEngine();
        assertFalse(gameEngine.gameStarted());
    }

    @Test
    @DisplayName("Game is niet gestart getGameInfo geeft start = false terug")
    void getGameInfo_NotStarted_ReturnStartFalse(){
        GameEngine gameEngine = new GameEngine();
        JsonObject gameInfo = JsonParser.parseString(gameEngine.getGameInfo()).getAsJsonObject();

        boolean startValue = gameInfo.get("start").getAsBoolean();

        assertFalse(startValue);
    }


    @Test
    @DisplayName("Game is gestart en geeft true terug")
    void gameStarted_Started_ReturnsTrue(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("astma");
        assertTrue(gameEngine.gameStarted());
    }

    @Test
    @DisplayName("Game start geeft een woord lengte van 5 terug")
    void gameState_CorrectLength_ReturnLengthFive(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("papie");

        assertSame(5, gameEngine.getRightLengthByGameState());
    }

    @Test
    @DisplayName("Volgende ronde geeft een woord lengte van 6 terug")
    void nextRound_SecondRound_ReturnWordSixLetter(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("papie");
        gameEngine.roundController("papie");
        gameEngine.nextRound("troela");

        assertSame(6, gameEngine.getRightLengthByGameState());
    }

    @Test
    @DisplayName("Final ronde geeft een woord lengte van 7 terug")
    void nextRound_FinalRound_ReturnWordSevenLetters(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("papie");

        gameEngine.roundController("papie");
        gameEngine.nextRound("troela");

        gameEngine.roundController("troela");
        gameEngine.nextRound("sevenen");

        assertSame(7, gameEngine.getRightLengthByGameState());
    }

    @Test
    @DisplayName("Na het raden van het laatste woord in de laatste ronde moet je een WIN krijgen")
    void gameState_Won_GameStateWon(){
        GameEngine gameEngine = new GameEngine();
        gameEngine.start("appel");
        gameEngine.roundController("appel");

        gameEngine.nextRound("anders");
        gameEngine.roundController("anders");

        gameEngine.nextRound("afstand");
        gameEngine.roundController("afstand");

        assertSame(GameState.WON, gameEngine.getGameState());
    }

    @Test
    @DisplayName("Als je alle rondes gewonnen hebt dan gameinfo won returned true")
    void gameInfo_Won_ReturnTrue(){
        GameEngine gameEngine = new GameEngine();

        gameEngine.start("astma");
        gameEngine.roundController("astma");

        gameEngine.nextRound("badpak");
        gameEngine.roundController("badpak");

        gameEngine.nextRound("element");
        gameEngine.roundController("element");

        JsonObject gameInfo = JsonParser.parseString(gameEngine.getGameInfo()).getAsJsonObject();

        boolean wonValue = gameInfo.get("won").getAsBoolean();

        assertTrue(wonValue);
    }

    @Test
    @DisplayName("Volgende ronde alleen toegestaan als het woord geraden is en gamestate==Playing")
    void nextRoundAllowed_WordGuessed_ReturnTrue(){
        GameEngine gameEngine = new GameEngine();

        gameEngine.start("astma");
        gameEngine.roundController("astma");

        assertTrue(gameEngine.nextRoundAllowed());
    }

    @Test
    @DisplayName("Als de game niet gestart is dan nextRoundAllowed==false")
    void nextRoundAllowed_GameNotStarted_ReturnFalse(){
        GameEngine gameEngine = new GameEngine();

        assertFalse(gameEngine.nextRoundAllowed());
    }

    @Test
    @DisplayName("Als het woord nog niet geraden is dan nextRoundAllowed==false")
    void nextRoundAllowed_WordNotGuessed_ReturnFalse(){
        GameEngine gameEngine = new GameEngine();

        gameEngine.start("patat");
        assertFalse(gameEngine.nextRoundAllowed());
    }

}
