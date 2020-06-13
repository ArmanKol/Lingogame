package hu.bep.logic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Random word generator test")
class RandomWordGeneratorTest {

    @Test
    @DisplayName("De terug gegeven int mag niet hoger zijn dan de lengte")
    void randomNumber_CantGreaterThanInput_ReturnFalse(){
        RandomWordGenerator generator = new RandomWordGenerator(100);

        for(int x=0; x < generator.getLength(); x++){
            int randomInt = generator.getRandomNumber();
            assertFalse(randomInt > generator.getLength());
        }
    }
}
