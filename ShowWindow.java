import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
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

class ShowWindow extends Frame{

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

    Player first;
    Player second;
    /* 1 : first -1 : second */
    Integer active = 1;

    public ShowWindow(){
        initLayout();
        first = new Player(2);
        second = new Player(2);
    }

	public static void main (String[] args){
        new ShowWindow();
	}

    public void paint(Graphics g){
        g.drawLine(50,50,100,0);
        g.drawOval(100,100,30,30);
    }

    private void initLayout(){
        btn = new  Button("Submit");

        panel = new Panel(){
            public void paint(Graphics g){
                panelGraphics = g;
                initBoard();
            }
        };

        handPoint = new Point(0,0);

        setBackground(new Color(200,200,0));
        setBounds(10,10,720,720);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setVisible(true);

        panel.setBackground(new Color(0,222,0));
        panel.setBounds(0,10,720,540);

        btn.setBackground(new Color(128,0,128));
        btn.setBounds(320,550,80,30);
        
        add(panel);
        add(btn);

        frameListener();
        panelListener();
        buttonListener();
    }

    private void initBoard(){
        //row
        panelGraphics.drawLine(160, 70, 560, 70);
        panelGraphics.drawLine(160, 120, 560, 120);
        panelGraphics.drawLine(160, 170, 560, 170);
        panelGraphics.drawLine(160, 220, 560, 220);
        panelGraphics.drawLine(160, 270, 560, 270);
        panelGraphics.drawLine(160, 320, 560, 320);
        panelGraphics.drawLine(160, 370, 560, 370);
        panelGraphics.drawLine(160, 420, 560, 420);
        panelGraphics.drawLine(160, 470, 560, 470);

        //col
        panelGraphics.drawLine(160, 70, 160, 470);
        panelGraphics.drawLine(210, 70, 210, 470);
        panelGraphics.drawLine(260, 70, 260, 470);
        panelGraphics.drawLine(310, 70, 310, 470);
        panelGraphics.drawLine(360, 70, 360, 470);
        panelGraphics.drawLine(410, 70, 410, 470);
        panelGraphics.drawLine(460, 70, 460, 470);
        panelGraphics.drawLine(510, 70, 510, 470);
        panelGraphics.drawLine(560, 70, 560, 470);

        //init chip
        panelGraphics.drawOval(315, 225, 40, 40);
        panelGraphics.fillOval(365, 225, 40, 40);
        panelGraphics.fillOval(315, 275, 40, 40);
        panelGraphics.drawOval(365, 275, 40, 40);
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

                handPoint = calcHandPoint(e.getPoint(), active);

                if(-1 < handPoint.getX() && -1 < handPoint.getY()){
                    panelGraphics = panel.getGraphics();

                    panelGraphics.drawOval((int)handPoint.getX(),
                                            (int)handPoint.getY(),
                                            40,40);
                    panel.paint(panelGraphics);
                    active *= -1;
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
            if(checkPiece(iX, iY, active)){
                optPoint.setLocation(
                    165 + iX * 50,
                    75 + iY * 50);
            }
        }

        return optPoint;
    }

    private boolean checkPiece(Integer x, Integer y, Integer active){

        List<Point> revAllList;
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
                    revAllList.addAll(revLineList);
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
                    revAllList.addAll(revLineList);
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
                    revAllList.addAll(revLineList);
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
                    revAllList.addAll(revLineList);
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
                    revAllList.addAll(revLineList);
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
                    revAllList.addAll(revLineList);
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
                    revAllList.addAll(revLineList);
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
                    revAllList.addAll(revLineList);
                    break;
                }
            }
        }
        revLineList.clear();

        for(int i = 0; i < revAllList.size() ;i++){
            System.out.println(revAllList.get(i));
        }

        return true;
    }

    private void closed(){
        this.dispose();
    }

}
