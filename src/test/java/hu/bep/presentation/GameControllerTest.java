package hu.bep.presentation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hu.bep.LingogameApplication;
import hu.bep.logic.GameEngine;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = LingogameApplication.class)
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Game controller")
public class GameControllerTest {

    @Autowired
    private GameController controller;

//    private MockMvc mockMvc;
//
//    @Before
//    public void setup() {
//        // Process mock annotations
//        MockitoAnnotations.initMocks(this);
//
//        // Setup Spring test in standalone mode
//        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
//                .build();
//
//    }

    @Test
    @Order(1)
    @DisplayName("Woord raden zonder dat het spel gestart is geeft een error terug")
    void guessWordNotStartedGivesError(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertTrue(controller.guessWord("", request).getStatusCode().isError());
    }

    @Test
    @Order(2)
    @DisplayName("Check woord met lengte dat niet in database staat returned NOT_FOUND")
    void wordLength(){
        //Legit word lengths 5,6,7
        assertEquals("NOT_FOUND", controller.getRandomWord(4));
        assertEquals("NOT_FOUND", controller.getRandomWord(8));
    }

    @Test
    @Order(3)
    @DisplayName("Woord lengte == 5")
    void givesWordWithLength5(){
        int wordLength = 5;
        String word = controller.getRandomWord(wordLength);
        assertTrue(word.length() == wordLength);
    }

    @Test
    @Order(4)
    @DisplayName("Check feedback NOT null")
    void feedbackWordNotThrow() throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));

        String inputWord = "banaan";
        assertNotNull(controller.guessWord(inputWord, request));
    }

    @Test
    @Order(5)
    @DisplayName("Woord raden als het spel gestart is geeft een feedback terug")
    void guessWordStartedGivesfeedback(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));
        String body = controller.guessWord("test", request).getBody();

        System.out.println(body);

        JsonObject object = JsonParser.parseString(body).getAsJsonObject();
        assertTrue(object.get("feedbackword") != null);
    }

    @Test
    @Order(6)
    @DisplayName("Als je verliest: win == false response")
    void gameLostSendslostBack(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        controller.startGame(request.getSession(true));
        String body = "";

        for(int g=0; g < 6; g++){
            body = controller.guessWord("test", request).getBody();
        }

        System.out.println(body);

        JsonObject object = JsonParser.parseString(body).getAsJsonObject();
        assertFalse(object.get("won").getAsBoolean());
    }

    @Test
    @Order(7)
    @DisplayName("Wanneer je hebt verloren of gewonnen kun je score opslaan")
    void gameCanBeSavedWhenNotPlayingAnymore(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));
        String body = "";

        for(int g=0; g < 6; g++){
            controller.guessWord("test", request);
        }

        body = controller.saveScore("player", request).getBody();

        assertTrue(body.equals("Saved"));
    }

    @Test
    @Order(8)
    @DisplayName("Wanneer je hebt speelt kun je GEEN score opslaan")
    void gameCanNotBeSavedWhenPlaying(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));
        String body = "";

        for(int g=0; g < 5; g++){
            controller.guessWord("tata", request);
        }

        body = controller.saveScore("player2", request).getBody();
        System.out.println(body);

        assertTrue(body.equals("Could not be saved"));
    }

    @Test
    @Order(9)
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
    @Order(10)
    @DisplayName("Een gestarte game stuurt bij het raden een OK response terug")
    void guessWordAfterStartGivesOk(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        controller.startGame(request.getSession(true));
        GameEngine gameEngine = (GameEngine) request.getSession(false).getAttribute("gameEngine");

        if(gameEngine.gameStarted()){
            assertTrue(controller.guessWord("started", request).getStatusCode() == HttpStatus.OK);
        }
    }

    @Test
    @Order(10)
    @DisplayName("Score kan niet opgeslagen worden zonder session")
    void scoreCantSaveWhenNoSessionAvailable(){
        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity response = controller.saveScore("Pieter", request);

        assertTrue(response.getStatusCode() == HttpStatus.BAD_REQUEST);
        assertEquals("No session available", response.getBody());
    }

}
