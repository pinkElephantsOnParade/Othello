import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.List;

class Othello extends Frame implements Runnable{

    Button btn;
    Canvas canvas;
    Panel panel;
    Point handPoint;
    Graphics panelGraphics;
    /* none : 0
       white(first) : 1
       black(second) : -1
    */
    Integer[][] boardFlag = {{0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0},
                            {0,0,0,1,-1,0,0,0},
                            {0,0,0,-1,1,0,0,0},
                            {0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0},
                            {0,0,0,0,0,0,0,0}};

    Player firstPlayer;
    Player secondPlayer;
    
    /* 1 : first -1 : second */
    Integer active = 1;
    List<Point> revAllList;
    boolean onTheGame = true;

    Color boardGREEN;
    Color operateYELLOW;

    Label titleLabel;
    Label whiteCountLabel;
    Label blackCountLabel;
    Label playerTurnLabel;
    Font  countLabelFont;
    Font  turnLabelFont;

    public Othello(){
        boardGREEN = new Color(0,222,0);
        operateYELLOW = new Color(200,200,0);
        firstPlayer = new Player(2);
        secondPlayer = new Player(2);
        initLayout();
    }

	public static void main (String[] args){
        Othello game = new Othello();
        new Thread(game).start();
	}

    public void run(){

        try{
            Thread.sleep(500);
            //init chip
            panelGraphics = panel.getGraphics();
            panelGraphics.drawOval(315, 225, 40, 40);
            panelGraphics.fillOval(365, 225, 40, 40);
            panelGraphics.fillOval(315, 275, 40, 40);
            panelGraphics.drawOval(365, 275, 40, 40);
        }catch(InterruptedException e){
            System.out.println(e.toString());
        }

    }

