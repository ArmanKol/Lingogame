package hu.bep.presentation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    @DisplayName("Woord raden zonder dat het spel gestart is geeft een bad request")
    void guessWord_GameNotStarted_GivesError(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertSame(HttpStatus.BAD_REQUEST, controller.guessWord("", request).getStatusCode());
    }

    @Test
    @DisplayName("Check woord met lengte dat niet in database staat returned NOT_FOUND")
    void getRandomWord_LengthNotFound_ReturnNOTFOUND(){
        //Legit word lengths 5,6,7
        assertEquals("NOT_FOUND", controller.getRandomWord(4));
        assertEquals("NOT_FOUND", controller.getRandomWord(8));
    }

    @Test
    @DisplayName("Woord lengte == 5")
    void getRandomWord_LengthFive_ReturnWordLengthFive(){
        int wordLength = 5;
        String word = controller.getRandomWord(wordLength);

        assertSame(wordLength, word.length());
    }

    @Test
    @DisplayName("Check feedback NOT null")
    void guessWord_ValidWord_ReturnsFeedback(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));

        String inputWord = "banaan";
        assertNotNull(controller.guessWord(inputWord, request));
    }

    @Test
    @DisplayName("Als je verliest: win == false response")
    void guessWord_NotGuessed_ReturnFalseWin(){
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
    void saveScore_WonOrLost_ScoreSaved(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        controller.startGame(request.getSession(true));
        String body = "";

        for(int g=0; g < 6; g++){
            controller.guessWord("test", request);
        }

        body = controller.saveScore("player", request).getBody();

        JsonObject object = JsonParser.parseString(body.toString()).getAsJsonObject();

        assertTrue(object.get("saved").getAsBoolean());

        scoreboardRepository.deleteByPlayerName("player");
    }

    @Test
    @DisplayName("Wanneer je speelt kun je GEEN score opslaan")
    void saveScore_WhenPlaying_CantSaveScore(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));
        String body = "";

        for(int g=0; g < 5; g++){
            controller.guessWord("tata", request);
        }

        body = controller.saveScore("player2", request).getBody();

        JsonObject object = JsonParser.parseString(body.toString()).getAsJsonObject();

        assertFalse(object.get("saved").getAsBoolean());
    }

    @Test
    @DisplayName("Woord geraden begint een nieuwe ronde")
    void guessWord_WordGuessed_StartNewRound(){
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
    void guessWord_RoundStarted_ResponseOk(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));
        GameEngine gameEngine = (GameEngine) request.getSession(false).getAttribute("gameEngine");

        if(gameEngine.gameStarted()){
            assertSame(HttpStatus.OK, controller.guessWord("started", request).getStatusCode());
        }
    }

    @Test
    @DisplayName("Score kan niet opgeslagen worden zonder session")
    void saveScore_NoSession_CantSaveScore(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity response = controller.saveScore("Pieter", request);

        JsonObject object = JsonParser.parseString(response.getBody().toString()).getAsJsonObject();

        assertSame(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No session available", object.get("session").getAsString());
    }

    @Test
    @DisplayName("getScoreboard geeft een response ok met een lijst van legit player object terug")
    void getScoreboard_Call_ReturnsListPlayers(){
        ResponseEntity response = controller.getScoreboard();
        HttpStatus responseStatus = response.getStatusCode();
        List<Player> scores = (List<Player>) response.getBody();

        for(Player player : scores){
            assertNotSame(0,player.getId());
            assertNotNull(player.getPlayerName());
        }
        assertSame(HttpStatus.OK, responseStatus);
    }

    @Test
    @DisplayName("Bij een bestaande session start game geeft de huidige session terug")
    void startGame_SessionExists_ReturnSession(){
        HttpSession session = mock(HttpSession.class);

        GameEngine gameEngine = new GameEngine();
        gameEngine.start("teste");

        System.out.println();

        when(session.isNew()).thenReturn(true).thenReturn(false).thenReturn(false);
        when(session.getAttribute("gameEngine")).thenReturn(gameEngine);

        ResponseEntity response = controller.startGame(session);

        ResponseEntity response2 = controller.startGame(session);

        System.out.println(response);
        System.out.println(response2);

        assertSame(HttpStatus.OK, response2.getStatusCode());
        assertEquals(response.getBody(), response2.getBody());
    }

}
