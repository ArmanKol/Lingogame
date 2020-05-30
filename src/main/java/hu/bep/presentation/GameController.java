package hu.bep.presentation;

import hu.bep.logic.RandomWordGenerator;
import hu.bep.persistence.WordRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController{
    private static Logger logger = LogManager.getLogger(GameController.class);

    @Autowired
    private WordRepository wordRepository;

    @GetMapping("/api/randomword")
    public String getRandomWord(){
        RandomWordGenerator randomGenerator = new RandomWordGenerator(wordRepository.getMinID(), wordRepository.getMaxID());

        int wordID = randomGenerator.getRandomNumber();

        return wordRepository.findByID(wordID).getWord();
    }


}