    private void initLayout(){
        btn = new  Button("Submit");

        panel = new Panel(){
            public void paint(Graphics g){
                panelGraphics = g;
                initBoard(panelGraphics);
            }
        };

        handPoint = new Point(0,0);

        setBackground(operateYELLOW);
        setBounds(10,10,720,720);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setVisible(true);

        panel.setBackground(boardGREEN);
        panel.setBounds(0,10,720,540);

        btn.setBackground(new Color(128,0,128));
        btn.setBounds(320,550,80,30);

        titleLabel = new Label("Othello");
        titleLabel.setFont(new  Font(Font.SANS_SERIF, Font.BOLD, 32));
        titleLabel.setBounds(300, 10, 200, 60);

        countLabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 24);
        turnLabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 28);

        whiteCountLabel = new Label();
        whiteCountLabel.setFont(countLabelFont);
        whiteCountLabel.setBounds(60, 255, 100, 30);
        whiteCountLabel.setText("白:" + firstPlayer.getScore());

        blackCountLabel = new Label();
        blackCountLabel.setFont(countLabelFont);
        blackCountLabel.setBounds(620, 255, 100, 30);
        blackCountLabel.setText("黒:" + secondPlayer.getScore());

        playerTurnLabel = new Label("白のターン");
        playerTurnLabel.setFont(turnLabelFont);
        playerTurnLabel.setBounds(300, 480, 200, 60);

        panel.add(whiteCountLabel);
        panel.add(blackCountLabel);
        panel.add(playerTurnLabel);
        panel.add(titleLabel);
        add(panel);
        add(btn);

        frameListener();
        panelListener();
        buttonListener();
    }

    private void initBoard(Graphics g){
        //row
        g.drawLine(160, 70, 560, 70);
        g.drawLine(160, 120, 560, 120);
        g.drawLine(160, 170, 560, 170);
        g.drawLine(160, 220, 560, 220);
        g.drawLine(160, 270, 560, 270);
        g.drawLine(160, 320, 560, 320);
        g.drawLine(160, 370, 560, 370);
        g.drawLine(160, 420, 560, 420);
        g.drawLine(160, 470, 560, 470);

        //col
        g.drawLine(160, 70, 160, 470);
        g.drawLine(210, 70, 210, 470);
        g.drawLine(260, 70, 260, 470);
        g.drawLine(310, 70, 310, 470);
        g.drawLine(360, 70, 360, 470);
        g.drawLine(410, 70, 410, 470);
        g.drawLine(460, 70, 460, 470);
        g.drawLine(510, 70, 510, 470);
        g.drawLine(560, 70, 560, 470);

        g.drawOval(20, 255, 30, 30);
        g.fillOval(580, 255, 30, 30);
    }

    private void frameListener(){
        this.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                System.out.println(e.paramString());
                if(e.getKeyChar() == 'q'){
                    closed();
                }
            }
        });

        this.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                System.out.println(e.getPoint().toString());
            }            
        });

        this.addWindowListener(new WindowAdapter(){
            public void windowOpened(WindowEvent e) {
                System.out.println(e.paramString());
            }
            public void windowClosing(WindowEvent e){
                System.out.println(e.paramString());
                closed();
            }
        });
    }

    private void panelListener(){
        panel.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){

                String turnText = "";
                handPoint = calcHandPoint(e.getPoint(), active);

                if(-1 < handPoint.getX() && -1 < handPoint.getY() && onTheGame){
                    if(active == 1){
                        //white
                        reverseWhite((int)handPoint.getX(), (int)handPoint.getY());
                        turnText = "黒のターン";
                    }else{
                        //black
                        reverseBlack((int)handPoint.getX(), (int)handPoint.getY());
                        turnText = "白のターン";
                    }

                    firstPlayer.setScore(whiteCount());
                    secondPlayer.setScore(blackCount());

                    whiteCountLabel.setText("白" + firstPlayer.getScore());  
                    blackCountLabel.setText("黒" + secondPlayer.getScore());

                    if(restCount() == 0){
                        onTheGame = false;
                        if(secondPlayer.getScore() < firstPlayer.getScore() ){
                            turnText = "白の勝ち";
                        }else if(firstPlayer.getScore() < secondPlayer.getScore()){
                            turnText = "黒の勝ち";
                        }else{
                            turnText = "引き分け";
                        }
                    }

                    playerTurnLabel.setText(turnText);

                    revAllList.clear();
                    active *= -1;       //change player
                }
                
            }
        });
    }

    private void buttonListener(){
        btn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println(e.paramString());
                closed();
            }            
        });        
    }

    private Point calcHandPoint(Point p, Integer active){
        Integer iX;
        Integer iY;
        Point optPoint = new Point(-1, -1);

        iX = (int)((p.getX() - 160.0) / 50);
        iY = (int)((p.getY() - 70.0) / 50);

        if( (-1 < iX && iX < 8) && (-1 < iY && iY < 8)){
            reversePiece(iX, iY, active);
            if(0 < revAllList.size()){
                optPoint.setLocation(
                    165 + iX * 50,
                    75 + iY * 50);
            }
        }
        return optPoint;
    }

    private void reversePiece(Integer x, Integer y, Integer active){

        List<Point> revLineList;
        int opposite;

        revAllList = new ArrayList<>();
        revLineList = new ArrayList<>();

        // white
        if(active == 1 ){
            opposite = -1;   // black
        } 
        // black
        else{
            opposite = 1;   // white
        }
        
        System.out.println("x = " + x + ",y = " + y + ",op = " + opposite);

        //left - top
        if(0 < x && 0 < y){            
            for(int i = 1 ; 0 < x - i && 0 < y - i ; i++){
                if(opposite == boardFlag[x - i][y - i]){       
                    revLineList.add(new Point(x - i, y - i));
                } else if ( i != 1 && (opposite * -1) == boardFlag[x - i][y - i]){
                    for(Point p :revLineList){
                        System.out.println("[left - top]" + p);
                    }
                    revAllList.addAll(revLineList);
                    break;
                } else {
                    break;
                }
            }
        }
        revLineList.clear();

        //center - top
        if(0 < y){            
            for(int i = 1 ; 0 < y - i ; i++){
                if(opposite == boardFlag[x][y - i]){       
                    revLineList.add(new Point(x, y - i));
                } else if ( i != 1 && (opposite * -1) == boardFlag[x][y - i]){
                    for(Point p :revLineList){
                        System.out.println("[center - top]" + p);
                    }
                    revAllList.addAll(revLineList);
                    break;
                } else {
                    break;
                }
            }
        }
        revLineList.clear();

        //right - top
        if(x < 8 && 0 < y){            
            for(int i = 1 ; x + i < 8 && 0 < y - i ; i++){
                if(opposite == boardFlag[x + i][y - i]){       
                    revLineList.add(new Point(x + i, y - i));
                } else if ( i != 1 && (opposite * -1) == boardFlag[x + i][y - i]){
                    for(Point p :revLineList){
                        System.out.println("[right - top]" + p);
                    }
                    revAllList.addAll(revLineList);
                    break;
                } else {
                    break;
                }
            }
        }
        revLineList.clear();

        //left
        if( 0 < x ){            
            for(int i = 1 ; 0 < x - i ; i++){
                if(opposite == boardFlag[x - i][y]){       
                    revLineList.add(new Point(x - i, y));
                } else if ( i != 1 && (opposite * -1) == boardFlag[x - i][y]){
                    for(Point p :revLineList){
                        System.out.println("[left]" + p);
                    }
                    revAllList.addAll(revLineList);
                    break;
                } else {
                    break;
                }
            }
        }
        revLineList.clear();

        //right
        if( x < 8){            
            for(int i = 1 ; x + i < 8; i++){
                if(opposite == boardFlag[x + i][y]){       
                    revLineList.add(new Point(x + i, y));
                } else if ( i != 1 && (opposite * -1) == boardFlag[x + i][y]){
                    for(Point p :revLineList){
                        System.out.println("[right]" + p);
                    }
                    revAllList.addAll(revLineList);
                    break;
                } else {
                    break;
                }
            }
        }
        revLineList.clear();

        //left - bottom
        if(0 < x && y < 8){            
            for(int i = 1 ; 0 < x - i && y + i < 8; i++){
                if(opposite == boardFlag[x - i][y + i]){       
                    revLineList.add(new Point(x - i, y + i));
                } else if ( i != 1 && (opposite * -1) == boardFlag[x - i][y + i]){
                    for(Point p :revLineList){
                        System.out.println("[left - bottom]" + p);
                    }
                    revAllList.addAll(revLineList);
                    break;
                } else {
                    break;
                }
            }
        }
        revLineList.clear();

        //center - bottom
        if(y < 8){            
            for(int i = 1 ; y + i < 8; i++){
                if(opposite == boardFlag[x][y + i]){       
                    revLineList.add(new Point(x, y + i));
                } else if ( i != 1 && (opposite * -1) == boardFlag[x][y + i]){
                    for(Point p :revLineList){
                        System.out.println("[center - bottom]" + p);
                    }
                    revAllList.addAll(revLineList);
                    break;
                } else {
                    break;
                }
            }
        }
        revLineList.clear();

        //right - bottom
        if(x < 8 && y < 8){            
            for(int i = 1 ; x + i < 8 && y + i < 8; i++){
                if(opposite == boardFlag[x + i][y + i]){       
                    revLineList.add(new Point(x + i, y + i));
                } else if ( i != 1 && (opposite * -1) == boardFlag[x + i][y + i]){
                    for(Point p :revLineList){
                        System.out.println("[right - bottom]" + p);
                    }
                    revAllList.addAll(revLineList);
                    break;
                } else {
                    break;
                }
            }
        }
        revLineList.clear();

        for(Point p :revAllList){
            System.out.println("[All]" + p);
        }

    }

    public void reverseWhite(int initX, int initY){
        Point g = pointToGrid(initX, initY);
        boardFlag[(int)g.getX()][(int)g.getY()] = 1;

        panelGraphics = panel.getGraphics();
        panelGraphics.drawOval(initX, initY, 40, 40);
        for(Point p : revAllList){
            boardFlag[(int)p.getX()][(int)p.getY()] = 1;
            panelGraphics.setColor(boardGREEN);
            panelGraphics.fillRect((int)(p.getX() * 50) + 161,
                                    (int)(p.getY() * 50) + 71, 49, 49);
            panelGraphics.setColor(Color.black);
            panelGraphics.drawOval((int)p.getX() * 50 + 165,
                                    (int)p.getY() * 50 + 75, 40, 40);
                                    
        }
        panel.paint(panelGraphics);
        printBoard();
    }

    public void reverseBlack(int initX, int initY){
        Point g = pointToGrid(initX, initY);
        boardFlag[(int)g.getX()][(int)g.getY()] = -1;

        panelGraphics = panel.getGraphics();
        panelGraphics.fillOval(initX, initY, 40, 40);

        for(Point p : revAllList){
            boardFlag[(int)p.getX()][(int)p.getY()] = -1;
            panelGraphics.setColor(boardGREEN);
            panelGraphics.fillRect((int)p.getX() * 50 + 161,
                                    (int)p.getY() * 50 + 71, 49, 49);
            panelGraphics.setColor(Color.black);
            panelGraphics.fillOval((int)p.getX() * 50 + 165,
                                    (int)p.getY() * 50 + 75, 40, 40);
        }
        panel.paint(panelGraphics);
        printBoard();
    }

    private void printBoard(){
        for(int y = 0; y < 8;y++){
            for(int x = 0; x < 8;x++){
                System.out.print(boardFlag[x][y] + " ");
            }
            System.out.println("");
        }
    }

    private int whiteCount(){
        int count = 0;
        for(int y = 0; y < 8;y++){
            for(int x = 0; x < 8;x++){
                if(boardFlag[x][y] == 1){
                    count++;
                }
            }
        }
        return count;
    }

    private int blackCount(){
        int count = 0;
        for(int y = 0; y < 8;y++){
            for(int x = 0; x < 8;x++){
                if(boardFlag[x][y] == -1){
                    count++;
                }
            }
        }
        return count;
    }

    private int restCount(){
        int count = 0;
        for(int y = 0; y < 8;y++){
            for(int x = 0; x < 8;x++){
                if(boardFlag[x][y] == 0){
                    count++;
                }
            }
        }        
        return count;
    }

    private Point pointToGrid(Point p){
        Point grid = new Point(
            (int)((p.getX() - 160.0) / 50),
            (int)((p.getY() - 70.0) / 50));
        return grid;
    }

    private Point pointToGrid(int x, int y){
        Point grid = new Point(
            (x - 160) / 50,
            (y - 70) / 50
            );
        return grid;
    }

    private Point gridToPoint(Point g){
        Point pt = new Point(
            (int)(g.getX()) * 50 + 160,
            (int)(g.getY()) * 50 + 70
            );
        return pt;
    }

    private Point gridToPoint(int x, int y){
        Point pt = new Point(
            x * 50 + 160,
            y * 50 + 70
            );
        return pt;
    }

    private void closed(){
        this.dispose();
    }

}
