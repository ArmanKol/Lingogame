package hu.bep.presentation;

import hu.bep.logic.RandomWordGenerator;
import hu.bep.persistence.WordRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class GameController{
    private static Logger logger = LogManager.getLogger(GameController.class);

    @Autowired
    private WordRepository wordRepository;

    @GetMapping("/lingo/randomword")
    public String getRandom(){
        RandomWordGenerator randomGenerator = new RandomWordGenerator(wordRepository.getMinID(), wordRepository.getMaxID());

        int wordID = randomGenerator.getRandomNumber();
        String word = wordRepository.findByID(wordID).getWord();

        return word;
    }
}
