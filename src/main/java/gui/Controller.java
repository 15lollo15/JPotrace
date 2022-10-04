package gui;

import tracing.Settings;
import tracing.TurnPolicy;
import workers.BlackAndWhiteWorker;
import workers.ColorWorker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {
    public static final String MAIN_FRAME_TITLE = "JTracing";
    private static Controller controller;

    private final MainFrame mainFrame;

    private Controller() {
        mainFrame = MainFrame.getInstance();
        setup();
        setupListeners();
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Mode");

        menu.add(new JCheckBoxMenuItem ("Basic"));
        menu.add(new JCheckBoxMenuItem ("Advance"));
        ((JCheckBoxMenuItem)menu.getMenuComponent(0)).setState(true);
        menuBar.add(menu);
        mainFrame.setJMenuBar(menuBar);
    }

    private void setup() {
        mainFrame.setTitle(MAIN_FRAME_TITLE);
        setupMenuBar();

        mainFrame.getAdvancePanel().setVisible(false);
        mainFrame.pack();

        mainFrame.getThresholdField().setEnabled(false);
        mainFrame.getThresholdField().setText(Integer.toString(128));
        mainFrame.getThresholdSlider().setMaximum(255);
        mainFrame.getThresholdSlider().setValue(128);

        mainFrame.getSrcImageField().setEnabled(false);
        mainFrame.getDestinationSvgField().setEnabled(false);

        for (TurnPolicy tp : TurnPolicy.values())
            mainFrame.getTurnPolicyComboBox().addItem(tp);

        mainFrame.getScaleSpinner().setModel(new SpinnerNumberModel(1, 1, 10, 1));
        mainFrame.getMinAreaSpinner().setModel(new SpinnerNumberModel(2, 0, Integer.MAX_VALUE, 1));

        mainFrame.getEnableCurveOptimization().setSelected(true);
        mainFrame.getColorNumberSpinner().setModel(new SpinnerNumberModel(10, 1, 256*256*256, 1));

        mainFrame.getBlurSpinner().setModel(new SpinnerNumberModel(1, 1, 13, 2));

        mainFrame.getLogTextArea().setEnabled(false);
        mainFrame.getLogAreaScrollPane().setEnabled(false);
    }

    public void disableAll(boolean yes) {
        mainFrame.getChooseSourceImageButton().setEnabled(!yes);
        mainFrame.getChooseDestinationSvgButton().setEnabled(!yes);
        mainFrame.getScaleSpinner().setEnabled(!yes);
        mainFrame.getConversionModePane().setEnabled(!yes);

        mainFrame.getThresholdSlider().setEnabled(!yes);
        mainFrame.getColorNumberSpinner().setEnabled(!yes);

        mainFrame.getStartConversionButton().setEnabled(!yes);
    }

    private void setupListeners() {
        mainFrame.getThresholdSlider().addChangeListener(e -> updateThresholdField());

        JMenu menu = mainFrame.getJMenuBar().getMenu(0);

        JCheckBoxMenuItem basicCheckBox = (JCheckBoxMenuItem)menu.getItem(0);
        JCheckBoxMenuItem advanceCheckBox = (JCheckBoxMenuItem)menu.getItem(1);


        basicCheckBox.addActionListener(e -> setMode(0));
        advanceCheckBox.addActionListener(e -> setMode(1));

        mainFrame.getChooseSourceImageButton().addActionListener(e -> chooseSourceImage());
        mainFrame.getChooseDestinationSvgButton().addActionListener(e -> chooseDestinationSVG());

        mainFrame.getStartConversionButton().addActionListener(e -> startConversion());
        mainFrame.getBlurSpinner().addChangeListener(e -> checkIfOdd());

        mainFrame.getPixelArtCheckBox().addChangeListener(e -> pixelArtListener());
    }

    private void pixelArtListener() {
        boolean selected = mainFrame.getPixelArtCheckBox().isSelected();
        if (selected) {
            //TODO: Turn off other settings
        } else {
            //TODO: Turn on other settings
        }
    }

    private void checkIfOdd(){
        int value = (Integer)mainFrame.getBlurSpinner().getValue();
        mainFrame.getBlurSpinner().setValue(value % 2 == 0 ? value + 1 : value);
    }

    private void setMode(int modeIndex) {
        JMenu menu = mainFrame.getJMenuBar().getMenu(0);
        for (int i = 0; i < menu.getItemCount(); i++) {
            JCheckBoxMenuItem checkBoxMenuItem = (JCheckBoxMenuItem) menu.getItem(i);
            checkBoxMenuItem.setState(i == modeIndex);
        }
        mainFrame.getAdvancePanel().setVisible(modeIndex != 0);
        mainFrame.pack();
    }

    private void chooseSourceImage() {
        JFileChooser fileChooser = new JFileChooser();
        if (!mainFrame.getSrcImageField().getText().isEmpty())
            fileChooser.setCurrentDirectory(new File(mainFrame.getSrcImageField().getText()));

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image", ImageIO.getReaderFileSuffixes()));
        int r = fileChooser.showOpenDialog(mainFrame);
        if (r == JFileChooser.APPROVE_OPTION) {
            mainFrame.getSrcImageField().setText(fileChooser.getSelectedFile().getPath());
        }
    }

    private void chooseDestinationSVG() {
        JFileChooser fileChooser = new JFileChooser();

        if (!mainFrame.getDestinationSvgField().getText().isEmpty())
            fileChooser.setCurrentDirectory(new File(mainFrame.getDestinationSvgField().getText()));

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("SVG", "svg"));
        int r = fileChooser.showSaveDialog(mainFrame);
        if (r == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getPath();
            if (!path.contains(".svg"))
                path += ".svg";
            mainFrame.getDestinationSvgField().setText(path);
        }
    }

    private void startConversion() {
        String srcPath = mainFrame.getSrcImageField().getText();
        String destPath = mainFrame.getDestinationSvgField().getText();
        if (srcPath.isEmpty() || destPath.isEmpty()){
            JOptionPane.showMessageDialog(mainFrame,
                    "You need to fill all the fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        BufferedImage input;
        try {
            input = ImageIO.read(new File(srcPath));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Error in reading process",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int scale = (Integer)mainFrame.getScaleSpinner().getValue();

        if (mainFrame.getPixelArtCheckBox().isSelected()) {
            ColorWorker colorWorker = new ColorWorker(input,
                    new File(destPath),
                    scale,
                    mainFrame.getLogTextArea());
            colorWorker.execute();
            return;
        }

        Settings settings = new Settings();
        if (mainFrame.getAdvancePanel().isVisible()) {
            settings.setTurnPolicy((TurnPolicy) mainFrame.getTurnPolicyComboBox().getSelectedItem());
            settings.setTurdSize((Integer) mainFrame.getMinAreaSpinner().getValue());
            settings.setOptimizeCurve(mainFrame.getEnableCurveOptimization().isSelected());
        }

        mainFrame.getLogTextArea().setVisible(true);
        mainFrame.pack();
        mainFrame.getLogTextArea().setText("");
        if (mainFrame.getConversionModePane().getSelectedIndex() == 0) {
            int threshold = mainFrame.getThresholdSlider().getValue();
            BlackAndWhiteWorker blackAndWhiteWorker = new BlackAndWhiteWorker(input,
                    new File(destPath),
                    scale, threshold,
                    mainFrame.getLogTextArea(), settings);
            blackAndWhiteWorker.execute();
        }else {
            int numberOfColors = (Integer)mainFrame.getColorNumberSpinner().getValue();
            int blur = (Integer)mainFrame.getBlurSpinner().getValue();
            ColorWorker colorWorker = new ColorWorker(input,
                    new File(destPath),
                    scale, numberOfColors,
                    mainFrame.getLogTextArea(), blur, settings);
            colorWorker.execute();
        }
    }

    private void updateThresholdField() {
        int sliderValue = mainFrame.getThresholdSlider().getValue();
        mainFrame.getThresholdField().setText(Integer.toString(sliderValue));
    }

    public static Controller getInstance() {
        if (controller == null)
            controller = new Controller();
        return controller;
    }

    public void showWindow() {
        mainFrame.setVisible(true);
    }


}
