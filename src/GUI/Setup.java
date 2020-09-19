package GUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import Engine.Player.Player.PlayerType;

//Boilerplate
public class Setup extends JDialog {

    private PlayerType config;
    private int sliderValue;

    public Setup(final JFrame parent, final Table currentTable){
        super(parent, "Setup", true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(new Dimension(250,250));
        setLayout(new BorderLayout());
        add(new ButtonPanel(this), BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private final class ButtonPanel extends JPanel implements ChangeListener {

        protected JLabel label;
        protected JSlider slider;

        public ButtonPanel(final JDialog parent){
            setPreferredSize(new Dimension(150,150));
            setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.NORTH;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            JRadioButton humanVComputer = new JRadioButton("Human vs. Computer", true);
            JRadioButton computerVComputer = new JRadioButton("Computer vs. Computer", false);
            JButton okButton = new JButton("OK");
            this.slider = new DifficultySlider(2);
            this.label = new JLabel("Difficulty: " + slider.getValue());
            sliderValue = slider.getValue();
            slider.addChangeListener(this);
            JPanel sliderPanel = new JPanel();
            sliderPanel.setLayout(new BorderLayout());
            sliderPanel.add(label, BorderLayout.NORTH);
            sliderPanel.add(slider, BorderLayout.SOUTH);
            humanVComputer.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    computerVComputer.setSelected(false);
                }
            });
            computerVComputer.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    humanVComputer.setSelected(false);
                }
            });
            okButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(computerVComputer.isSelected())
                        config = PlayerType.COMPUTER;
                    else if(humanVComputer.isSelected())
                        config = PlayerType.USER;
                    parent.dispose();
                }
            });
            parent.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(WindowEvent e){
                }
            });
            add(humanVComputer, constraints);
            add(computerVComputer, constraints);
            add(sliderPanel, constraints);
            add(okButton, constraints);
            setVisible(true);
            parent.setResizable(false);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            label.setText("Difficulty: " + slider.getValue());
            sliderValue = slider.getValue();
        }

    }

    public static final class DifficultySlider extends JSlider {

        public DifficultySlider(final int value){
            super(1, 3, value);
            setPaintTrack(true);
            setPaintTicks(true);
            setMajorTickSpacing(10);
            setMinorTickSpacing(1);
        }

    }

    public PlayerType getConfig(){
        return config;
    }

    public int getSliderValue(){
        return sliderValue;
    }

    public void setConfig(PlayerType config){
        this.config = config;
    }

}
