package hu.bep.logic;

import java.util.Random;

public class RandomWordGenerator {

    private int length;

    public RandomWordGenerator(int length){
        this.length = length;
    }

    public int getRandomNumber(){
        int randomInt;
        Random random = new Random();
        int min = 0;

        randomInt = random.nextInt(length - min + 1) + min;

        if(!(randomInt >= min && randomInt <= length)){
            randomInt = random.nextInt(length - min + 1) + min;
        }

        return randomInt;
    }

}
