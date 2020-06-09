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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;
import java.util.List;

@RestController
public class GameController{
    private static Logger logger = LogManager.getLogger(GameController.class);

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
    public ResponseEntity<String> startGame(HttpSession session){
        GameEngine gameEngine = new GameEngine();

        if(session.isNew()){
            String randomWord = getRandomWord(gameEngine.getRightLengthByGameState());

            logger.info(session.isNew());
            logger.info(randomWord);

            if(gameEngine.start(randomWord)){
                session.setAttribute("gameEngine", gameEngine);
                return new ResponseEntity<>(gameEngine.getGameInfo(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(gameEngine.getGameInfo(), HttpStatus.CONFLICT);
            }
        }

        return new ResponseEntity<>(gameEngine.getGameInfo(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/api/lingo/guess/{word}")
    public ResponseEntity<String> guessWord(@PathVariable String word, HttpServletRequest request){
        if(request.getSession(false) == null){
            return new ResponseEntity("No session available", HttpStatus.BAD_REQUEST);
        }

        GameEngine gameEnginee = (GameEngine) request.getSession(false).getAttribute("gameEngine");

        if(gameEnginee.gameStarted()){
            gameEnginee.roundController(word);

            if(gameEnginee.isWordGuessed() && gameEnginee.getGameState() == GameState.PLAYING){
                String randomWord = getRandomWord(gameEnginee.getRightLengthByGameState());
                logger.info(randomWord);
                gameEnginee.nextRound(randomWord);
            }
            return new ResponseEntity<>(gameEnginee.getGameInfo(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(gameEnginee.getGameInfo(), HttpStatus.CONFLICT);
        }
    }

    @RequestMapping("/api/lingo/savescore/{name}")
    public ResponseEntity<String> saveScore(@PathVariable String name, HttpServletRequest request){
        if(request.getSession(false) == null){
            return new ResponseEntity("No session available", HttpStatus.BAD_REQUEST);
        }

        GameEngine gameEngine = (GameEngine) request.getSession(false).getAttribute("gameEngine");

        if(gameEngine.getGameState() != GameState.PLAYING){
            String playerName = name;
            int score = gameEngine.getScore();

            scoreboardRepository.save(new Player(playerName, score));

            return new ResponseEntity<>("Saved", HttpStatus.OK);
        }else{
            return new ResponseEntity<>("Could not be saved", HttpStatus.CONFLICT);
        }
    }

    @RequestMapping("/api/lingo/scoreboard")
    public ResponseEntity<List<Player>> getScoreboard(){
        List<Player> scoreboard = scoreboardRepository.findAll();

        return new ResponseEntity(scoreboard, HttpStatus.OK);
    }

}
