package hu.bep.presentation;

import hu.bep.logic.GameEngine;
import hu.bep.logic.RandomWordGenerator;
import hu.bep.logic.state.GameState;
import hu.bep.persistence.Player;
import hu.bep.persistence.ScoreboardRepository;
import hu.bep.persistence.WordRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;
import java.util.List;

@RestController
public class GameController{
    private static Logger logger = LogManager.getLogger(GameController.class);
    private GameEngine gameEngine = new GameEngine();

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private ScoreboardRepository scoreboardRepository;

    @GetMapping("/api/randomword/{length}")
    public String getRandomWord(@PathVariable int length){
        try{
            List<Integer> listWithWordsID = wordRepository.listWithIDs(length);

            RandomWordGenerator randomGenerator = new RandomWordGenerator(listWithWordsID.size());

            int randomGeneratedIndex = randomGenerator.getRandomNumber();
            int wordID = listWithWordsID.get(randomGeneratedIndex);

            return wordRepository.findByID(wordID).getWord();
        }catch(IndexOutOfBoundsException iobe){
            logger.info(iobe);
            return "NOT_FOUND";
        }
    }

    @GetMapping("/api/lingo/start")
    public ResponseEntity<String> startGame(){
        String randomWord = getRandomWord(gameEngine.getRightLengthByGameState());

        gameEngine.start(randomWord);
        logger.info(randomWord);

        return new ResponseEntity<>(gameEngine.getGameInfo(), HttpStatus.OK);
    }

    @GetMapping("/api/lingo/guess/{word}")
    public ResponseEntity<String> guessWord(@PathVariable String word){
        if(gameEngine.gameStarted()){
            gameEngine.roundController(word);

            if(gameEngine.isWordGuessed()){
                String randomWord = getRandomWord(gameEngine.getRightLengthByGameState());
                logger.info(randomWord);
                gameEngine.nextRound(randomWord);
            }
            return new ResponseEntity<>(gameEngine.getGameInfo(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(gameEngine.getGameInfo(), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping("/api/lingo/savescore/{name}")
    public ResponseEntity<String> saveScore(@PathVariable String name){
        if(gameEngine.getGameState() != GameState.PLAYING){
            String playerName = name;
            int score = gameEngine.getScore();

            scoreboardRepository.save(new Player(score, playerName));

            return new ResponseEntity<>("Saved", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Could not be saved", HttpStatus.OK);
        }
    }

}
