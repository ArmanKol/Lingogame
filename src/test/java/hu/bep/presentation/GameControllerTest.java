package hu.bep.presentation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hu.bep.LingogameApplication;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = LingogameApplication.class)
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Game controller")
public class GameControllerTest {

    @Autowired
    GameController controller;

    @Test
    @Order(1)
    @DisplayName("Woord raden zonder dat het spel gestart is geeft een error terug")
    void guessWordNotStartedGivesError(){
        System.out.println(controller.guessWord("").getBody());
        assertTrue(controller.guessWord("").getStatusCode().isError());
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
    void feedbackWordNotThrow(){
        controller.startGame();
        String inputWord = "banaan";

        assertNotNull(controller.guessWord(inputWord));
    }

    @Test
    @Order(5)
    @DisplayName("Woord raden als het spel gestart is geeft een feedback terug")
    void guessWordStartedGivesfeedback(){
        controller.startGame();
        String body = controller.guessWord("test").getBody();
        JsonObject object = JsonParser.parseString(body).getAsJsonObject();
        assertTrue(object.get("feedbackword") != null);
    }

    @Test
    @Order(6)
    @DisplayName("Als je verliest: win == false response")
    void gameLostSendslostBack(){
        controller.startGame();
        String body = "";

        for(int g=0; g < 6; g++){
            body = controller.guessWord("test").getBody();
        }

        JsonObject object = JsonParser.parseString(body).getAsJsonObject();
        assertFalse(object.get("won").getAsBoolean());
    }

    @Test
    @Order(7)
    @DisplayName("Wanneer je hebt verloren of gewonnen kun je score opslaan")
    void gameCanBeSavedWhenNotPlayingAnymore(){
        controller.startGame();
        String body = "";

        for(int g=0; g < 6; g++){
            controller.guessWord("test");
        }

        body = controller.saveScore("player").getBody();

        assertTrue(body.equals("Saved"));
    }

    @Test
    @Order(8)
    @DisplayName("Wanneer je hebt speelt kun je GEEN score opslaan")
    void gameCanNotBeSavedWhenPlaying(){
        controller.startGame();
        String body = "";

        for(int g=0; g < 5; g++){
            controller.guessWord("tata");
        }

        body = controller.saveScore("player2").getBody();
        System.out.println(body);

        assertTrue(body.equals("Could not be saved"));
    }


}
