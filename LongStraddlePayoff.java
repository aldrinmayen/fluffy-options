import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;

public class LongStraddlePayoff extends JPanel {

    private double strikePrice;
    private double callPrice;
    private double putPrice;
    private String expirationDate;
    private final DecimalFormat df = new DecimalFormat("#.##");

    public LongStraddlePayoff(double strikePrice, double callPrice, double putPrice, String expirationDate) {
        this.strikePrice = strikePrice;
        this.callPrice = callPrice;
        this.putPrice = putPrice;
        this.expirationDate = expirationDate;
    
    }
    // ... (constructor)

    public void updateParameters(double strikePrice, double callPrice, double putPrice, String expirationDate) {
        this.strikePrice = strikePrice;
        this.callPrice = callPrice;
        this.putPrice = putPrice;
        this.expirationDate = expirationDate;
        repaint(); // Request a redraw of the panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 50;
        int zeroY = height / 2;

        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.draw(new Line2D.Double(padding, zeroY, width - padding, zeroY)); // x-axis (Underlying Price)
        g2d.draw(new Line2D.Double(padding, padding, padding, height - padding)); // y-axis (Profit/Loss)

        // Draw labels
        g2d.drawString("Underlying Price", width / 2 - 60, zeroY + 20);
        g2d.drawString("Profit/Loss", padding - 35, height / 2);
        g2d.drawString("Long Straddle - Expiration: " + expirationDate + ", Strike: $" + df.format(strikePrice) + ", Call: $" + df.format(callPrice) + ", Put: $" + df.format(putPrice), 10, 20);

        // Draw payoff area
        drawPayoffArea(g2d, width, height, padding, zeroY);

        // Draw strike price line and label
        g2d.setColor(Color.GRAY);
        double startPrice = 0;
        double endPrice = strikePrice * 2;
        int strikeX = calculateX(strikePrice, width - 2 * padding, startPrice, endPrice) + padding;
        g2d.draw(new Line2D.Double(strikeX, padding, strikeX, height - padding));
        g2d.drawString("Strike: $" + df.format(strikePrice), strikeX - 25, zeroY - 15);

        // Draw break-even points
        double breakEvenUp = strikePrice + callPrice + putPrice;
        int breakEvenUpX = calculateX(breakEvenUp, width - 2 * padding, startPrice, endPrice) + padding;
        g2d.setColor(Color.BLUE);
        g2d.fillOval(breakEvenUpX - 3, zeroY - 3, 6, 6);
        g2d.drawString("BE Up: $" + df.format(breakEvenUp), breakEvenUpX + 5, zeroY + 15);

        double breakEvenDown = strikePrice - (callPrice + putPrice);
        int breakEvenDownX = calculateX(breakEvenDown, width - 2 * padding, startPrice, endPrice) + padding;
        g2d.fillOval(breakEvenDownX - 3, zeroY - 3, 6, 6);
        g2d.drawString("BE Down: $" + df.format(breakEvenDown), breakEvenDownX + 5, zeroY + 30);

        // Draw vertical profit/loss values
        int numYLabs = 5;
        double maxAbsPayoff = Math.max(strikePrice, Math.abs(callPrice + putPrice) * 2) + 10;
        for (int i = 0; i <= numYLabs; i++) {
            double yValue = calculateYValue(i, numYLabs, height - 2 * padding);
            int yCoord = padding + (int) (yValue * (height - 2 * padding));
            double payoffValue = mapYToPayoff(yValue, height - 2 * padding, maxAbsPayoff);
            if (Math.abs(payoffValue) > 0.01) {
                g2d.drawString("$" + df.format(payoffValue), padding - 40, yCoord + 5);
            }
            g2d.draw(new Line2D.Double(padding - 5, yCoord, padding, yCoord)); // Tick mark
        }

        // Draw horizontal price values (starting from 0)
        int numXLabs = 5;
        for (int i = 0; i <= numXLabs; i++) {
            double xValue = (double) i / numXLabs;
            int xCoord = padding + (int) (xValue * (width - 2 * padding));
            double priceValue = xValue * endPrice;
            g2d.drawString("$" + df.format(priceValue), xCoord - 15, zeroY + 35);
            g2d.draw(new Line2D.Double(xCoord, zeroY, xCoord, zeroY + 5)); // Tick mark
        }
    }

    private void drawPayoffArea(Graphics2D g2d, int panelWidth, int panelHeight, int padding, int zeroY) {
        int startX = padding;
        int endX = panelWidth - padding;
        double startPrice = 0;
        double endPrice = strikePrice * 2;
        double priceStep = (endPrice - startPrice) / (endX - startX);

        for (int x = startX; x < endX; x++) {
            double currentUnderlyingPrice = startPrice + (x - startX) * priceStep;
            double currentPayoff = calculateCombinedPayoff(currentUnderlyingPrice);
            int currentY = calculateYCoordinate(currentPayoff, panelHeight, padding);

            if (currentPayoff >= 0) {
                g2d.setColor(new Color(0, 255, 0, 100));
                g2d.fillRect(x, Math.min(currentY, zeroY), 1, Math.abs(currentY - zeroY));
            } else {
                g2d.setColor(new Color(255, 0, 0, 100));
                g2d.fillRect(x, Math.min(currentY, zeroY), 1, Math.abs(currentY - zeroY));
            }
            g2d.setColor(Color.GREEN);
            g2d.draw(new Line2D.Double(x, currentY, x + 1, currentY));
        }
    }

    private double calculateCombinedPayoff(double currentUnderlyingPrice) {
        double callPayoff = Math.max(0, currentUnderlyingPrice - strikePrice) - callPrice;
        double putPayoff = Math.max(0, strikePrice - currentUnderlyingPrice) - putPrice;
        return callPayoff + putPayoff;
    }

    private int calculateX(double price, int availableWidth, double minPrice, double maxPrice) {
        return (int) (((price - minPrice) / (maxPrice - minPrice)) * availableWidth);
    }

    private int calculateYCoordinate(double payoff, int panelHeight, int padding) {
        double maxAbsPayoff = Math.max(strikePrice, Math.abs(callPrice + putPrice) * 2) + 10; // Adjust scaling
        return padding + (int) ((maxAbsPayoff - payoff) / (2 * maxAbsPayoff) * (panelHeight - 2 * padding));
    }

    private double calculateYValue(int index, int total, int availableHeight) {
        return (double) index / total;
    }

    private double mapYToPayoff(double yValue, int availableHeight, double maxAbsPayoff) {
        return maxAbsPayoff - (yValue * 2 * maxAbsPayoff);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Long Straddle Payoff");
        LongStraddlePayoff straddleGraph = new LongStraddlePayoff(150.0, 5.0, 3.0, "2024-12-20");
        frame.add(straddleGraph);
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}