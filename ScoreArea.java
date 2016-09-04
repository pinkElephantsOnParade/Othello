
import java.awt.Point;

import java.util.ArrayList;

public class ScoreArea extends Point{

    private int score;
    private ArrayList<ScoreArea> relatedAreaList;
    private int evalScore;

    public ScoreArea(int x, int y, int score){
        this.score = score;
        this.setLocation(x, y);
        relatedAreaList = new ArrayList<>();
        this.evalScore = 0;
    }

    public void setScore(int score){
        this.score = score;
    }

    public int getScore(){
        return score;
    }

    public void setEvalScore(int eval){
        this.evalScore = eval;
    }

    public int getEvalScore(){
        return evalScore;
    }

    public void setRelatedArea(ScoreArea area){
        relatedAreaList.add(area);
    }

    public ArrayList<ScoreArea> getRelatedArea(){
        return relatedAreaList;
    }

    public String toString(){

        StringBuilder sb = new StringBuilder();
        sb.append("x = ");
        sb.append(this.getX());
        sb.append(", y = ");
        sb.append(this.getY());
        sb.append(", score = ");
        sb.append(this.score);
        sb.append(", eval = ");
        sb.append(evalScore);

        return  sb.toString();
    }
}