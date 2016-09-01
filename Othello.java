import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Othello extends Frame implements ItemListener, Runnable{

    Button startButton;
    Button exitButton;
    Canvas canvas;
    Panel panel;
    Point handPoint;
    Graphics panelGraphics;
    /* none : 0
       white(first) : 1
       black(second) : -1
    */
    static Integer[][] boardFlag = {{0,0,0,0,0,0,0,0},
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
    static Integer active = 1;
    List<Point> revAllList;
    boolean onTheGame = false;
    boolean initPiece = true;

    Color boardGREEN;
    Color operateYELLOW;
    Color candidateBlue;
    Color playerSilver;

    Label titleLabel;
    Label whiteCountLabel;
    Label blackCountLabel;
    Label playerTurnLabel;
    Font  countLabelFont;
    Font  turnLabelFont;

    Panel whiteOperatePanel;
    Panel blackOperatePanel;

    CheckboxGroup whitePlayerType;
    CheckboxGroup blackPlayerType;

    Checkbox whiteHuman;
    Checkbox whiteRandom;
    Checkbox whiteTwoTree;
    Checkbox whiteThreeTree;

    Checkbox blackHuman;
    Checkbox blackRandom;
    Checkbox blackTwoTree;
    Checkbox blackThreeTree;

    static Othello game;
    static Thread cpuThread;

    public Othello(){
        boardGREEN = new Color(0,222,0);
        operateYELLOW = new Color(200,200,0);
        candidateBlue = new Color(115, 184, 226);
        playerSilver = new  Color(192, 192, 192);
        firstPlayer = new Player(2, null);
        secondPlayer = new Player(2, new RandomStrategy());
        revAllList = new ArrayList<>();
        initLayout();
    }

	public static void main (String[] args){
        game = new Othello();
        cpuThread = new Thread(game);
        cpuThread.start();
	}

    public void run(){

        int count = 0;
        Point nextHand;
        try{
            //駒の初期配置
            if(initPiece){
                Thread.sleep(500);
                panelGraphics = panel.getGraphics();
                panelGraphics.drawOval(315, 225, 40, 40);
                panelGraphics.fillOval(365, 225, 40, 40);
                panelGraphics.fillOval(315, 275, 40, 40);
                panelGraphics.drawOval(365, 275, 40, 40);
                initPiece = false;
                putCandidateArea(revAllList);
            }
            
            while(onTheGame){
                Thread.sleep(500);
                if(active == 1 && Objects.nonNull(firstPlayer.getStrategy())){
                    nextHand = firstPlayer.nextHand();
                    revAllList = reversePiece((int)nextHand.getX(), (int)nextHand.getY());
                    reverse(gridToPoint(nextHand));
                    //putCandidateArea(revAllList);
                    //revAllList.clear();
                } else if (active == -1 && Objects.nonNull(secondPlayer.getStrategy())) {
                    System.out.println(secondPlayer.nextHand());
                    nextHand = secondPlayer.nextHand();
                    revAllList = reversePiece((int)nextHand.getX(), (int)nextHand.getY());
                    reverse(gridToPoint(nextHand));
                    //putCandidateArea(revAllList);
                    //revAllList.clear();
                }
                
                System.out.println( (++count / 2) + "[sec]");
            }
        }catch(InterruptedException e){
            System.out.println(e.toString());
        }

    }

    public void itemStateChanged(ItemEvent ie){
        System.out.println(ie.getItem() + "is selected.");
        System.out.println(ie.paramString());
    }

    private void initLayout(){
        startButton = new Button("Start");
        exitButton = new Button("Exit");

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

        startButton.setBounds(600,600,100,30);
        exitButton.setBounds(600,640,100,30);

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

        Label whiteOperateLabel = new Label("白 - 先攻");
        whiteOperateLabel.setBounds(10, 120, 80, 18);

        whiteOperatePanel = new Panel();
        whiteOperatePanel.setBackground(playerSilver);
        whiteOperatePanel.setBounds(30, 560, 170, 150);

        whitePlayerType = new CheckboxGroup();
        whiteHuman = new Checkbox("1:人間", whitePlayerType, true);
        whiteHuman.setBounds(10,10,160,18);
        whiteHuman.addItemListener(this);
        whiteRandom = new Checkbox("2:CPU(ランダム)", whitePlayerType, false);
        whiteRandom.setBounds(10,30,160,18);
        whiteRandom.addItemListener(this);
        whiteTwoTree = new Checkbox("3:CPU(木探索-深さ2)", whitePlayerType, false);
        whiteTwoTree.setBounds(10,50,160,18);
        whiteTwoTree.addItemListener(this);
        whiteThreeTree = new Checkbox("4:CPU(木探索-深さ3)", whitePlayerType, false);
        whiteThreeTree.setBounds(10,70,160,18);
        whiteThreeTree.addItemListener(this);

        Label blackOperateLabel = new Label("黒 - 後攻");
        blackOperateLabel.setBounds(10, 120, 80, 18);

        blackOperatePanel = new Panel();
        blackOperatePanel.setBackground(playerSilver);
        blackOperatePanel.setBounds(210, 560, 170, 150);

        blackPlayerType = new CheckboxGroup();
        blackHuman = new Checkbox("1:人間", blackPlayerType, true);
        blackHuman.setBounds(10,10,160,18);
        blackHuman.addItemListener(this);
        blackRandom = new Checkbox("2:CPU(ランダム)", blackPlayerType, false);
        blackRandom.setBounds(10,30,160,18);
        blackRandom.addItemListener(this);        
        blackTwoTree = new Checkbox("3:CPU(木探索-深さ2)", blackPlayerType, false);
        blackTwoTree.setBounds(10,50,160,18);
        blackTwoTree.addItemListener(this);
        blackThreeTree = new Checkbox("4:CPU(木探索-深さ3)", blackPlayerType, false);
        blackThreeTree.setBounds(10,70,160,18);
        blackThreeTree.addItemListener(this);

        panel.add(whiteCountLabel);
        panel.add(blackCountLabel);
        panel.add(playerTurnLabel);
        panel.add(titleLabel);
        add(panel);

        whiteOperatePanel.add(whiteHuman);
        whiteOperatePanel.add(whiteRandom);
        whiteOperatePanel.add(whiteTwoTree);
        whiteOperatePanel.add(whiteThreeTree);
        whiteOperatePanel.add(whiteOperateLabel);
        add(whiteOperatePanel);

        blackOperatePanel.add(blackHuman);
        blackOperatePanel.add(blackRandom);
        blackOperatePanel.add(blackTwoTree);
        blackOperatePanel.add(blackThreeTree);
        blackOperatePanel.add(blackOperateLabel);
        add(blackOperatePanel);

        add(startButton);
        add(exitButton);

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
                //指す場所を選ぶ
                handPoint = calcHandPoint(e.getPoint());
                //駒をひっくり返す
                reverse(handPoint);        
            }
        });
    }

    private void reverse(Point hand){
        String turnText = "";
        if(-1 < hand.getX() && -1 < hand.getY() && onTheGame){
            if(active == 1){
                //white
                reverseWhite((int)hand.getX(), (int)hand.getY());
            }else{
                //black
                reverseBlack((int)hand.getX(), (int)hand.getY());
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
                playerTurnLabel.setText(turnText);
            } else {
                active *= -1;       //change player   
                turnMessage();
                putCandidateArea(revAllList);
            }
            revAllList.clear();
        }
    }

    private void buttonListener(){

        startButton.addActionListener(new  ActionListener(){
            public void actionPerformed(ActionEvent e){

                int wtype = parsePlayerType(whitePlayerType.getSelectedCheckbox().getLabel());
                int btype = parsePlayerType(blackPlayerType.getSelectedCheckbox().getLabel());

                if( 1 == wtype){
                    firstPlayer.setStrategy(null);
                }else{
                    firstPlayer.setStrategy(new RandomStrategy());
                }

                if( 1 == btype ){
                    secondPlayer.setStrategy(null);
                }else{
                    secondPlayer.setStrategy(new RandomStrategy());
                }

                System.out.println(
                    whitePlayerType.getSelectedCheckbox().
                    getLabel() + "," + 
                    blackPlayerType.getSelectedCheckbox().
                    getLabel()
                );
                onTheGame = true; 
                new Thread(game).start();          
            }
        });

        exitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println(e.paramString());
                closed();
            }            
        });        
    }

    private int parsePlayerType(String name){
        String[] sep = name.split(":");
        return Integer.parseInt(sep[0]);
    }

    private Point calcHandPoint(Point p){
        Integer iX;
        Integer iY;
        Point optPoint = new Point(-1, -1);

        iX = (int)((p.getX() - 160.0) / 50);
        iY = (int)((p.getY() - 70.0) / 50);

        if( (-1 < iX && iX < 8) && (-1 < iY && iY < 8)){
            revAllList = reversePiece(iX, iY);
            if(!revAllList.isEmpty()){
                optPoint.setLocation(
                    165 + iX * 50,
                    75 + iY * 50);
            }
        }
        return optPoint;
    }

    public static List<Point> reversePiece(Integer x, Integer y){

        List<Point> revLineList;
        List<Point> revTotalList;
        int opposite;

        revLineList = new ArrayList<>();
        revTotalList = new ArrayList<>();

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
                    for(Point p : revLineList){
                        System.out.println("[left - top]" + p);
                    }
                    revTotalList.addAll(revLineList);
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
                    revTotalList.addAll(revLineList);
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
                    revTotalList.addAll(revLineList);
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
                    revTotalList.addAll(revLineList);
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
                    revTotalList.addAll(revLineList);
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
                    revTotalList.addAll(revLineList);
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
                    revTotalList.addAll(revLineList);
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
                    revTotalList.addAll(revLineList);
                    break;
                } else {
                    break;
                }
            }
        }
        revLineList.clear();

        for(Point p : revTotalList){
            System.out.println("[All]" + p);
        }

        return revTotalList;
    }

    private void putCandidateArea(List<Point> revTotalList){
       int count = 0;
       for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                revTotalList = reversePiece(x, y);
                if(!revTotalList.isEmpty() && boardFlag[x][y] == 0){
                    putCandidate(panelGraphics,x,y);
                    count++;
                } else {
                    if(boardFlag[x][y] == 1){
                        putWhite(panelGraphics,x,y);
                    }else if(boardFlag[x][y] == -1){
                        putBlack(panelGraphics,x,y);
                    }else {
                        putBlankArea(panelGraphics,x,y);
                    }                    
                }
                revTotalList.clear();
            }
       }
       if(count == 0){
            active *= -1;
            turnMessage();
       }
    }

    private void reverseWhite(int initX, int initY){
        Point g = pointToGrid(initX, initY);
        boardFlag[(int)g.getX()][(int)g.getY()] = 1;

        panelGraphics = panel.getGraphics();
        panelGraphics.drawOval(initX, initY, 40, 40);
        for(Point p : revAllList){
            boardFlag[(int)p.getX()][(int)p.getY()] = 1;
            putWhite(panelGraphics, (int)p.getX(), (int)p.getY());
        }
        panel.paint(panelGraphics);
        printBoard();
    }

    private void reverseBlack(int initX, int initY){
        Point g = pointToGrid(initX, initY);
        boardFlag[(int)g.getX()][(int)g.getY()] = -1;

        panelGraphics = panel.getGraphics();
        panelGraphics.fillOval(initX, initY, 40, 40);

        for(Point p : revAllList){
            boardFlag[(int)p.getX()][(int)p.getY()] = -1;
            putBlack(panelGraphics, (int)p.getX(), (int)p.getY());
        }
        panel.paint(panelGraphics);
        printBoard();
    }

    public void putWhite(Graphics g, int x, int y){
        g.setColor(boardGREEN);
        g.fillRect(x * 50 + 161, y * 50 + 71, 49, 49);
        g.setColor(Color.black);
        g.drawOval(x * 50 + 165, y * 50 + 75, 40, 40);
    }

    public void putBlack(Graphics g, int x, int y){
        g.setColor(boardGREEN);
        g.fillRect(x * 50 + 161, y * 50 + 71, 49, 49);
        g.setColor(Color.black);
        g.fillOval(x * 50 + 165, y * 50 + 75, 40, 40);
    }

    public void putBlankArea(Graphics g, int x, int y){
        g.setColor(boardGREEN);
        g.fillRect(x * 50 + 161, y * 50 + 71, 49, 49);
    }

    public void putCandidate(Graphics g, int x, int y){
        g.setColor(candidateBlue);
        g.fillRect(x * 50 + 161, y * 50 + 71, 49, 49);
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

    private void turnMessage(){
        String turnText = "";
        if(active == 1){
            turnText = "白のターン";
        }else{
            turnText = "黒のターン";
        }
        playerTurnLabel.setText(turnText);
    }

    private void closed(){
        onTheGame = false;
        cpuThread.interrupt();
        this.dispose();
    }

}
