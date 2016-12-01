
public class Main{

    Othello game;
    Thread  cpuThread;

    public Main(){
        game = new Othello();
        cpuThread = new Thread(game);
        cpuThread.start();
    }

    public static void main(String... args){
        new Main();
    }

}