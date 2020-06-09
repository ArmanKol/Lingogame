package hu.bep.logic;

import java.util.Random;

public class RandomWordGenerator {

    private int length;

    public RandomWordGenerator(final int length){
        this.length = length;
    }

    public int getRandomNumber(){
        int randomInt;
        Random random = new Random();
        int min = 0;

        randomInt = random.nextInt(length - min + 1) + min;

        return randomInt;
    }

    public int getLength(){
        return length;
    }

}
