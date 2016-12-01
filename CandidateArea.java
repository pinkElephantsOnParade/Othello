
import java.awt.Graphics;
import java.awt.Point;

import java.awt.ArrayList;
import java.awt.List;

class CandidateArea implements Runnable{

    Graphics g;
    List<Point> pList;
    Integer[][] bFlag;
    Integer active;

    public CandidateArea(Graphics g, List<Point> pList, Integer[][] bFlag, Integer active){
        this.g = g;
        this.pList = pList;
        this.bFlag = bFlag;
        this.active = active;
    }

    public void run(){
        Thread.sleep(500);
        calc();
    }

    private void calc(){
        System.out.println("candidate ...");
    }
}