function startGame(){
    fetch('api/lingo/start')
        .then(response => response.json().then(data => {
            document.getElementById("guessesLeft").innerHTML = data.guessesleft;
            document.getElementById("score").innerHTML = data.score;
            document.getElementById("wordLength").innerHTML = data.wordlength;
            //document.getElementById("inputWord").maxLength = data.wordlength;

            document.getElementById("game").style.display = "block";
            document.getElementById("gameInfo").style.display = "block";

            console.log(data);
        }));
}

function sendWord(){
    var input = document.getElementById("inputWord").value;

    fetch('http://localhost:8080/api/lingo/guess/'+ input)
        .then(response => response.json().then(data => {
            document.getElementById("guessesLeft").innerHTML = data.guessesleft;
            document.getElementById("score").innerHTML = data.score;
            //document.getElementById("wordLength").setAttribute("maxlength", data.wordlength);
            document.getElementById("feedbackWord").innerHTML = data.feedbackword;
            console.log(data);
            
            if(data.won){
                showEnd(data);
            }

        }));
}

function showEnd(data){
    document.getElementById("end").style.display = "block";
    document.getElementById("game").style.display = "none";
    document.getElementById("gameInfo").style.display = "none";
    document.getElementById("totalScore").innerHTML = data.score;
    document.getElementById("feedbackWordEnd").innerHTML = data.feedbackword;
}

function saveScore(){
    var input = document.getElementById("playerName").value;

    fetch('http://localhost:8080/api/lingo/savescore/'+ input)
        .then(response => response.json().then(data => {
            console.log(data);
        }));
}