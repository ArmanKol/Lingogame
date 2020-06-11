package hu.bep.presentation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hu.bep.LingogameApplication;
import hu.bep.logic.GameEngine;
import hu.bep.persistence.Player;
import hu.bep.persistence.ScoreboardRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Game controller")
class GameControllerTest {

    @Autowired
    private GameController controller;

    @Autowired
    private ScoreboardRepository scoreboardRepository;

    @Test
    @DisplayName("Woord raden zonder dat het spel gestart is geeft een error terug")
    void guessWordNotStartedGivesError() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertTrue(controller.guessWord("", request).getStatusCode().isError());
    }

    @Test
    @DisplayName("Check woord met lengte dat niet in database staat returned NOT_FOUND")
    void wordLength(){
        //Legit word lengths 5,6,7
        assertEquals("NOT_FOUND", controller.getRandomWord(4));
        assertEquals("NOT_FOUND", controller.getRandomWord(8));
    }

    @Test
    @DisplayName("Woord lengte == 5")
    void givesWordWithLength5(){
        int wordLength = 5;
        String word = controller.getRandomWord(wordLength);

        assertSame(wordLength, word.length());
    }

    @Test
    @DisplayName("Check feedback NOT null")
    void feedbackWordNotThrow() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));

        String inputWord = "banaan";
        assertNotNull(controller.guessWord(inputWord, request));
    }

    @Test
    @DisplayName("Woord raden als het spel gestart is geeft een feedback terug")
    void guessWordStartedGivesfeedback(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));
        String body = controller.guessWord("test", request).getBody();

        JsonObject object = JsonParser.parseString(body).getAsJsonObject();

        assertNotNull(object.get("feedbackword"));
    }

    @Test
    @DisplayName("Als je verliest: win == false response")
    void gameLostSendslostBack(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        controller.startGame(request.getSession(true));
        String body = "";

        for(int g=0; g < 6; g++){
            body = controller.guessWord("test", request).getBody();
        }

        JsonObject object = JsonParser.parseString(body).getAsJsonObject();
        assertFalse(object.get("won").getAsBoolean());
    }

    @Test
    @Transactional
    @DisplayName("Wanneer je hebt verloren of gewonnen kun je score opslaan")
    void gameCanBeSavedWhenNotPlayingAnymore(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        controller.startGame(request.getSession(true));
        String body = "";

        for(int g=0; g < 6; g++){
            controller.guessWord("test", request);
        }

        body = controller.saveScore("player", request).getBody();

        assertEquals("Saved", body);

        scoreboardRepository.deleteByPlayerName("player");
    }

    @Test
    @DisplayName("Wanneer je speelt kun je GEEN score opslaan")
    void gameCanNotBeSavedWhenPlaying(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));
        String body = "";

        for(int g=0; g < 5; g++){
            controller.guessWord("tata", request);
        }

        body = controller.saveScore("player2", request).getBody();

        assertEquals("Could not be saved", body);
    }

    @Test
    @DisplayName("Woord geraden begint een nieuwe ronde")
    void wordGuessedNewRound(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));
        GameEngine gameEngine;

        gameEngine = (GameEngine) request.getSession(false).getAttribute("gameEngine");

        String wordToGuess = gameEngine.getGivenWord();
        int lengthWordRoundOne = wordToGuess.length();

        controller.guessWord(wordToGuess, request);

        gameEngine = (GameEngine) request.getSession(false).getAttribute("gameEngine");

        int lenthWordRoundTwo = gameEngine.getGivenWord().length();

        assertTrue(lenthWordRoundTwo > lengthWordRoundOne);
    }

    @Test
    @DisplayName("Een gestarte game stuurt bij het raden een OK response terug")
    void guessWordAfterStartGivesOk(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));
        GameEngine gameEngine = (GameEngine) request.getSession(false).getAttribute("gameEngine");

        if(gameEngine.gameStarted()){
            assertSame(HttpStatus.OK, controller.guessWord("started", request).getStatusCode());
        }
    }

    @Test
    @DisplayName("Score kan niet opgeslagen worden zonder session")
    void scoreCantSaveWhenNoSessionAvailable(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity response = controller.saveScore("Pieter", request);

        assertSame(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No session available", response.getBody());
    }

    @Test
    @DisplayName("getScoreboard geeft een response ok met een lijst van legit player object terug")
    void getScoreboardReturnsListofPlayersWithScore(){
        ResponseEntity response = controller.getScoreboard();
        HttpStatus responseStatus = response.getStatusCode();
        List<Player> scores = (List<Player>) response.getBody();

        for(Player player : scores){
            assertNotSame(0,player.getId());
            assertNotNull(player.getPlayerName());
        }
        assertSame(HttpStatus.OK, responseStatus);
    }

}
