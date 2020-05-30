package hu.bep.logic;

import hu.bep.logic.state.GameState;
import hu.bep.logic.state.LevelState;

import java.util.HashMap;
import java.util.Map;

public class GameEngine {
    private String wordToGuess;
    private int guessesLeft;
    private int score;
    private GameState gameState;
    private LevelState levelState;
    private Map<Character, Integer> charsWordToGuess = new HashMap<Character, Integer>();


    public GameEngine(){

    }

    public void start(){
        gameState = GameState.PLAYING;
        levelState= LevelState.FIVE_LETTER_WORD;
        guessesLeft = 5;
        score = 0;
        wordToGuess = "hallo";
        fillHashMapWithLetters(wordToGuess, charsWordToGuess);
    }

    public void roundController(String wordGuess){
        if(wordGuessed(wordGuess)){
            nextRound();
        }

        if(guessesLeft == 0){

        }

        guessesLeft--;
    }

    private void nextRound(){
        charsWordToGuess.clear();
        levelStateController();
        guessesLeft = 5;
        wordToGuess = "poepen";
        fillHashMapWithLetters(wordToGuess, charsWordToGuess);
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

    private boolean wordGuessed(String inputWord){
        Map<Integer, Character> charsGuessedRight = new HashMap<Integer, Character>();
        StringBuilder guessedWord = new StringBuilder();

        if(inputWord.length() <= wordToGuess.length()){
            guessedWord = checkWord(inputWord, charsGuessedRight).get(1);
        }else if(inputWord.length() > wordToGuess.length()){

        }

        if(guessedWord.toString().equals(inputWord)){
            return true;
        }

        return false;
    }

    private Map<Integer, StringBuilder> checkWord(String inputWord, Map<Integer, Character> charsGuessedRight){
        Map<Integer, StringBuilder> returnValues = new HashMap<Integer, StringBuilder>();

        StringBuilder feedBackWord = new StringBuilder();
        StringBuilder guessedChars = new StringBuilder();

        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_YELLOW = "\u001B[33m";

        for(int i =0; i < inputWord.length(); i++){
            char charInputWord = inputWord.charAt(i);
            char charWordToGuess = wordToGuess.charAt(i);

            if(charInputWord == charWordToGuess){
                feedBackWord.append(ANSI_GREEN + charInputWord + ANSI_RESET);
                charsGuessedRight.put(i, charInputWord);
                guessedChars.append(charInputWord);
            }else if(charsWordToGuess.containsKey(charInputWord)){
                if(feedBackWord.toString().contains(String.valueOf(charInputWord)) && charsWordToGuess.get(charInputWord) == 1){
                    feedBackWord.append(ANSI_RED + charInputWord + ANSI_RESET);
                    guessedChars.append(".");
                }else{
                    feedBackWord.append(ANSI_YELLOW + charInputWord + ANSI_RESET);
                    guessedChars.append(".");
                }
            }else{
                feedBackWord.append(ANSI_RED + charInputWord + ANSI_RESET);
                guessedChars.append(".");
            }
        }

        System.out.println("-----------");
        System.out.println(feedBackWord);
        System.out.println(guessedChars);
        System.out.println("-----------");

        returnValues.put(0, feedBackWord);
        returnValues.put(1, guessedChars);

        return returnValues;
    }

    private void score(StringBuilder input, StringBuilder guessedChars){
        if(input == guessedChars){
            score = 50;
        }

    }


    private void fillHashMapWithLetters(String givenWord, Map<Character, Integer> map){
        for(int i = 0; i < givenWord.length(); i++){
            char character = givenWord.charAt(i);
            if(map.containsKey(character)){
                map.replace(character, map.get(character)+1);
            }else{
                map.put(character, 1);
            }
        }
    }

    public String getGivenWord(){
        return wordToGuess;
    }

}
