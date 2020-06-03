function startGame(){
    fetch('http://localhost:8080/api/lingo/start')
        .then(response => response.json().then(data => {
            document.getElementById("guessesLeft").innerHTML = data.guessesleft;
            document.getElementById("score").innerHTML = data.score;
            document.getElementById("wordLength").innerHTML = data.wordlength;
            document.getElementById("inputWord").maxLength = data.wordlength;
            console.log(data);
        }));
}

function sendWord(){
    var input = document.getElementById("inputWord").value;

    fetch('http://localhost:8080/api/lingo/guess/'+ input)
        .then(response => response.json().then(data => {
            document.getElementById("guessesLeft").innerHTML = data.guessesleft;
            document.getElementById("score").innerHTML = data.score;
            document.getElementById("wordLength").setAttribute("maxlength", data.wordlength);
            document.getElementById("feedbackWord").innerHTML = data.feedbackword;
            console.log(data);
        }));
}