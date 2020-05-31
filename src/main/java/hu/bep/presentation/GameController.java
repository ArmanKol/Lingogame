package hu.bep.presentation;

import hu.bep.logic.GameEngine;
import hu.bep.logic.RandomWordGenerator;
import hu.bep.persistence.WordRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameController{
    private static Logger logger = LogManager.getLogger(GameController.class);
    private GameEngine gameEngine = new GameEngine();

    @Autowired
    private WordRepository wordRepository;

    @GetMapping("/api/randomword/{length}")
    public String getRandomWord(@PathVariable int length){
        List<Integer> listWithWordsID = wordRepository.listWithIDs(length);
        RandomWordGenerator randomGenerator = new RandomWordGenerator(listWithWordsID.size());

        int randomGeneratedIndex = randomGenerator.getRandomNumber();
        int wordID = listWithWordsID.get(randomGeneratedIndex);

        return wordRepository.findByID(wordID).getWord();
    }

    @GetMapping("/api/lingo/start")
    public void startGame(){
        String randomWord = getRandomWord(5);
        gameEngine.start(randomWord);
        logger.info(randomWord);
    }

    @GetMapping("/api/lingo/{word}")
    public ResponseEntity<String> guessWord(@PathVariable String word){
        gameEngine.roundController(word);

        return ResponseEntity.ok(gameEngine.getFeedbackWord());
    }

}
