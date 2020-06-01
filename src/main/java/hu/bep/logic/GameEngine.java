package hu.bep.logic;

import hu.bep.logic.state.GameState;
import hu.bep.logic.state.LevelState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameEngine {
    private static GameEngine instance;

    private String wordToGuess;
    private String feedbackWord;
    private int guessesLeft;
    private int score;
    private GameState gameState;
    private LevelState levelState;

    private Map<Integer, Character> charsFromWordToGuess = new HashMap<Integer, Character>();
    private Map<Integer, Character> charsFromInputWord = new HashMap<Integer, Character>();

    public GameEngine(){

    }

    public void start(String word){
        gameState = GameState.PLAYING;
        levelState= LevelState.FIVE_LETTER_WORD;
        guessesLeft = 5;
        score = 0;
        wordToGuess = word;
        fillHashMapWithLetters(wordToGuess, charsFromWordToGuess);
    }

    public void roundController(String wordGuess){
        if(wordGuessed(wordGuess)){
            calculateScore();
            nextRound();
            return;
        }

        if(guessesLeft == 0){
            gameState = GameState.LOST;
            restart();
            return;
        }else{
            guessesLeft--;
        }
    }

    private void nextRound(){
        charsFromWordToGuess.clear();

        levelStateController();
        guessesLeft = 5;
        wordToGuess = "poepen";
        fillHashMapWithLetters(wordToGuess, charsFromWordToGuess);
    }

    private void restart(){
        charsFromWordToGuess.clear();

        gameState = GameState.PLAYING;
        guessesLeft = 5;
        score = 0;
        wordToGuess = "hallo";
        fillHashMapWithLetters(wordToGuess, charsFromWordToGuess);
    }

    private void finish(){

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
                levelState = LevelState.FIVE_LETTER_WORD;
                break;
        }
    }

    public boolean wordGuessed(String inputWord){
        feedbackWord = checkWord(inputWord).get(0).toString();

        if(inputWord.equals(wordToGuess)){
            return true;
        }

        return false;
    }

    private Map<Integer, StringBuilder> checkWord(String inputWord){
        fillHashMapWithLetters(inputWord, charsFromInputWord);
        Map<Integer, StringBuilder> returnValues = new HashMap<Integer, StringBuilder>();

        Map<Integer, Character> hasCharAtDifferentIndex = new HashMap<Integer, Character>();
        Map<Integer, String> finalFeedback = new HashMap<Integer, String>();

        checkIfCharsCorrect(finalFeedback, hasCharAtDifferentIndex);
        replaceIncorrectChars(finalFeedback, hasCharAtDifferentIndex);

        StringBuilder feedBackWord = createFeedbackString(finalFeedback);

//        System.out.println("-----------");
//        System.out.println(feedBackWord);
//        System.out.println("-----------");

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

    private void fillHashMapWithLetters(String inputWord, Map<Integer, Character> map){
        for(int i =0; i < inputWord.length(); i++){
            map.put(i, inputWord.toCharArray()[i]);
        }
    }

    public String getGivenWord(){
        return wordToGuess;
    }

    public int getScore(){
        return score;
    }

    public static GameEngine getInstance(){
        if(instance == null){
            instance = new GameEngine();
        }

        return instance;
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

}
