
import java.awt.Point;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class RandomStrategy implements Strategy{

    public RandomStrategy(){

    }

    public Point nextHand(){

        Point p = null;
        List<Point> revTotalList;
        List<Point> revCanList = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        long seed = c.get(Calendar.SECOND) * c.get(Calendar.MILLISECOND);
        Random r = new Random(seed);

        int count = 0;

        for(int y = 0; y < 8;y++){
            for(int x = 0 ; x < 8;x++){
                revTotalList = Othello.reversePiece(x, y);
                if(!revTotalList.isEmpty() && Othello.boardFlag[x][y] == 0){
                    revCanList.add(new Point(x,y));
                    count++;
                }
                revTotalList.clear();
            }
        }

        System.out.println("");
        System.out.println("[候補エリア]");
        for(Point pt : revCanList){
            System.out.println("[" + pt.getX() + ","+ pt.getY() +"]");
        }

        if(!revCanList.isEmpty()){
            p = revCanList.get(r.nextInt(revCanList.size()));
        }

        return p;
    }
}