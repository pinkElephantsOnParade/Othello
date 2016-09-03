
import java.awt.Point;

import java.util.ArrayList;

public class ScoreArea extends Point{

    private int score;
    private ArrayList<ScoreArea> relatedAreaList;

    public ScoreArea(int x, int y, int score){
        this.score = score;
        this.setLocation(x, y);
        relatedAreaList = new ArrayList<>();
    }

    public void setScore(int score){
        this.score = score;
    }

    public int getScore(){
        return score;
    }

    public void setRelatedArea(ScoreArea area){
        relatedAreaList.add(area);
    }

    public ArrayList<ScoreArea> getRelatedArea(){
        return relatedAreaList;
    }

    public String toString(){
        return "x = " + this.getX() + ", y = " + this.getY() + ", score = " + this.score;
    }
}