package hu.bep.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WordChecker {
    private static final Logger LOGGER = LogManager.getLogger(WordChecker.class);

    private String wordToGuess;
    private String feedback;

    private final Map<Integer, Character> charsWordToGuess = new HashMap<>();
    private final Map<Integer, Character> charsInputWord = new HashMap<>();

    public WordChecker(){

    }

    protected boolean wordGuessed(final String inputWord){
        feedback = checkWord(inputWord).get(0).toString();

        return inputWord.equals(wordToGuess);
    }

    private Map<Integer, StringBuilder> checkWord(String inputWord){
        fillHashMapWithLetters(inputWord, charsInputWord);
        Map<Integer, StringBuilder> returnValues = new HashMap<>();

        Map<Integer, Character> hasCharAtDiffIndex = new HashMap<>();
        Map<Integer, String> finalFeedback = new HashMap<>();

        checkIfCharsCorrect(finalFeedback, hasCharAtDiffIndex);
        replacePresentChars(finalFeedback, hasCharAtDiffIndex);

        StringBuilder feedBackWord = createFeedbackString(finalFeedback);

        returnValues.put(0, feedBackWord);

        charsInputWord.clear();
        return returnValues;
    }

    private void checkIfCharsCorrect(Map<Integer, String> finalFeedback, Map<Integer, Character> absentChars){
        for(int i =0; i < wordToGuess.length(); i++){
            if(charsInputWord.get(i) == null || charsWordToGuess.get(i) == null){
                LOGGER.warn("charsInputWord or charsWordToGuess had null. This is intended");
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

    private void replacePresentChars(Map<Integer, String> finalFeedback, Map<Integer, Character> absentChars){
        for(int index: absentChars.keySet()){
            char getCharFromIndex = absentChars.get(index);

            int countCharInput = Collections.frequency(charsInputWord.values(), getCharFromIndex);
            int countCharWordToGuess = Collections.frequency(charsWordToGuess.values(), getCharFromIndex);

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

    protected void fillHashMapWithLetters(String inputWord, Map<Integer, Character> map){
        for(int i =0; i < inputWord.length(); i++){
            map.put(i, inputWord.toCharArray()[i]);
        }
    }

    protected void setWordToGuess(String input){
        this.wordToGuess = input;
    }

    protected Map<Integer, Character> getCharsWordToGuess(){
        return charsWordToGuess;
    }

    protected String getFeedback(){
        return feedback;
    }

    protected String getWordToGuess(){
        return wordToGuess;
    }

}
