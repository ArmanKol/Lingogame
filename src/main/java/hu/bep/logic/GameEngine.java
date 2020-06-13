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
    private boolean wordIsGuessed;

    private Map<Integer, Character> charsWordToGuess = new HashMap<>();
    private Map<Integer, Character> charsInputWord = new HashMap<>();

    private static final Logger LOGGER = LogManager.getLogger(GameEngine.class);

    public GameEngine(){
        levelState= LevelState.FIVE_LETTER_WORD;
    }

    public boolean start(final String word){
        boolean returnValue = false;
        levelState= LevelState.FIVE_LETTER_WORD;

        if(word.length() == getRightLengthByGameState()){
            charsWordToGuess.clear();
            gameState = GameState.PLAYING;
            guessesLeft = 5;
            score = 0;
            wordToGuess = word;
            fillHashMapWithLetters(wordToGuess, charsWordToGuess);

            returnValue = true;
        }

        return returnValue;
    }

    public boolean gameStarted(){
        return (!charsWordToGuess.isEmpty() && gameState == GameState.PLAYING);
    }

    public void roundController(final String wordGuess){
        if(wordGuessed(wordGuess)){
            wordIsGuessed = true;
            calculateScore();
            levelStateController();
        }else if(guessesLeft == 0){
            gameState = GameState.LOST;
        }else{
            guessesLeft--;
        }
    }

    public void nextRound(String newWord){
        if(gameState == GameState.WON){
            return;
        }

        wordIsGuessed = false;
        charsWordToGuess.clear();
        guessesLeft = 5;
        wordToGuess = newWord;
        fillHashMapWithLetters(wordToGuess, charsWordToGuess);
    }

    public boolean nextRoundAllowed(){
        return (isWordGuessed() && getGameState() == GameState.PLAYING);
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
            default:
                levelState = LevelState.FIVE_LETTER_WORD;
                break;
        }
    }

    private boolean wordGuessed(final String inputWord){
        feedbackWord = checkWord(inputWord).get(0).toString();

        return inputWord.equals(wordToGuess);
    }

    private Map<Integer, StringBuilder> checkWord(String inputWord){
        fillHashMapWithLetters(inputWord, charsInputWord);
        Map<Integer, StringBuilder> returnValues = new HashMap<>();

        Map<Integer, Character> hasCharAtDiffIndex = new HashMap<>();
        Map<Integer, String> finalFeedback = new HashMap<>();

        checkIfCharsCorrect(finalFeedback, hasCharAtDiffIndex);
        replaceIncorrectChars(finalFeedback, hasCharAtDiffIndex);

        StringBuilder feedBackWord = createFeedbackString(finalFeedback);

        returnValues.put(0, feedBackWord);

        charsInputWord.clear();
        return returnValues;
    }

    private void checkIfCharsCorrect(Map<Integer, String> finalFeedback, Map<Integer, Character> absentChars){
        for(int i =0; i < wordToGuess.length(); i++){
            if(charsInputWord.get(i) == null || charsWordToGuess.get(i) == null){
                LOGGER.warn("charsInputWord or charsWordToGuess had null");
                continue;
            }
            char charInputWord = charsInputWord.get(i);
            char charWordToGuess = charsWordToGuess.get(i);

            if(charInputWord == charWordToGuess){
                finalFeedback.put(i, charInputWord+": CORRECT " );
            }else if(!charsWordToGuess.containsValue(charInputWord)){
                finalFeedback.put(i, charInputWord + ": INCORRECT ");
            }else{
                absentChars.put(i, charInputWord);
                finalFeedback.put(i, charInputWord + ": PRESENT ");
            }
        }
    }

    private void replaceIncorrectChars(Map<Integer, String> finalFeedback, Map<Integer, Character> absentChars){
        for(int index: absentChars.keySet()){
            char getCharFromIndex = absentChars.get(index);

            int countCharInput = Collections.frequency(charsInputWord.values(), getCharFromIndex);
            int countCharWordToGuess = Collections.frequency(charsWordToGuess.values(), getCharFromIndex);

            if(countCharInput < countCharWordToGuess){
                continue;
            }

            if(countCharInput > countCharWordToGuess){
                finalFeedback.replace(index, getCharFromIndex + ": INCORRECT ");
                charsInputWord.remove(index);
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
        int rightLength;

        switch(levelState){
            case FIVE_LETTER_WORD:
                rightLength = 5;
                break;
            case SIX_LETTER_WORD:
                rightLength = 6;
                break;
            case SEVEN_LETTER_WORD:
                rightLength = 7;
                break;
            default:
                rightLength = 0;
        }

        return rightLength;
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
            gameInfo.addProperty("won", gameState == GameState.WON);
            gameInfo.addProperty("feedbackword", feedbackWord);
        }else if(gameState == GameState.PLAYING){
            gameInfo.addProperty("guessesleft", guessesLeft);
            gameInfo.addProperty("score", score);
            gameInfo.addProperty("wordlength", getRightLengthByGameState());
            gameInfo.addProperty("feedbackword", feedbackWord);
        }else if(gameState == null){
            gameInfo.addProperty("start", false);
            gameInfo.addProperty("guessesleft", guessesLeft);
            gameInfo.addProperty("score", score);
            gameInfo.addProperty("wordlength", getRightLengthByGameState());
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

    public boolean isWordGuessed(){
        return wordIsGuessed;
    }

}
