package hu.bep.persistence;

import javax.persistence.*;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    @Column(name = "totalscore")
    private int totalScore;

    @Column(name = "playername")
    private String playerName;

    public Player(){

    }

    public Player(final String playername, final int totalscore){
        this.totalScore = totalscore;
        this.playerName = playername;
    }

    public int getTotalScore(){
        return totalScore;
    }

    public String getPlayerName(){
        return playerName;
    }

    public long getId(){
        return id;
    }

    public void setScore(final int score){
        this.totalScore = score;
    }

    @Override
    public String toString(){
        return "total score:"+totalScore+", playername:"+playerName+"}";
    }
}
