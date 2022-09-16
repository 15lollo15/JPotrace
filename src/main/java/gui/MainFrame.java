package gui;

import potrace.TurnPolicy;

import javax.swing.*;

public class MainFrame extends JFrame{
    private JPanel mainPanel;
    private JTextField srcImageField;
    private JButton chooseSourceImageButton;
    private JTextField destinationSvgField;
    private JButton chooseDestinationSvgButton;
    private JSpinner scaleSpinner;
    private JTabbedPane conversionModePane;
    private JSlider thresholdSlider;
    private JTextField thresholdField;
    private JSpinner colorNumberSpinner;
    private JTextArea logTextArea;
    private JButton startConversionButton;
    private JComboBox<TurnPolicy> turnPolicyComboBox;
    private JSpinner minAreaSpinner;
    private JRadioButton enableCurveOptimization;
    private JRadioButton disableCurveOptimization;
    private JPanel advancePanel;
    private JScrollPane logAreaScrollPane;
    private static MainFrame mainFrame;

    private MainFrame() {
        super();
        createUIComponents();
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(enableCurveOptimization);
        buttonGroup.add(disableCurveOptimization);
    }

    public static MainFrame getInstance() {
        if (mainFrame == null)
            mainFrame = new MainFrame();
        return mainFrame;
    }

    public JPanel getAdvancePanel() {
        return advancePanel;
    }

    public JTextArea getLogTextArea() {
        return logTextArea;
    }

    public JSlider getThresholdSlider() {
        return thresholdSlider;
    }

    public JTextField getThresholdField() {
        return thresholdField;
    }

    public JTextField getSrcImageField() {
        return srcImageField;
    }

    public JTextField getDestinationSvgField() {
        return destinationSvgField;
    }

    public JComboBox<TurnPolicy> getTurnPolicyComboBox() {
        return turnPolicyComboBox;
    }

    public JSpinner getScaleSpinner() {
        return scaleSpinner;
    }

    public JSpinner getMinAreaSpinner() {
        return minAreaSpinner;
    }

    public JRadioButton getEnableCurveOptimization() {
        return enableCurveOptimization;
    }

    public JRadioButton getDisableCurveOptimization() {
        return disableCurveOptimization;
    }

    public JSpinner getColorNumberSpinner() {
        return colorNumberSpinner;
    }

    public JButton getChooseSourceImageButton() {
        return chooseSourceImageButton;
    }

    public JButton getChooseDestinationSvgButton() {
        return chooseDestinationSvgButton;
    }

    public JTabbedPane getConversionModePane() {
        return conversionModePane;
    }

    public JButton getStartConversionButton() {
        return startConversionButton;
    }

    public JScrollPane getLogAreaScrollPane() {
        return logAreaScrollPane;
    }

    private void createUIComponents() {}
}
