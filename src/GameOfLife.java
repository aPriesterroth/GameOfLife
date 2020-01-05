import javax.swing.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameOfLife implements Runnable {

    public static final int WIDTH = 1080;
    public static final int HEIGHT = 720;

    public static final int RECTANGLE_SIZE_PX = 10;

    public static final int INITIAL_CELLS_COUNT = 5000;

    private static final int SLEEP_TIME_MS = 200;

    private final JFrame frame;

    private boolean running = false;

    private boolean[][] cells;
    private boolean[][] cellsCopy;

    public GameOfLife() {
        int width = (WIDTH / RECTANGLE_SIZE_PX);
        int height = (HEIGHT / RECTANGLE_SIZE_PX);

        cells = new boolean[width][height];

        frame = new JFrame("Conway's Game of Life");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new GridPanel(cells, RECTANGLE_SIZE_PX));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        running = true;
    }

    @Override
    public void run() {

        //initializeCells();
        setPattern();

        int cellsWidth = (WIDTH / RECTANGLE_SIZE_PX);
        int cellsHeight = (HEIGHT / RECTANGLE_SIZE_PX);

        int gen = 0;

        while(running) {

            cellsCopy = new boolean[cellsWidth][cellsHeight];

            for(int x = 0; x < cellsWidth; x++) {
                for(int y  = 0; y < cellsHeight; y++) {
                   applyRules(cellsCopy, x, y);
                }
            }

            cells = cellsCopy;
            gen++;

            frame.getContentPane().removeAll();
            frame.add(new GridPanel(cells, RECTANGLE_SIZE_PX));
            frame.revalidate();

            System.out.println("Generation: " + gen);
            System.out.println("Sleeping");
            try {
                Thread.sleep(SLEEP_TIME_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Awake");
        }
    }

    private void applyRules(boolean[][] cellsCopy, int x, int y) {

        int neighbours = getNeighbours(x, y);

        if(isCellAlive(x, y) && neighbours < 2) { // Loneliness
            cellsCopy[x][y] = false;
        } else if(isCellAlive(x, y) && neighbours > 3) { // Overpopulation
            cellsCopy[x][y] = false;
        } else if(!isCellAlive(x, y) && neighbours == 3) { // Reproduction
            cellsCopy[x][y] = true;
        } else {
            cellsCopy[x][y] = cells[x][y];
        }
    }

    private int getNeighbours(int x, int y) {
        int neighbours = 0;

        // Top left
        if(x-1 >= 0 && y-1 >= 0 && cells[x-1][y-1]) neighbours++;
        // Top center
        if(y-1 >= 0 && cells[x][y-1]) neighbours++;
        // Top right
        if(x+1 < cells.length && y-1 >= 0 && cells[x+1][y-1]) neighbours++;
        // Left
        if(x-1 >= 0 && cells[x-1][y]) neighbours++;
        // Right
        if(x+1 < cells.length && cells[x+1][y]) neighbours++;
        // Bottom left
        if(x-1 >= 0 && y+1 < cells[x-1].length && cells[x-1][y+1]) neighbours++;
        // Bottom center
        if(y+1 < cells[x].length && cells[x][y+1]) neighbours++;
        // Bottom right
        if(x+1 < cells.length && y+1 < cells[x+1].length && cells[x+1][y+1]) neighbours++;

        return neighbours;
    }

    private void initializeCells() {
        int width = (WIDTH / RECTANGLE_SIZE_PX);
        int height = (HEIGHT / RECTANGLE_SIZE_PX);

        for(int i = 0; i < INITIAL_CELLS_COUNT; i++) {
            int x = ThreadLocalRandom.current().nextInt(width);
            int y = ThreadLocalRandom.current().nextInt(height);

            cells[x][y] = true;
        }
    }

    private void setPattern() {
        cells[15][11] = true;
        cells[15][12] = true;
        cells[16][11] = true;
        cells[16][12] = true;

        cells[19][10] = true;
        cells[19][11] = true;
        cells[19][12] = true;
        cells[20][10] = true;
        cells[20][11] = true;
        cells[20][12] = true;

        cells[24][8] = true;
        cells[24][9] = true;
        cells[24][13] = true;
        cells[24][14] = true;
        cells[25][9] = true;
        cells[25][10] = true;
        cells[25][11] = true;
        cells[25][12] = true;
        cells[25][13] = true;
        cells[26][10] = true;
        cells[26][11] = true;
        cells[26][12] = true;
        cells[27][11] = true;

        cells[34][12] = true;
        cells[35][13] = true;
        cells[35][14] = true;
        cells[36][12] = true;
        cells[36][13] = true;

        cells[41][6] = true;
        cells[41][7] = true;
        cells[41][11] = true;
        cells[41][12] = true;
        cells[42][8] = true;
        cells[42][9] = true;
        cells[42][10] = true;
        cells[43][7] = true;
        cells[43][11] = true;
        cells[44][8] = true;
        cells[44][10] = true;
        cells[45][9] = true;

        cells[49][9] = true;
        cells[49][10] = true;
        cells[50][9] = true;
        cells[50][10] = true;
    }

    public boolean isCellAlive(int x, int y) {
        return cells[x][y];
    }

    public static void main(String[] args) {
        new Thread(new GameOfLife()).start();
    }
}
