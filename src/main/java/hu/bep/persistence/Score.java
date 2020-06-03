package hu.bep.persistence;

import javax.persistence.*;

@Entity
@Table(name = "scoreboard")
public class Score {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    @Column(name = "score")
    private int score;

    @Column(name = "playername")
    private String playerName;

    public Score(long id, int score, String playerName){
        this.id = id;
        this.score = score;
        this.playerName = playerName;
    }

    public Score(String playername){
        this.playerName = playername;
    }

    public int getScore(){
        return score;
    }

    public String getPlayerName(){
        return playerName;
    }

    public long getId(){
        return id;
    }

    public void setScore(int score){
        this.score = score;
    }

    @Override
    public String toString(){
        return "{id:"+id+", score:"+score+", playername:"+playerName+"}";
    }
}
