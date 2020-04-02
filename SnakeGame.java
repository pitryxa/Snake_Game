import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame {
    public static final int DOT_SIZE = 10;
    public static final int DOTS_HEIGHT = 30;
    public static final int DOTS_WIDTH = 30;
    public static final int SNAKE_SIZE = 2;

    public static final int SNAKE_START_LENGTH = 3;



    public static void main(String[] args) {
        new Window(DOT_SIZE, DOTS_HEIGHT, DOTS_WIDTH);

    }


}

class Window extends JFrame {
    private int screenHeight;
    private int screenWidth;

    public Window(int gridSize, int dotsH, int dotsW) {
        super("Snake Game 1.0");
        ScreenSize();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(dotsW*gridSize+37, dotsH*gridSize+60);
        setLocation((screenWidth-dotsW*gridSize)/2, (screenHeight-dotsH*gridSize)/2);
        add(new Board());
        setVisible(true);

    }

    public void ScreenSize() {
        Dimension sSize = Toolkit.getDefaultToolkit ().getScreenSize ();
        screenHeight = sSize.height;
        screenWidth  = sSize.width;
    }
}

class Board extends JPanel implements ActionListener  {
    private Snake snake;
    private Apple apple;
    Timer timer = new Timer(250,this);


    public Board(){
        snake = new Snake(SnakeGame.SNAKE_START_LENGTH, positionRandom());
        apple = new Apple(positionRandom());

        timer.start();
        addKeyListener(new KeyPressedListener());
        setFocusable(true);


    }

    public static Point positionRandom(){
        int x = (int) Math.round(Math.random() * (SnakeGame.DOTS_WIDTH-1));
        int y = (int) Math.round(Math.random() * (SnakeGame.DOTS_HEIGHT-1));

        return (new Point(x, y));
    }

    public void drawApple(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(0));
        g2d.setColor(Color.red);
        Rectangle2D rect = new Rectangle2D.Double(
                10 + apple.getPos().x * SnakeGame.DOT_SIZE,
                10 + apple.getPos().y * SnakeGame.DOT_SIZE,
                SnakeGame.DOT_SIZE,
                SnakeGame.DOT_SIZE);
        g2d.draw(rect);
        g2d.fill(rect);
    }

    public void drawSnake(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(0));

        Color color;
        int i=0;

        for (Point p : snake.getXY()) {
            color = i==0 ? Color.green.darker() : Color.green;

            Rectangle2D rect = new Rectangle2D.Double(
                    10 + p.x * SnakeGame.DOT_SIZE + SnakeGame.SNAKE_SIZE,
                    10 + p.y * SnakeGame.DOT_SIZE + SnakeGame.SNAKE_SIZE,
                    SnakeGame.DOT_SIZE - SnakeGame.SNAKE_SIZE,
                    SnakeGame.DOT_SIZE - SnakeGame.SNAKE_SIZE);
            g2d.setColor(color);
            g2d.draw(rect);
            g2d.fill(rect);
            i++;
        }
    }

    public void drawField(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(2));
        Rectangle2D rect = new Rectangle2D.Double(10, 10, SnakeGame.DOTS_WIDTH * SnakeGame.DOT_SIZE, SnakeGame.DOTS_HEIGHT * SnakeGame.DOT_SIZE);
        g2d.draw(rect);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawField(g);
        drawSnake(g);
        drawApple(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Point nextHead = Snake.nextHeadByMove();

        if (isApple()) {
            apple.newPos();
            snake.moveSnake(nextHead, true);
        } else {
            snake.moveSnake(nextHead, false);
        }

        repaint();
    }

    public boolean isApple() {
        return (snake.getHead().equals(apple.getPos()));
    }

    class KeyPressedListener extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    snake.setDir(Snake.Direction.UP);
                    break;

                case KeyEvent.VK_RIGHT:
                    snake.setDir(Snake.Direction.RIGHT);
                    break;

                case KeyEvent.VK_DOWN:
                    snake.setDir(Snake.Direction.DOWN);
                    break;

                case KeyEvent.VK_LEFT:
                    snake.setDir(Snake.Direction.LEFT);
                    break;
            }
        }
    }
}

