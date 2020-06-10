package hu.bep.logic;

import java.util.Random;

public class RandomWordGenerator {

    private int length;
    private Random random = new Random();

    public RandomWordGenerator(final int length){
        this.length = length;
    }

    public int getRandomNumber(){
        int randomInt;
        int min = 0;

        randomInt = random.nextInt(length - min + 1) + min;

        return randomInt;
    }

    public int getLength(){
        return length;
    }

}
