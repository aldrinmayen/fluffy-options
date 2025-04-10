import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class OptionStrategyVisualizer extends JFrame implements ActionListener {

    private JComboBox<String> strategySelector;
    private JPanel inputPanel;
    private JPanel chartPanelContainer; // Top 3/4 for the chart
    private JPanel controlPanel;       // Bottom 1/4 for inputs and button
    private CardLayout inputCardLayout;
    private CardLayout chartCardLayout;
    private LongCallPayoff longCallGraph;
    private LongPutPayoff longPutGraph;
    private LongStraddlePayoff longStraddleGraph;

    private JTextField callStrikeField;
    private JTextField callPremiumField;
    private JTextField callExpirationField;

   private JTextField putStrikeField;
    private JTextField putPremiumField;
    private JTextField putExpirationField;

    private JTextField straddleStrikeField;
    private JTextField straddleCallPremiumField;
    private JTextField straddlePutPremiumField;
    private JTextField straddleExpirationField;

    private JButton updateChartButton;

    public OptionStrategyVisualizer() {
        setTitle("Option Strategy Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Chart Panel Container (Top 3/4)
        chartPanelContainer = new JPanel(new CardLayout());
        add(chartPanelContainer, BorderLayout.CENTER); // Center gets the larger portion
        chartCardLayout = (CardLayout) chartPanelContainer.getLayout();

        // Initial empty chart panels
        longCallGraph = new LongCallPayoff(0, 0, "");
        longPutGraph = new LongPutPayoff(0, 0, "");
        longStraddleGraph = new LongStraddlePayoff(0, 0, 0, "");
        chartPanelContainer.add(longCallGraph, "Long Call");
        chartPanelContainer.add(longPutGraph, "Long Put");
        chartPanelContainer.add(longStraddleGraph, "Long Straddle");
        chartCardLayout.show(chartPanelContainer, "Long Call"); // Initial display

        // Control Panel (Bottom 1/4)
        controlPanel = new JPanel(new BorderLayout());
        add(controlPanel, BorderLayout.SOUTH);

        // Strategy Selection (within Control Panel)
        JPanel strategySelectPanel = new JPanel();
        strategySelectPanel.setBorder(new TitledBorder("Select Strategy"));
        String[] strategies = {"Long Call", "Long Straddle", "Long Put"};
        strategySelector = new JComboBox<>(strategies);
        strategySelectPanel.add(strategySelector);
        controlPanel.add(strategySelectPanel, BorderLayout.NORTH);

        // Input Panel (within Control Panel)
        inputPanel = new JPanel(new CardLayout());
        controlPanel.add(inputPanel, BorderLayout.CENTER);
        inputCardLayout = (CardLayout) inputPanel.getLayout();

        // Long Call Input Fields
        JPanel longCallInput = new JPanel(new GridLayout(3, 2, 5, 5));
        longCallInput.setBorder(new TitledBorder("Long Call Parameters"));
        longCallInput.add(new JLabel("Strike Price:"));
        callStrikeField = new JTextField();
        longCallInput.add(callStrikeField);
        longCallInput.add(new JLabel("Premium:"));
        callPremiumField = new JTextField();
        longCallInput.add(callPremiumField);
        longCallInput.add(new JLabel("Expiration Date (YYYY-MM-DD):"));
        callExpirationField = new JTextField();
        longCallInput.add(callExpirationField);
        inputPanel.add(longCallInput, "Long Call");

        // Long Put Input Fields
        JPanel longPutInput = new JPanel(new GridLayout(3, 2, 5, 5));
        longPutInput.setBorder(new TitledBorder("Long Put Parameters"));
        longPutInput.add(new JLabel("Strike Price:"));
        putStrikeField = new JTextField();
        longPutInput.add(putStrikeField);
        longPutInput.add(new JLabel("Premium:"));
        putPremiumField = new JTextField();
        longPutInput.add(putPremiumField);
        longPutInput.add(new JLabel("Expiration Date (YYYY-MM-DD):"));
        putExpirationField = new JTextField();
        longPutInput.add(putExpirationField);
        inputPanel.add(longPutInput, "Long Put");

        // Long Straddle Input Fields
        JPanel longStraddleInput = new JPanel(new GridLayout(4, 2, 5, 5));
        longStraddleInput.setBorder(new TitledBorder("Long Straddle Parameters"));
        longStraddleInput.add(new JLabel("Strike Price:"));
        straddleStrikeField = new JTextField();
        longStraddleInput.add(straddleStrikeField);
        longStraddleInput.add(new JLabel("Call Premium:"));
        straddleCallPremiumField = new JTextField();
        longStraddleInput.add(straddleCallPremiumField);
        longStraddleInput.add(new JLabel("Put Premium:"));
        straddlePutPremiumField = new JTextField();
        longStraddleInput.add(straddlePutPremiumField);
        longStraddleInput.add(new JLabel("Expiration Date (YYYY-MM-DD):"));
        straddleExpirationField = new JTextField();
        longStraddleInput.add(straddleExpirationField);
        inputPanel.add(longStraddleInput, "Long Straddle");

        // Update Button (within Control Panel)
        updateChartButton = new JButton("Update Chart");
        updateChartButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(updateChartButton);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Event Listener for Strategy Selection
        strategySelector.addActionListener(e -> {
            inputCardLayout.show(inputPanel, (String) strategySelector.getSelectedItem());
            chartCardLayout.show(chartPanelContainer, (String) strategySelector.getSelectedItem());
        });

        setSize(800, 700); // Adjust size as needed
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == updateChartButton) {
            String selectedStrategy = (String) strategySelector.getSelectedItem();
            try {
                if (selectedStrategy.equals("Long Call")) {
                    double strikePrice = Double.parseDouble(callStrikeField.getText());
                    double premium = Double.parseDouble(callPremiumField.getText());
                    String expirationDate = callExpirationField.getText();
                    longCallGraph.updateParameters(strikePrice, premium, expirationDate);
                } else if (selectedStrategy.equals("Long Straddle")) {
                    double strikePrice = Double.parseDouble(straddleStrikeField.getText());
                    double callPremium = Double.parseDouble(straddleCallPremiumField.getText());
                    double putPremium = Double.parseDouble(straddlePutPremiumField.getText());
                    String expirationDate = straddleExpirationField.getText();
                    longStraddleGraph.updateParameters(strikePrice, callPremium, putPremium, expirationDate);
                } else if (selectedStrategy.equals("Long Put")){
                    double strikePrice = Double.parseDouble(putStrikeField.getText());
                    double Premium = Double.parseDouble(putPremiumField.getText());
                    String expirationDate = putExpirationField.getText();
                    longPutGraph.updateParameters(strikePrice, Premium, expirationDate);
                }
                chartPanelContainer.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers for prices.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OptionStrategyVisualizer::new);
    }
}