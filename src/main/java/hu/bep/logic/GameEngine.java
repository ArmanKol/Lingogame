package hu.bep.logic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hu.bep.logic.state.GameState;
import hu.bep.logic.state.LevelState;

public class GameEngine {
    private WordChecker wordChecker;

    private int guessesLeft;
    private int score;
    private GameState gameState;
    private LevelState levelState;
    private boolean wordIsGuessed;

    public GameEngine(){
        levelState= LevelState.FIVE_LETTER_WORD;
        wordChecker = new WordChecker();
    }

    private GameEngine(int guessesLeft, int score, GameState gameState, LevelState levelState, boolean wordIsGuessed, String wordToGuess){
        this.guessesLeft = guessesLeft;
        this.score = score;
        this.gameState = gameState;
        this.levelState = levelState;
        this.wordIsGuessed = wordIsGuessed;

        wordChecker = new WordChecker();
        wordChecker.setWordToGuess(wordToGuess);
        wordChecker.fillHashMapWithLetters(wordToGuess, wordChecker.getCharsWordToGuess());
    }

    public boolean start(final String word){
        boolean returnValue = false;
        levelState= LevelState.FIVE_LETTER_WORD;

        if(word.length() == getRightLengthByGameState()){
            wordChecker.getCharsWordToGuess().clear();
            gameState = GameState.PLAYING;
            guessesLeft = 5;
            score = 0;
            wordChecker.setWordToGuess(word);
            wordChecker.fillHashMapWithLetters(word, wordChecker.getCharsWordToGuess());

            returnValue = true;
        }

        return returnValue;
    }

    public boolean gameStarted(){
        return (!wordChecker.getCharsWordToGuess().isEmpty() && gameState == GameState.PLAYING);
    }

    public void roundController(final String wordGuess){
        if(wordChecker.wordGuessed(wordGuess)){
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
        if(gameState != GameState.WON){
            wordIsGuessed = false;
            wordChecker.getCharsWordToGuess().clear();
            guessesLeft = 5;
            wordChecker.setWordToGuess(newWord);
            wordChecker.fillHashMapWithLetters(newWord, wordChecker.getCharsWordToGuess());
        }
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

    public String getGameInfo(){
        JsonObject gameInfo = new JsonObject();

        if(gameState == GameState.WON || gameState == GameState.LOST){
            gameInfo.addProperty("score", score);
            gameInfo.addProperty("won", gameState == GameState.WON);
            gameInfo.addProperty("feedbackword", wordChecker.getFeedback());
        }else if(gameState == GameState.PLAYING){
            gameInfo.addProperty("guessesleft", guessesLeft);
            gameInfo.addProperty("score", score);
            gameInfo.addProperty("wordlength", getRightLengthByGameState());
            gameInfo.addProperty("feedbackword", wordChecker.getFeedback());
        }else if(gameState == null){
            gameInfo.addProperty("start", false);
            gameInfo.addProperty("guessesleft", guessesLeft);
            gameInfo.addProperty("score", score);
            gameInfo.addProperty("wordlength", getRightLengthByGameState());
        }

        return gameInfo.toString();
    }

    public String getGameInfoForSession(){
        JsonObject gameInfo = new JsonObject();

        gameInfo.addProperty("guessesLeft", guessesLeft);
        gameInfo.addProperty("score", score);
        gameInfo.addProperty("gameState", gameState.toString());
        gameInfo.addProperty("levelState", levelState.toString());
        gameInfo.addProperty("wordIsGuessed", wordIsGuessed);
        gameInfo.addProperty("wordToGuess", getGivenWord());

        return gameInfo.toString();
    }

    public static GameEngine turnInfoIntoEngine(String input){
        JsonObject gameInfo = JsonParser.parseString(input).getAsJsonObject();

        int guessesLeft = gameInfo.get("guessesLeft").getAsInt();
        int score = gameInfo.get("score").getAsInt();
        GameState gameState = GameState.valueOf(gameInfo.get("gameState").getAsString());
        LevelState levelState = LevelState.valueOf(gameInfo.get("levelState").getAsString());;
        boolean wordIsGuessed = gameInfo.get("wordIsGuessed").getAsBoolean();
        String wordToGuess = gameInfo.get("wordToGuess").getAsString();

        return new GameEngine(guessesLeft, score, gameState, levelState, wordIsGuessed, wordToGuess);
    }

    public String getGivenWord(){
        return wordChecker.getWordToGuess();
    }

    public int getScore(){
        return score;
    }

    public GameState getGameState(){
        return gameState;
    }

    public boolean isWordGuessed(){
        return wordIsGuessed;
    }

}
