import java.awt.*;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import javax.swing.*;

public class LongPutPayoff extends JPanel {

    private double strikePrice;
    private double putPrice;
    private String expirationDate;
    private final DecimalFormat df = new DecimalFormat("#.##");

    public LongPutPayoff(double strikePrice, double putPrice, String expirationDate) {
        this.strikePrice = strikePrice;
        this.putPrice = putPrice;
        this.expirationDate = expirationDate;
    }

    public void updateParameters(double strikePrice, double putPrice, String expirationDate) {
        this.strikePrice = strikePrice;
        this.putPrice = putPrice;
        this.expirationDate = expirationDate;
        repaint();
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
        g2d.drawString("Long Put - Expiration: " + expirationDate + ", Strike: $" + df.format(strikePrice) + ", Premium: $" + df.format(putPrice), 10, 20);

        // Draw payoff area
        drawPayoffArea(g2d, width, height, padding, zeroY);

        // Draw strike price line and label
        g2d.setColor(Color.GRAY);
        double startPrice = 0;
        double endPrice = strikePrice * 2;
        int strikeX = calculateX(strikePrice, width - 2 * padding, startPrice, endPrice) + padding;
        g2d.draw(new Line2D.Double(strikeX, padding, strikeX, height - padding));
        g2d.drawString("Strike: $" + df.format(strikePrice), strikeX - 25, zeroY - 15);

        // Draw break-even point
        double breakEvenPrice = strikePrice - putPrice;
        int breakEvenX = calculateX(breakEvenPrice, width - 2 * padding, startPrice, endPrice) + padding;
        g2d.setColor(Color.BLUE);
        g2d.fillOval(breakEvenX - 3, zeroY - 3, 6, 6);
        g2d.drawString("BE: $" + df.format(breakEvenPrice), breakEvenX + 5, zeroY + 15);

        // Draw vertical profit/loss values
        int numYLabs = 5;
        double maxAbsPayoff = Math.max(strikePrice, Math.abs(putPrice) * 2) + 10; // Adjust scaling
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
            double priceValue = xValue * (strikePrice * 2); // Use the same end price as other charts
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
            double currentPayoff = calculatePayoff(currentUnderlyingPrice);
            int currentY = calculateYCoordinate(currentPayoff, panelHeight, padding);
            System.out.println("Price: " + currentUnderlyingPrice + ", Payoff: " + currentPayoff + ", Y: " + currentY);
            if (currentPayoff >= 0) {
                g2d.setColor(new Color(0, 255, 0, 100));
                g2d.fillRect(x, Math.min(currentY, zeroY), 1, Math.abs(currentY - zeroY));
            } else {
                g2d.setColor(new Color(255, 0, 0, 100));
                g2d.fillRect(x, Math.min(currentY, zeroY), 1, Math.abs(currentY - zeroY));
            }
            g2d.setColor(Color.RED); // Use red for the Long Put payoff line
            g2d.draw(new Line2D.Double(x, currentY, x + 1, currentY));
        }
    }

    private double calculatePayoff(double currentUnderlyingPrice) {
        return Math.max(0, strikePrice - currentUnderlyingPrice) - putPrice; // Inversed payoff
    }

    private int calculateX(double price, int availableWidth, double minPrice, double maxPrice) {
        return (int) (((price - minPrice) / (maxPrice - minPrice)) * availableWidth);
    }

    private int calculateYCoordinate(double payoff, int panelHeight, int padding) {
        double maxAbsPayoff = Math.max(strikePrice, Math.abs(putPrice) * 2) + 10; // Adjust scaling
        return padding + (int) ((maxAbsPayoff - payoff) / (2 * maxAbsPayoff) * (panelHeight - 2 * padding));
       // double maxPotentialProfit = strikePrice; // Theoretical max profit if price goes to 0
       // double maxPotentialLoss = putPrice;
       // double scaleFactor = (panelHeight - 2 * padding) / (maxPotentialProfit + maxPotentialLoss);
       // return (int) (padding + maxPotentialProfit * scaleFactor - payoff * scaleFactor);
    }

    private double calculateYValue(int index, int total, int availableHeight) {
        return (double) index / total;
    }

    private double mapYToPayoff(double yValue, int availableHeight, double maxAbsPayoff) {
        return maxAbsPayoff - (yValue * 2 * maxAbsPayoff);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Long Put Payoff");
        LongPutPayoff longPutGraph = new LongPutPayoff(150.0, 3.0, "2024-12-20");
        frame.add(longPutGraph);
        frame.setSize(800, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}