package hu.bep.logic;

import com.google.gson.JsonObject;
import hu.bep.logic.state.GameState;
import hu.bep.logic.state.LevelState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameEngine {
    private String wordToGuess;
    private String feedbackWord;
    private int guessesLeft;
    private int score;
    private GameState gameState;
    private LevelState levelState;
    private boolean wordGuessed;

    private Map<Integer, Character> charsFromWordToGuess = new HashMap<Integer, Character>();
    private Map<Integer, Character> charsFromInputWord = new HashMap<Integer, Character>();

    private static Logger logger = LogManager.getLogger(GameEngine.class);

    public GameEngine(){
        levelState= LevelState.FIVE_LETTER_WORD;
    }

    public boolean start(String word){
        boolean returnValue = false;
        levelState= LevelState.FIVE_LETTER_WORD;

        if(word.length() == getRightLengthByGameState()){
            charsFromWordToGuess.clear();
            gameState = GameState.PLAYING;
            guessesLeft = 5;
            score = 0;
            wordToGuess = word;
            fillHashMapWithLetters(wordToGuess, charsFromWordToGuess);

            returnValue = true;
        }

        return returnValue;
    }

    public boolean gameStarted(){
        if(charsFromWordToGuess.size() > 0 && gameState == GameState.PLAYING){
            return true;
        }

        return false;
    }

    public void roundController(String wordGuess){
        if(wordGuessed(wordGuess)){
            wordGuessed = true;
            calculateScore();
            levelStateController();
            return;
        }

        if(guessesLeft == 0){
            gameState = GameState.LOST;
            return;
        }else{
            guessesLeft--;
        }
    }

    public void nextRound(String newWord){
        //levelStateController();
        if(gameState == GameState.WON){
            return;
        }

        wordGuessed = false;
        charsFromWordToGuess.clear();
        guessesLeft = 5;
        wordToGuess = newWord;
        fillHashMapWithLetters(wordToGuess, charsFromWordToGuess);
    }

    private void levelStateController(){
        switch(levelState){
            case FIVE_LETTER_WORD:
                levelState = LevelState.SIX_LETTER_WORD;
                break;
            case SIX_LETTER_WORD:
                levelState = LevelState.SEVEN_LETTER_WORD;
                break;
            case SEVEN_LETTER_WORD:
                gameState = GameState.WON;
                break;
        }
    }

    public boolean wordGuessed(String inputWord){
        feedbackWord = checkWord(inputWord).get(0).toString();

        return inputWord.equals(wordToGuess);
    }

    private Map<Integer, StringBuilder> checkWord(String inputWord){
        fillHashMapWithLetters(inputWord, charsFromInputWord);
        Map<Integer, StringBuilder> returnValues = new HashMap<Integer, StringBuilder>();

        Map<Integer, Character> hasCharAtDifferentIndex = new HashMap<Integer, Character>();
        Map<Integer, String> finalFeedback = new HashMap<Integer, String>();

        checkIfCharsCorrect(finalFeedback, hasCharAtDifferentIndex);
        replaceIncorrectChars(finalFeedback, hasCharAtDifferentIndex);

        StringBuilder feedBackWord = createFeedbackString(finalFeedback);

        returnValues.put(0, feedBackWord);

        charsFromInputWord.clear();
        return returnValues;
    }

    private void checkIfCharsCorrect(Map<Integer, String> finalFeedback, Map<Integer, Character> absentChars){
        for(int i =0; i < wordToGuess.length(); i++){
            try{
                char charInputWord = charsFromInputWord.get(i);
                char charWordToGuess = charsFromWordToGuess.get(i);

                if(charInputWord == charWordToGuess){
                    finalFeedback.put(i, charInputWord+": CORRECT " );
                }else if(!charsFromWordToGuess.containsValue(charInputWord)){
                    finalFeedback.put(i, charInputWord + ": INCORRECT ");
                }else {
                    absentChars.put(i, charInputWord);
                    finalFeedback.put(i, charInputWord + ": PRESENT ");
                }
            }catch(NullPointerException npe){
                continue;
            }
        }
    }

    private void replaceIncorrectChars(Map<Integer, String> finalFeedback, Map<Integer, Character> absentChars){
        for(int index: absentChars.keySet()){
            char getCharFromIndex = absentChars.get(index);

            int countCharInput = Collections.frequency(charsFromInputWord.values(), getCharFromIndex);
            int countCharWordToGuess = Collections.frequency(charsFromWordToGuess.values(), getCharFromIndex);

            if(!(countCharInput > countCharWordToGuess)){
                continue;
            }

            if(countCharInput > countCharWordToGuess){
                finalFeedback.replace(index, getCharFromIndex + ": INCORRECT ");
                charsFromInputWord.remove(index);
            }
        }
    }

    private StringBuilder createFeedbackString(Map<Integer, String> map){
        StringBuilder feedBackWord = new StringBuilder();

        for(String c : map.values()){
            feedBackWord.append(c);
        }

        return feedBackWord;
    }

    private void calculateScore(){
        if(guessesLeft == 5){
            score += 50;
        }

        if(guessesLeft == 4){
            score += 40;
        }

        if(guessesLeft == 3){
            score += 30;
        }

        if(guessesLeft == 2){
            score += 20;
        }

        if(guessesLeft == 1){
            score += 10;
        }
    }

    public int getRightLengthByGameState(){
        switch(levelState){
            case FIVE_LETTER_WORD:
                return 5;
            case SIX_LETTER_WORD:
                return 6;
            case SEVEN_LETTER_WORD:
                return 7;
            default:
                return 0;
        }
    }

    private void fillHashMapWithLetters(String inputWord, Map<Integer, Character> map){
        for(int i =0; i < inputWord.length(); i++){
            map.put(i, inputWord.toCharArray()[i]);
        }
    }

    public String getGameInfo(){
        JsonObject gameInfo = new JsonObject();

        if(gameState == GameState.WON || gameState == GameState.LOST){
            gameInfo.addProperty("score", score);
            gameInfo.addProperty("won", ((gameState == GameState.WON) ? true : false));
        }else if(gameState == GameState.PLAYING){
            gameInfo.addProperty("guessesleft", guessesLeft);
            gameInfo.addProperty("score", score);
            gameInfo.addProperty("wordlength", getRightLengthByGameState());
            gameInfo.addProperty("feedbackword", feedbackWord);
        }else if(gameState == null){
            gameInfo.addProperty("start", false);
        }

        return gameInfo.toString();
    }

    public String getGivenWord(){
        return wordToGuess;
    }

    public int getScore(){
        return score;
    }

    public GameState getGameState(){
        return gameState;
    }

    public String getFeedbackWord(){
        return feedbackWord;
    }

    public LevelState getLevelState(){
        return levelState;
    }

    public boolean isWordGuessed(){
        return wordGuessed;
    }

    public void setWordToGuess(String word){
        this.wordToGuess = word;
    }

}
