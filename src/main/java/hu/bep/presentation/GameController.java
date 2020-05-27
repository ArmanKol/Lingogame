package hu.bep.presentation;

import hu.bep.persistence.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class GameController{

    @Autowired
    private WordRepository wordRepository;


    @GetMapping("/lingo/randomword")
    public String doGet(HttpServletRequest request, HttpServletResponse response){
        return "Hallo";
    }
}
