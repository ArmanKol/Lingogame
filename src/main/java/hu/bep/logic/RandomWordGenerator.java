package hu.bep.logic;

import java.util.Random;

public class RandomWordGenerator {

    private int min, max;

    public RandomWordGenerator(int min, int max){
        this.min = min;
        this.max = max;
    }


    public int getRandomNumber(){
        int randomInt;
        Random random = new Random();

        randomInt = random.nextInt(max - min + 1) + min;

        if(!(randomInt >= min && randomInt <= max)){
            randomInt = random.nextInt(max - min + 1) + min;
        }

        return randomInt;
    }

}
