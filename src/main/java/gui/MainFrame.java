package gui;

import tracing.base.TurnPolicy;

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
    private JSpinner blurSpinner;
    private JCheckBox pixelArtCheckBox;
    private JCheckBox numberOfColorsCheckBox;
    private JCheckBox paletteSimplificationCheckBox;
    private ButtonGroup curveOptimizationButtonGroup;
    private static MainFrame mainFrame;
    private MainFrame() {
        super();
        createUIComponents();
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        curveOptimizationButtonGroup = new ButtonGroup();
        curveOptimizationButtonGroup.add(enableCurveOptimization);
        curveOptimizationButtonGroup.add(disableCurveOptimization);
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

    public JSpinner getBlurSpinner() { return blurSpinner; }

    public JCheckBox getPixelArtCheckBox() {
        return pixelArtCheckBox;
    }

    public JCheckBox getNumberOfColorsCheckBox() {
        return numberOfColorsCheckBox;
    }

    public JCheckBox getPaletteSimplificationCheckBox() {
        return paletteSimplificationCheckBox;
    }

    public JRadioButton getDisableCurveOptimization() {
        return disableCurveOptimization;
    }

    private void createUIComponents() {
        // Do nothing
    }
}
