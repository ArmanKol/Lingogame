package hu.bep.presentation;

import hu.bep.LingogameApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = LingogameApplication.class)
@WebAppConfiguration
@DisplayName("Game controller")
public class GameControllerTest {

    @Autowired
    GameController controller;

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
        assertTrue(word.length() == wordLength);
    }

    @Test
    @DisplayName("Check feedback NOT null")
    void feedbackWordNotThrow(){
        controller.startGame();
        String inputWord = "banaan";

        assertNotNull(controller.guessWord(inputWord));
    }
}
