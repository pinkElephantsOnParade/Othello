
import java.awt.Point;

public class ScoreArea extends Point{

    private int score;

    public ScoreArea(int x, int y, int score){
        this.score = score;
        this.setLocation(x, y);
    }

    public void setScore(int score){
        this.score = score;
    }

    public int getScore(){
        return score;
    }

    public String toString(){
        return "x = " + this.getX() + ", y = " + this.getY() + ", score = " + this.score;
    }
}