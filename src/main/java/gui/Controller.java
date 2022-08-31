package gui;

import potrace.Info;
import potrace.TurnPolicy;
import workers.BlackAndWhiteWorker;
import workers.ColorWorker;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {
    public static final String MAIN_FRAME_TITLE = "JPotrace";
    private static Controller controller;

    private MainFrame mainFrame;

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

    }

    private void setMode(int modeIndex) {
        JMenu menu = mainFrame.getJMenuBar().getMenu(0);
        for (int i = 0; i < menu.getItemCount(); i++) {
            JCheckBoxMenuItem checkBoxMenuItem = (JCheckBoxMenuItem) menu.getItem(i);
            if (i != modeIndex)
                checkBoxMenuItem.setState(false);
            else
                checkBoxMenuItem.setState(true);
        }

        if(modeIndex == 0)
            mainFrame.getAdvancePanel().setVisible(false);
        else
            mainFrame.getAdvancePanel().setVisible(true);

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

        Info info = new Info();
        if (mainFrame.getAdvancePanel().isVisible()) {
            info.turnpolicy = (TurnPolicy) mainFrame.getTurnPolicyComboBox().getSelectedItem();
            info.turdsize = (Integer)mainFrame.getMinAreaSpinner().getValue();
            info.optcurve = mainFrame.getEnableCurveOptimization().isSelected();
        }

        mainFrame.getLogTextArea().setVisible(true);
        mainFrame.pack();
        mainFrame.getLogTextArea().setText("");
        if (mainFrame.getConversionModePane().getSelectedIndex() == 0) {
            int threshold = (Integer)mainFrame.getThresholdSlider().getValue();
            BlackAndWhiteWorker blackAndWhiteWorker = new BlackAndWhiteWorker(input,
                    new File(destPath),
                    scale, threshold,
                    mainFrame.getLogTextArea(), info);
            blackAndWhiteWorker.execute();
        }else {
            int numberOfColors = (Integer)mainFrame.getColorNumberSpinner().getValue();
            ColorWorker colorWorker = new ColorWorker(input,
                    new File(destPath),
                    scale, numberOfColors,
                    mainFrame.getLogTextArea(), info);
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
