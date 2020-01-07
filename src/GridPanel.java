import javax.swing.*;
import java.awt.*;

public class GridPanel extends JPanel {

    private final boolean[][] cells;

    private final int width;
    private final int height;
    private final int rectangleSize;

    public GridPanel(boolean[][] cells, int width, int height, int rectangleSize) {
        this.cells = cells;
        this.width = width;
        this.height = height;
        this.rectangleSize = rectangleSize;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        for(int x = 0; x < (getWidth() / rectangleSize); x += 1) {
            for (int y = 0; y < (getHeight() / rectangleSize); y += 1) {

                if(cells[x][y]) {
                    g2d.fillRect(x * rectangleSize, y * rectangleSize, rectangleSize, rectangleSize);
                } else {
                    g.drawRect(x * rectangleSize, y * rectangleSize, rectangleSize, rectangleSize);
                }
            }
        }
        g2d.dispose();
    }
}