class Snake {
    private static LinkedList<Point> snakeXY = new LinkedList();
    private int length;
    private static Direction curDir;

    public Snake (int length, Point headXY){
        Random random = new Random();

        Direction[] dirs = Direction.values();
        curDir = dirs[random.nextInt(dirs.length)];
        Direction dirBuf = curDir;

        this.length = length;

        snakeXY.add(headXY);

        for (int i=1; i<length; i++){
            Point nextPoint = new Point();
            nextPoint = nextPointWithDir(snakeXY.get(i-1), dirBuf);

            int dirCnt=0;
            while (!isPointInBoard(nextPoint) || isPointIsSnake(nextPoint)){
                if (dirCnt == 4) break;

                dirBuf = nextDir(dirBuf);
                nextPoint = nextPointWithDir(snakeXY.get(i-1), dirBuf);
                dirCnt++;
            }
            if (dirCnt == 4)
                System.exit(1);

            snakeXY.add(nextPoint);
        }
    }

    public LinkedList<Point> getXY(){
        return snakeXY;
    }

    public Direction nextDir(Direction dir){
        int index = dir.ordinal();
        index = index == 3 ? 0 : index+1;
        return Direction.values()[index];
    }

    public static boolean isPointIsSnake(Point p){
        for (Point pSnake : snakeXY) {
            if (p.equals(pSnake))
                return true;

        }

        return false;
    }

    public Point nextPointWithDir(Point curP, Direction dir) {
        Point nextP = new Point();

        nextP.x = curP.x;
        nextP.y = curP.y;

        switch (dir) {
            case UP:
                nextP.y++;
                break;
            case RIGHT:
                nextP.x--;
                break;
            case DOWN:
                nextP.y--;
                break;
            case LEFT:
                nextP.x++;
                break;
        }

        return nextP;
    }

    public boolean isPointInBoard(Point p){
        if (p.x<0 || p.x>=SnakeGame.DOTS_WIDTH || p.y<0 || p.y>=SnakeGame.DOTS_HEIGHT)
            return false;
        return true;
    }

    public static Point nextHeadByMove(){
        Point nextP = new Point();

        nextP.x = getHeadX();
        nextP.y = getHeadY();

        switch (getDir()) {
            case UP:
                nextP.y--;
                break;
            case RIGHT:
                nextP.x++;
                break;
            case DOWN:
                nextP.y++;
                break;
            case LEFT:
                nextP.x--;
                break;
        }

        return nextP;
    }

    public static Direction getDir() {
        return curDir;
    }

    public void moveSnake(Point nextHead, boolean isApple){


        if (!isPointInBoard(nextHead) || isPointIsSnake(nextHead)) {
            System.out.println("Game Over");
            System.exit(-1);
        }

        snakeXY.addFirst(nextHead);
        if (!isApple) {
            snakeXY.removeLast();
        }

    }

    public static int getHeadX(){
        if (snakeXY.size()==0) return -1;

        return snakeXY.get(0).x;
    }

    public static int getHeadY(){
        if (snakeXY.size()==0) return -1;

        return snakeXY.get(0).y;
    }

    public Point getHead() {
        if (snakeXY.size()==0) return null;

        return snakeXY.get(0);
    }

    public void setDir(Direction direction) {
        curDir = direction;
    }

    enum Direction {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }
}

class Apple {
    private Point appleXY;

    public Apple(Point pos){
        while (Snake.isPointIsSnake(pos)) {
            pos = Board.positionRandom();
        }

        appleXY = pos;
    }

    public Point newPos() {
        Point pos = Board.positionRandom();

        while (Snake.isPointIsSnake(pos)) {
            pos = Board.positionRandom();
        }

        appleXY = pos;
        return pos;
    }

    public Point getPos(){
        return appleXY;
    }
}

