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
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Random;

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
    Checkbox whiteScorePriority;
    Checkbox whiteTwoTree;
    Checkbox whiteEvaluate;

    Checkbox blackHuman;
    Checkbox blackRandom;
    Checkbox blackScorePriority;
    Checkbox blackTwoTree;
    Checkbox blackEvaluate;

    static Othello game;
    static Thread cpuThread;

    static Integer thinkTime;

    String[] strategyName = {
        "NotName","NotName",
        "RandomStrategy",
        "ScorePriorityStrategy",
        "MinMaxStrategy",
        "EvaluateStrategy"
    };

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
        thinkTime = 5;
        game = new Othello();
        cpuThread = new Thread(game);
        cpuThread.start();
	}

    public void run(){

        int count = 0;
        Point nextHand;
        Calendar c = Calendar.getInstance();
        long seed = c.get(Calendar.SECOND) * c.get(Calendar.MILLISECOND);
        Random r = new Random(seed);

        try{
            //駒の初期配置
            while(initPiece){
                Thread.sleep(500);
                panelGraphics = panel.getGraphics();

                for(int x = 0; x < 8;x++){
                    for(int y = 0; y < 8;y++){
                        putBlankArea(panelGraphics,x,y);
                    }
                }
                panelGraphics.drawOval(315, 225, 40, 40);
                panelGraphics.fillOval(365, 225, 40, 40);
                panelGraphics.fillOval(315, 275, 40, 40);
                panelGraphics.drawOval(365, 275, 40, 40);
                initPiece = false;
                initBoard();
                putCandidateArea(revAllList);
                System.out.println("Init");
            }
            
            while(onTheGame){
                Thread.sleep(thinkTime * 100);
                if(active == 1 && Objects.nonNull(firstPlayer.getStrategy())){
                    nextHand = firstPlayer.nextHand();
                    if(nextHand == null){
                        List<Point> restList = scanRestArea();
                        Point thisPoint = restList.get(r.nextInt(restList.size()));
                        Point coordinatePoint = gridToPoint(thisPoint);
                        reverseBlack((int)coordinatePoint.getX(), (int)coordinatePoint.getY());
                        System.out.println("+++++no put white stone+++++");
                    }else{
                        revAllList.clear();
                        revAllList = reversePiece((int)nextHand.getX(), (int)nextHand.getY());
                        reverse(gridToPoint(nextHand));
                    }
                } else if (active == -1 && Objects.nonNull(secondPlayer.getStrategy())) {
                    nextHand = secondPlayer.nextHand();
                    if(nextHand == null){
                        List<Point> restList = scanRestArea();
                        Point thisPoint = restList.get(r.nextInt(restList.size()));
                        Point coordinatePoint = gridToPoint(thisPoint);
                        reverseWhite((int)coordinatePoint.getX(), (int)coordinatePoint.getY());
                        System.out.println("+++++no put black stone+++++");
                    }else{
                        revAllList.clear();
                        revAllList = reversePiece((int)nextHand.getX(), (int)nextHand.getY());
                        reverse(gridToPoint(nextHand));
                    }
                }

                if(count % 2 == 0){
                    System.out.println( (++count / 2) + "[sec]");
                }
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
        whiteOperateLabel.setBounds(10, 130, 80, 18);

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
        whiteScorePriority = new Checkbox("3:CPU(スコア優先)", whitePlayerType, false);
        whiteScorePriority.setBounds(10,50,160,18);
        whiteScorePriority.addItemListener(this);
        whiteTwoTree = new Checkbox("4:CPU(MinMax探索)", whitePlayerType, false);
        whiteTwoTree.setBounds(10,70,160,18);
        whiteTwoTree.addItemListener(this);
        whiteEvaluate = new Checkbox("5:CPU(評価探索)", whitePlayerType, false);
        whiteEvaluate.setBounds(10,90,160,18);
        whiteEvaluate.addItemListener(this);

        Label blackOperateLabel = new Label("黒 - 後攻");
        blackOperateLabel.setBounds(10, 130, 80, 18);

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
        blackScorePriority = new Checkbox("3:CPU(スコア優先)", blackPlayerType, false);
        blackScorePriority.setBounds(10,50,160,18);
        blackScorePriority.addItemListener(this);
        blackTwoTree = new Checkbox("4:CPU(MinMax探索)", blackPlayerType, false);
        blackTwoTree.setBounds(10,70,160,18);
        blackTwoTree.addItemListener(this);
        blackEvaluate = new Checkbox("5:CPU(評価探索)", blackPlayerType, false);
        blackEvaluate.setBounds(10,90,160,18);
        blackEvaluate.addItemListener(this);

        panel.add(whiteCountLabel);
        panel.add(blackCountLabel);
        panel.add(playerTurnLabel);
        panel.add(titleLabel);
        add(panel);

        whiteOperatePanel.add(whiteHuman);
        whiteOperatePanel.add(whiteRandom);
        whiteOperatePanel.add(whiteScorePriority);
        whiteOperatePanel.add(whiteTwoTree);
        whiteOperatePanel.add(whiteEvaluate);
        whiteOperatePanel.add(whiteOperateLabel);
        add(whiteOperatePanel);

        blackOperatePanel.add(blackHuman);
        blackOperatePanel.add(blackRandom);
        blackOperatePanel.add(blackScorePriority);
        blackOperatePanel.add(blackTwoTree);
        blackOperatePanel.add(blackEvaluate);
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

            if(restCount() == 0 || firstPlayer.getScore() == 0 || secondPlayer.getScore() == 0){
                onTheGame = false;
                active = 1;
                if(secondPlayer.getScore() < firstPlayer.getScore() ){
                    turnText = "白の勝ち";
                }else if(firstPlayer.getScore() < secondPlayer.getScore()){
                    turnText = "黒の勝ち";
                }else{
                    turnText = "引き分け";
                }
                playerTurnLabel.setText(turnText);
                startButton.setEnabled(true);
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
            public void actionPerformed(ActionEvent e) {

                int wtype = parsePlayerType(whitePlayerType.getSelectedCheckbox().getLabel());
                int btype = parsePlayerType(blackPlayerType.getSelectedCheckbox().getLabel());

                if( 1 == wtype){
                    firstPlayer.setStrategy(null);
                }else{
                    setFirstCPUStrategy(wtype);
                }

                if( 1 == btype ){
                    secondPlayer.setStrategy(null);
                }else{
                    setSecondCPUStrategy(btype);
                }

                System.out.println(
                    whitePlayerType.getSelectedCheckbox().
                    getLabel() + "," + 
                    blackPlayerType.getSelectedCheckbox().
                    getLabel()
                );

                initPiece = true; 
                onTheGame = true; 
                startButton.setEnabled(false);
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

    private void setFirstCPUStrategy(int type){

        try{
            Class cs = Class.forName(strategyName[type]);
            Object obj = (Strategy)cs.newInstance();
            if( obj instanceof Strategy){
                firstPlayer.setStrategy((Strategy)obj);
            }
        }catch(ClassNotFoundException e){
            System.out.println(e.toString());
        }catch(InstantiationException e){
            System.out.println(e.toString());
        }catch(IllegalAccessException e){
            System.out.println(e.toString());
        }
    }

    private void setSecondCPUStrategy(int type){
        try{
            Class cs = Class.forName(strategyName[type]);
            Object obj = (Strategy)cs.newInstance();
            if( obj instanceof Strategy){
                secondPlayer.setStrategy((Strategy)obj);
            }
        }catch(ClassNotFoundException e){
            System.out.println(e.toString());
        }catch(InstantiationException e){
            System.out.println(e.toString());
        }catch(IllegalAccessException e){
            System.out.println(e.toString());
        }
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
        
        //System.out.println("x = " + x + ",y = " + y + ",op = " + opposite);

        //left - top
        if(0 < x && 0 < y){            
            for(int i = 1 ; 0 < x - i && 0 < y - i ; i++){
                if(opposite == boardFlag[x - i][y - i]){       
                    revLineList.add(new Point(x - i, y - i));
                } else if ( i != 1 && (opposite * -1) == boardFlag[x - i][y - i]){
                    for(Point p : revLineList){
                        //System.out.println("[left - top]" + p);
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
                        //System.out.println("[center - top]" + p);
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
                        //System.out.println("[right - top]" + p);
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
                        //System.out.println("[left]" + p);
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
                        //System.out.println("[right]" + p);
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
                        //System.out.println("[left - bottom]" + p);
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
                        //System.out.println("[center - bottom]" + p);
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
                        //System.out.println("[right - bottom]" + p);
                    }
                    revTotalList.addAll(revLineList);
                    break;
                } else {
                    break;
                }
            }
        }
        revLineList.clear();

        /*
        for(Point p : revTotalList){
            System.out.println("[All]" + p);
        }
        */
        return revTotalList;
    }

    public static List<Point> reversePiece(Integer x, Integer y, Integer[][] board, Integer active){

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
        
        //left - top
        if(0 < x && 0 < y){            
            for(int i = 1 ; 0 < x - i && 0 < y - i ; i++){
                if(opposite == board[x - i][y - i]){       
                    revLineList.add(new Point(x - i, y - i));
                } else if ( i != 1 && (opposite * -1) == board[x - i][y - i]){
                    for(Point p : revLineList){
                        //System.out.println("[left - top]" + p);
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
                if(opposite == board[x][y - i]){       
                    revLineList.add(new Point(x, y - i));
                } else if ( i != 1 && (opposite * -1) == board[x][y - i]){
                    for(Point p :revLineList){
                        //System.out.println("[center - top]" + p);
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
                if(opposite == board[x + i][y - i]){       
                    revLineList.add(new Point(x + i, y - i));
                } else if ( i != 1 && (opposite * -1) == board[x + i][y - i]){
                    for(Point p :revLineList){
                        //System.out.println("[right - top]" + p);
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
                if(opposite == board[x - i][y]){       
                    revLineList.add(new Point(x - i, y));
                } else if ( i != 1 && (opposite * -1) == board[x - i][y]){
                    for(Point p :revLineList){
                        //System.out.println("[left]" + p);
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
                if(opposite == board[x + i][y]){       
                    revLineList.add(new Point(x + i, y));
                } else if ( i != 1 && (opposite * -1) == board[x + i][y]){
                    for(Point p :revLineList){
                        //System.out.println("[right]" + p);
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
                if(opposite == board[x - i][y + i]){       
                    revLineList.add(new Point(x - i, y + i));
                } else if ( i != 1 && (opposite * -1) == board[x - i][y + i]){
                    for(Point p :revLineList){
                        //System.out.println("[left - bottom]" + p);
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
                if(opposite == board[x][y + i]){       
                    revLineList.add(new Point(x, y + i));
                } else if ( i != 1 && (opposite * -1) == board[x][y + i]){
                    for(Point p :revLineList){
                        //System.out.println("[center - bottom]" + p);
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
                if(opposite == board[x + i][y + i]){       
                    revLineList.add(new Point(x + i, y + i));
                } else if ( i != 1 && (opposite * -1) == board[x + i][y + i]){
                    for(Point p :revLineList){
                        //System.out.println("[right - bottom]" + p);
                    }
                    revTotalList.addAll(revLineList);
                    break;
                } else {
                    break;
                }
            }
        }
        revLineList.clear();

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

    static public int restCount(){
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

    public List<Point> scanRestArea(){
        List<Point> pList = new ArrayList<>();
        for(int y = 0; y < 8;y++){
            for(int x = 0; x < 8;x++){
                if(boardFlag[x][y] == 0){
                    pList.add(new Point(x,y));
                }
            }
        }
        return pList;
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
            (int)(g.getX()) * 50 + 165,
            (int)(g.getY()) * 50 + 75
            );
        return pt;
    }

    private Point gridToPoint(int x, int y){
        Point pt = new Point(
            x * 50 + 165,
            y * 50 + 75
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

    private void initBoard(){

        for(int y = 0;y < 8;y++){
            for(int x = 0;x < 8;x++){
                if((x == 3 && y == 3) || (x == 4 && y == 4)){
                    boardFlag[x][y] = 1;
                }else if((x == 4 && y == 3) || (x == 3 && y == 4)){
                    boardFlag[x][y] = -1;
                } else {
                    boardFlag[x][y] = 0;
                }
            }
        }
    }

    private void closed(){
        onTheGame = false;
        cpuThread.interrupt();
        this.dispose();
    }

}
