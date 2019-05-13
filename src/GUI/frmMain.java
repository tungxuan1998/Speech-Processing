package GUI;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

public class frmMain {
    private JPanel panel1;
    private JButton playButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JTextField didYouMeanTextField;
    private JPanel pnlGIF;

    private void add(JLabel label) {
        pnlGIF.add(label);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ASL");
        frame.setContentPane(new frmMain().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
        frame.setSize(480, 360);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        String arg = "C:\\Users\\Sony\\Desktop\\Video\\red.gif";
        ImageIcon icon = new ImageIcon(arg);
        JLabel l = new JLabel();
        l.setIcon(icon);
        pnlGIF = new JPanel();
        pnlGIF.add(l);
    }
}