function getScoreboard(){
    fetch('api/lingo/scoreboard')
        .then(response => response.json().then(responseData => {
            var place = 1;
            for(const data of responseData){
				var tBody = document.createElement("tbody");

				var row = document.createElement("tr");
                
                var columnRankings = document.createElement("td");
                var rankingText = document.createTextNode(place);
                columnRankings.appendChild(rankingText);
                row.appendChild(columnRankings);

				var columnPlayerName = document.createElement("td");
				var playerNameText = document.createTextNode(data.playerName);
				columnPlayerName.appendChild(playerNameText);
				row.appendChild(columnPlayerName);

				var columnScore = document.createElement("td");
				var scoreText = document.createTextNode(data.totalScore);
				columnScore.appendChild(scoreText);
				row.appendChild(columnScore);

				tBody.appendChild(row);
                document.querySelector("#scoreboardTable").appendChild(tBody);
                place++;
			}
        }));
}

getScoreboard();