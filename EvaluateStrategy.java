
import java.awt.Point;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class EvaluateStrategy implements Strategy{


    static Integer[][] evalBoard = {
        {120, -20, 20,  5,  5, 20, -20, 120},
        {-20, -40, -5, -5, -5, -5, -40, -20},
        {20,   -5, 15,  3,  3, 15,  -5,  20},
        {5,    -5,  3,  3,  3,  3,  -5,   5},
        {5,    -5,  3,  3,  3,  3,  -5,   5},
        {20,   -5, 15,  3,  3, 15,  -5,  20},
        {-20, -40, -5, -5, -5, -5, -40, -20},
        {120, -20, 20,  5,  5, 20, -20, 120}
    };

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
                    revCanList.add(new ScoreArea(x,y, evalBoard[x][y]));
                    count++;
                }
                revTotalList.clear();
            }
        }

        int max = -200;
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

        System.out.println("***[Evaluate]:" + p.toString());
        return p;
    }

}