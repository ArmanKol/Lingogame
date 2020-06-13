function startGame(){
    document.getElementById("startButton").style.display = "none";
    document.getElementById("end").style.display = "none";
    document.getElementById("gameInfo").style.display = "none";

    fetch('api/lingo/start')
        .then(response => response.json().then(data => {
            document.getElementById("guessesLeft").innerHTML = data.guessesleft;
            document.getElementById("score").innerHTML = data.score;
            document.getElementById("wordLength").innerHTML = data.wordlength;
            document.getElementById("inputWord").maxLength = data.wordlength;

            document.getElementById("game").style.display = "block";
            document.getElementById("gameInfo").style.display = "block";
        }));
}

function sendWord(){
    var input = document.getElementById("inputWord").value;

    fetch('api/lingo/guess/', {method: 'POST', body: input})
        .then(response => response.json().then(data => {
            document.getElementById("guessesLeft").innerHTML = data.guessesleft;
            document.getElementById("score").innerHTML = data.score;
            document.getElementById("wordLength").innerHTML = data.wordlength;
            document.getElementById("inputWord").maxLength = data.wordlength;
            document.getElementById("feedbackWord").innerHTML = data.feedbackword;
            
            console.log(data);

            if(data.won !== undefined){
                showEnd(data);
            }

        }));
}

function showEnd(data){
    if(data.won){
        document.getElementById("won").innerHTML += " gewonnen!";
    }else{
        document.getElementById("won").innerHTML += " verloren!";
    }

    document.getElementById("end").style.display = "block";
    document.getElementById("game").style.display = "none";
    document.getElementById("gameInfo").style.display = "none";
    document.getElementById("totalScore").innerHTML = data.score;
    document.getElementById("feedbackWordEnd").innerHTML = data.feedbackword;
}

function saveScore(){
    var input = document.getElementById("playerName").value;

    fetch('api/lingo/savescore', {method: 'POST', body: input})
        .then(response => response.json().then(data => {
            if(data.saved){
                document.getElementById("startButton").style.display = "inline-block";
            }
        }));
}