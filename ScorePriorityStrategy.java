
import java.awt.Point;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class ScorePriorityStrategy implements Strategy{

    public Point nextHand(){

        Point p = null;
        List<Point> revTotalList;
        List<ScoreArea> revCanList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        long seed = c.get(Calendar.SECOND) * c.get(Calendar.MILLISECOND);
        Random r = new Random(seed);

        int count = 0;

        for(int y = 0; y < 8;y++){
            for(int x = 0 ; x < 8;x++){
                revTotalList = Othello.reversePiece(x, y);
                if(!revTotalList.isEmpty() && Othello.boardFlag[x][y] == 0){
                    revCanList.add(new ScoreArea(x,y,revTotalList.size()));
                    count++;
                }
                revTotalList.clear();
            }
        }

        int max = 0;
        ArrayList<ScoreArea> maxScore = new ArrayList<>();

        //最大得点を測定
        for(ScoreArea sa : revCanList){
            if(max < sa.getScore()){
                p = sa;
                max = sa.getScore();
            }
        }

        //最大得点を持つ候補エリアを抽出
        for(ScoreArea sa :revCanList){
            if(max == sa.getScore()){
                maxScore.add(sa);
            }
        }

        p = maxScore.get(r.nextInt(maxScore.size()));

        System.out.println(p.toString());

        return p;
    }

}