package hu.bep.logic;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Random word generator test")
public class RandomWordGeneratorTest {

    @Test
    @DisplayName("De terug gegeven int mag niet hoger dan de lengte")
    void numberCannotBeHigherThanLength(){
        RandomWordGenerator generator = new RandomWordGenerator(100);

        for(int x=0; x < generator.getLength(); x++){
            int randomInt = generator.getRandomNumber();
            assertFalse(randomInt > generator.getLength());
        }
    }
}
