
import java.awt.Point;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class TwoTreeStrategy implements Strategy{

    Integer[][] futureBoard = {
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,1,-1,0,0,0},
        {0,0,0,-1,1,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0}
    };
    List<Point> revTotalList;
    
    public TwoTreeStrategy(){

    }

    public Point nextHand(){

        Point p = null;
        List<ScoreArea> revCanList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        long seed = c.get(Calendar.SECOND) * c.get(Calendar.MILLISECOND);
        Random r = new Random(seed);

        ScoreArea roots = new ScoreArea(-1,-1,-1);

        for(int y = 0; y < 8;y++){
            for(int x = 0 ; x < 8;x++){
                //候補エリアを抽出
                revTotalList = Othello.reversePiece(x, y);
                if(!revTotalList.isEmpty() && Othello.boardFlag[x][y] == 0){
                    roots.setRelatedArea(new ScoreArea(x,y,EvaluateStrategy.evalBoard[x][y]));
                }
                revTotalList.clear();
            }
        }
        
        //次の一手
        for(ScoreArea sa : roots.getRelatedArea()){
            //自分の1手を推定
            estimateReverse(sa);
            //相手の一手を推定
            addScoreAreaLeaf(sa, Othello.active * -1);
        }

        //次の次の一手
        for(ScoreArea sa : roots.getRelatedArea()){
            estimateReverse(sa);
            for(ScoreArea osa : sa.getRelatedArea()){
                estimateReverse(osa);
                addScoreAreaLeaf(osa, Othello.active);
            }
        }

        //min-max探索法を実装
        int eval = 0;
        for(ScoreArea sa : roots.getRelatedArea()){
            for(ScoreArea osa : sa.getRelatedArea()){
                //評価値を挿入
                for(ScoreArea rsa : osa.getRelatedArea()){
                    rsa.setEvalScore(sa.getScore() + osa.getScore() + rsa.getScore());
                    System.out.println("********[ME]" + rsa.toString());
                }
                int max = -999;
                //親ノードに評価値を挿入
                for(ScoreArea rsa : osa.getRelatedArea()){
                    eval = rsa.getEvalScore();
                    if(max < eval){
                        max = eval;
                    }
                }
                osa.setEvalScore(max);
                System.out.println("*****[Opp]:" + osa.toString());
            }

            int min = 999;
            for(ScoreArea osa : sa.getRelatedArea()){
                eval = osa.getEvalScore();
                if(eval < min){
                    min = eval;
                }
            }
            sa.setEvalScore(min);
            System.out.println("[ME]:" + sa.toString());
        }

        //木探索全体の評価値結果を測定
        int result = -999;
        for(ScoreArea sa : roots.getRelatedArea()){
            if( result < sa.getEvalScore()){
                result = sa.getEvalScore();
            }
        }

        ArrayList<ScoreArea> resultScore = new ArrayList<>();
        for(ScoreArea sa : roots.getRelatedArea()){
            if(result == sa.getEvalScore()){
                resultScore.add(sa);
            }
        }

        if(5 < Othello.restCount()){
            p = resultScore.get(r.nextInt(resultScore.size()));
            System.out.println("***[[Result]]:" + p.toString());
        }else{
            //残り5マユ以下の場合
            p = roots.getRelatedArea().get(
                            r.nextInt(roots.getRelatedArea().size()));
        }

        return p;
    }

    private void cloneBoard(){
        for(int y = 0; y < 8;y++){
            for(int x = 0; x < 8;x++){
                futureBoard[x][y] = Othello.boardFlag[x][y]; 
            }
        }
    }

    private void estimateReverse(ScoreArea sa){
        cloneBoard();
        futureBoard[(int)sa.getX()][(int)sa.getY()] = Othello.active;
        revTotalList = Othello.reversePiece(
            (int)sa.getX(), 
            (int)sa.getY(), 
            futureBoard, 
            Othello.active);
        for(Point pt : revTotalList){
            futureBoard[(int)pt.getX()][(int)pt.getY()] = Othello.active;
        }
        revTotalList.clear();
    }

    private void addScoreAreaLeaf(ScoreArea sa, int active){
        for(int y = 0; y < 8;y++){
            for(int x = 0 ; x < 8;x++){
                revTotalList = Othello.reversePiece(x, y, futureBoard, active);
                if(!revTotalList.isEmpty() && Othello.boardFlag[x][y] == 0){
                    sa.setRelatedArea(new ScoreArea(x,y,EvaluateStrategy.evalBoard[x][y] * active));
                }
                revTotalList.clear();
            }
        }
    }

    private void printBoard(){
        for(int y = 0; y < 8;y++){
            for(int x = 0; x < 8;x++){
                System.out.print(futureBoard[x][y] + " ");
            }
            System.out.println("");
        }
    }

}