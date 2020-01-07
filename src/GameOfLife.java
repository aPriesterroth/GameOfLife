import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.ThreadLocalRandom;

public class GameOfLife implements Runnable {

    private static final int WIDTH = 1080;
    private static final int HEIGHT = 720;

    private static final int RECTANGLE_SIZE_PX_DEFAULT = 8;

    private static final int CELLS_COUNT_HORIZONTAL = WIDTH / RECTANGLE_SIZE_PX_DEFAULT;
    private static final int CELLS_COUNT_VERTICAL = HEIGHT / RECTANGLE_SIZE_PX_DEFAULT;

    private static final int RANDOM_PATTERN_CELL_COUNT_DEFAULT = 1000;

    private static final int MOUSE_COORDINATE_CORRECTION_X = 8;
    private static final int MOUSE_COORDINATE_CORRECTION_Y = 68;

    private static final int SLEEP_TIME_DEFAULT_MS = 100;

    private int sleepTime = SLEEP_TIME_DEFAULT_MS;

    private final JFrame frame;
    private final JTextField fieldSleepTime;
    private final JTextField fieldCellCount;

    private GridPanel panel;

    private boolean mouseMode = false;

    private boolean simulating = false;
    private boolean singleStep = false;

    private boolean[][] cells;

    public GameOfLife() {
        cells = new boolean[CELLS_COUNT_HORIZONTAL][CELLS_COUNT_VERTICAL];

        MouseListener listener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

                if (!isSimulating()) {

                    int cellX = (((e.getX() - MOUSE_COORDINATE_CORRECTION_X) / RECTANGLE_SIZE_PX_DEFAULT));
                    int cellY = (((e.getY() - MOUSE_COORDINATE_CORRECTION_Y) / RECTANGLE_SIZE_PX_DEFAULT));

                    if ((cellX >= 0 && cellX < CELLS_COUNT_HORIZONTAL) && (cellY >= 0 && cellY < CELLS_COUNT_VERTICAL)) {
                        mouseMode = cells[cellX][cellY];
                        cells[cellX][cellY] = !cells[cellX][cellY];
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };

        MouseMotionListener motionListener = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!isSimulating()) {
                    int cellX = (((e.getX() - MOUSE_COORDINATE_CORRECTION_X) / RECTANGLE_SIZE_PX_DEFAULT));
                    int cellY = (((e.getY() - MOUSE_COORDINATE_CORRECTION_Y) / RECTANGLE_SIZE_PX_DEFAULT));

                    if ((cellX >= 0 && cellX < CELLS_COUNT_HORIZONTAL) && (cellY >= 0 && cellY < CELLS_COUNT_VERTICAL) && cells[cellX][cellY] == mouseMode) {
                        cells[cellX][cellY] = !cells[cellX][cellY];
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        };

        frame = new JFrame("Conway's Game of Life");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.addMouseListener(listener);
        frame.addMouseMotionListener(motionListener);
        frame.setLayout(new BorderLayout());

        // Menu bar
        JPanel panelMenu = new JPanel();
        panelMenu.setLayout(new FlowLayout());

        // Start button
        JButton buttonStart = new JButton("Start");
        buttonStart.addActionListener((ActionEvent e) -> start());
        buttonStart.setFocusable(false);
        buttonStart.setContentAreaFilled(false);
        panelMenu.add(buttonStart);

        // Stop button
        JButton buttonStop = new JButton("Stop");
        buttonStop.addActionListener((ActionEvent e) -> stop());
        buttonStop.setFocusable(false);
        buttonStop.setContentAreaFilled(false);
        panelMenu.add(buttonStop);

        // Single step button
        JButton buttonSingleStep = new JButton(">");
        buttonSingleStep.addActionListener((ActionEvent e) -> singleStep = true);
        buttonSingleStep.setFocusable(false);
        buttonSingleStep.setContentAreaFilled(false);
        panelMenu.add(buttonSingleStep);

        // Clear button
        JButton buttonClear = new JButton("Clear");
        buttonClear.addActionListener((ActionEvent e) -> clear());
        buttonClear.setFocusable(false);
        buttonClear.setContentAreaFilled(false);
        panelMenu.add(buttonClear);

        // Sleep time label
        JLabel labelSleepTime = new JLabel("Speed: ");
        panelMenu.add(labelSleepTime);

        // Sleep time input field
        fieldSleepTime = new JTextField(Integer.toString(SLEEP_TIME_DEFAULT_MS), 5);
        panelMenu.add(fieldSleepTime);

        // Set speed button
        JButton buttonSpeed = new JButton("Set speed");
        buttonSpeed.addActionListener((ActionEvent e) -> setSpeed());
        buttonSpeed.setFocusable(false);
        buttonSpeed.setContentAreaFilled(false);
        panelMenu.add(buttonSpeed);

        // Producer pattern item
        JButton buttonSpaceGun = new JButton("Space gun");
        buttonSpaceGun.addActionListener((ActionEvent e) -> cellsSpaceGun());
        buttonSpaceGun.setFocusable(false);
        buttonSpaceGun.setContentAreaFilled(false);
        panelMenu.add(buttonSpaceGun);

        // Random pattern item
        JButton buttonRandom = new JButton(("Random"));
        buttonRandom.addActionListener((ActionEvent e) -> cellsRandom());
        buttonRandom.setFocusable(false);
        buttonRandom.setContentAreaFilled(false);
        panelMenu.add(buttonRandom);
        
        // Random pattern cell count label
        JLabel labelCellCount = new JLabel("Random sample size:");
        labelCellCount.setOpaque(false);
        panelMenu.add(labelCellCount);

        // Random pattern cell count input field
        fieldCellCount = new JTextField(Integer.toString(RANDOM_PATTERN_CELL_COUNT_DEFAULT), 5);
        labelCellCount.setOpaque(false);
        panelMenu.add(fieldCellCount);

        frame.add(panelMenu, BorderLayout.NORTH);

        panel = new GridPanel(cells, WIDTH, HEIGHT, RECTANGLE_SIZE_PX_DEFAULT);
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void run() {

        while(isRunning()) {

            if(isSimulating() || isSingleStep()) {
                boolean[][] cellsCopy = new boolean[CELLS_COUNT_HORIZONTAL][CELLS_COUNT_VERTICAL];

                for(int x = 0; x < CELLS_COUNT_HORIZONTAL; x++) {
                    for(int y  = 0; y < CELLS_COUNT_VERTICAL; y++) {
                        applyRules(cellsCopy, x, y);
                    }
                }

                cells = cellsCopy;
                singleStep = false;
            }

            frame.getContentPane().remove(panel);
            frame.add(panel = new GridPanel(cells, WIDTH, HEIGHT, RECTANGLE_SIZE_PX_DEFAULT));
            frame.revalidate();

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setSpeed() {
        sleepTime = Integer.parseInt(fieldSleepTime.getText());
    }

    private void clear() {
        cells = new boolean[CELLS_COUNT_HORIZONTAL][CELLS_COUNT_VERTICAL];
        stop();
    }

    private void stop() {
        simulating = false;
    }

    private void start() {
        simulating = true;
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

    private void cellsRandom() {
        clear();

        int sampleSize = Integer.parseInt(fieldCellCount.getText());

        for(int i = 0; i < sampleSize; i++) {
            int x = ThreadLocalRandom.current().nextInt(CELLS_COUNT_HORIZONTAL);
            int y = ThreadLocalRandom.current().nextInt(CELLS_COUNT_VERTICAL);

            cells[x][y] = true;
        }
    }

    private void cellsSpaceGun() {
        clear();

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

    private boolean isCellAlive(int x, int y) {
        return cells[x][y];
    }

    private boolean isRunning() {
        boolean running = true;
        return running;
    }

    private boolean isSimulating() {
        return simulating;
    }

    private boolean isSingleStep() {
        return singleStep;
    }

    public static void main(String[] args) {
        new Thread(new GameOfLife()).start();
    }
}
