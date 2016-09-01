
import java.awt.Point;
import java.util.Objects;

class Player{

    private Integer score;
    private Strategy strategy;

    public Player(){
        score = 0;
        strategy = null;
    }

    public Player(Integer score, Strategy strategy){
        this.score = score;
        this.strategy = strategy;
    }

    public Point nextHand(){
        return Objects.nonNull(strategy) ? strategy.nextHand() : null;
    }

    public void setScore(Integer score){
        this.score = score;
    }

    public Integer getScore(){
        return score;
    }

    public void setStrategy(Strategy strategy){
        this.strategy = strategy;
    }

    public Strategy getStrategy(){
        return strategy;
    }

}