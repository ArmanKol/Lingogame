package hu.bep.presentation;

import com.google.gson.JsonObject;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class GameController{
    private static final String SESSION_GAME_INFO = "gameInfo";
    private static final Logger LOGGER = LogManager.getLogger(GameController.class);

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private ScoreboardRepository scoreRepository;

    @GetMapping("/api/randomword/{length}")
    public String getRandomWord(@PathVariable int length){
        String response;

        try{
            List<Integer> listWithWordsID = wordRepository.listWithIDs(length);

            RandomWordGenerator randomGenerator = new RandomWordGenerator(listWithWordsID.size());

            int randomGenIndex = randomGenerator.getRandomNumber();
            int wordID = listWithWordsID.get(randomGenIndex);

            response = wordRepository.findByID(wordID).getWord();
        }catch(IndexOutOfBoundsException iobe){
            LOGGER.info(iobe);
            response = "NOT_FOUND";
        }

        return response;
    }

    @GetMapping("/api/lingo/start")
    public ResponseEntity<String> startGame(HttpSession session){
        GameEngine gameEngine = new GameEngine();
        ResponseEntity<String> response;

        LOGGER.info(session.isNew());

        if(session.isNew()){
            String randomWord = getRandomWord(gameEngine.getRightLengthByGameState());

            LOGGER.info(randomWord);

            if(gameEngine.start(randomWord)){
                session.setAttribute(SESSION_GAME_INFO, gameEngine.getGameInfoForSession());
                response = new ResponseEntity<>(gameEngine.getGameInfo(), HttpStatus.OK);
            }else{
                response = new ResponseEntity<>(gameEngine.getGameInfo(), HttpStatus.CONFLICT);
            }
        }else if(session.getAttribute(SESSION_GAME_INFO) != null) {
            String gameInfo = (String) session.getAttribute(SESSION_GAME_INFO);
            GameEngine gameEnginee = GameEngine.turnInfoIntoEngine(gameInfo);

            response = new ResponseEntity<>(gameEnginee.getGameInfo(), HttpStatus.OK);
        }else{
            response = new ResponseEntity<>(gameEngine.getGameInfo(), HttpStatus.BAD_REQUEST);
        }

        return response;
    }

    @PostMapping("/api/lingo/guess")
    public ResponseEntity<String> guessWord(@RequestBody String word, HttpServletRequest request){
        if(request.getSession(false) == null){
            return new ResponseEntity<>("No session available", HttpStatus.BAD_REQUEST);
        }
        String gameInfo = (String) request.getSession(false).getAttribute(SESSION_GAME_INFO);

        GameEngine gameEngine = GameEngine.turnInfoIntoEngine(gameInfo);

        if(gameEngine.gameStarted()){
            gameEngine.roundController(word);

            if(gameEngine.nextRoundAllowed()){
                String randomWord = getRandomWord(gameEngine.getRightLengthByGameState());
                LOGGER.info(randomWord);
                gameEngine.nextRound(randomWord);
            }


            request.getSession(false).setAttribute(SESSION_GAME_INFO, gameEngine.getGameInfoForSession());
            return new ResponseEntity<>(gameEngine.getGameInfo(), HttpStatus.OK);
        }else{
            request.getSession(false).setAttribute(SESSION_GAME_INFO, gameEngine.getGameInfoForSession());
            return new ResponseEntity<>(gameEngine.getGameInfo(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/api/lingo/savescore")
    public ResponseEntity<String> saveScore(@RequestBody String name, HttpServletRequest request){
        LOGGER.info(name);
        JsonObject response = new JsonObject();

        if(request.getSession(false) == null){
            response.addProperty("session", "No session available");
            return new ResponseEntity<>(response.toString(), HttpStatus.BAD_REQUEST);
        }

        String gameInfo = (String) request.getSession(false).getAttribute(SESSION_GAME_INFO);
        GameEngine gameEngine = GameEngine.turnInfoIntoEngine(gameInfo);

        if(gameEngine.getGameState() != GameState.PLAYING){
            int score = gameEngine.getScore();

            scoreRepository.save(new Player(name, score));

            request.getSession(false).invalidate();

            response.addProperty("saved", true);
            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        }
        response.addProperty("saved", false);
        return new ResponseEntity<>(response.toString(), HttpStatus.CONFLICT);
    }

    @GetMapping("/api/lingo/scoreboard")
    public ResponseEntity<List<Player>> getScoreboard(){
        List<Player> scoreboard = scoreRepository.findAll();

        return new ResponseEntity<>(scoreboard, HttpStatus.OK);
    }

}
