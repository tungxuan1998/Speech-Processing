package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.result.WordResult;

public class frmMain {
    private JPanel panel1;
    private JButton playButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JTextField didYouMeanTextField;
    private JPanel pnlGIF;
    private JButton confirmButton;

    private LiveSpeechRecognizer recognizer;
    private Logger logger = Logger.getLogger(getClass().getName());
    private String speechRecognitionResult;
    private boolean ignoreSpeechRecognitionResults = false;
    private boolean speechRecognizerThreadRunning = false;
    private boolean resourcesThreadRunning;
    private ExecutorService eventsExecutorService = Executors.newFixedThreadPool(2);

    private String word;
    private ImageIcon icon;
    private JLabel l;

    private void add(JLabel label) {
        pnlGIF.add(label);
    }

    public frmMain() {
        logger.log(Level.INFO, "Loading Speech Recognizer...\n");

        // Configuration
        Configuration configuration = new Configuration();

        // Load model from the jar
        configuration.setAcousticModelPath("Resources/Libraries/Sphinx data/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("Resources/Libraries/Sphinx data/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");

//        configuration.setLanguageModelPath("Resources/Libraries/Sphinx data/edu/cmu/sphinx/models/en-us/en-us.lm.bin");

        // Grammar
        configuration.setGrammarPath("Resources/grammars");
        configuration.setGrammarName("grammar");
        configuration.setUseGrammar(true);

        try {
            recognizer = new LiveSpeechRecognizer(configuration);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        startResourcesThread();
        startSpeechRecognition();

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                icon = new ImageIcon("C:\\Users\\Admin\\Desktop\\Video\\" + didYouMeanTextField.getText() + ".gif");
                l.setIcon(icon);
            }
        });
    }

    public synchronized void startSpeechRecognition() {
        //Check lock
        if (speechRecognizerThreadRunning)
            logger.log(Level.INFO, "Speech Recognition Thread already running...\n");
        else
            eventsExecutorService.submit(() -> {

                speechRecognizerThreadRunning = true;
                ignoreSpeechRecognitionResults = false;

                recognizer.startRecognition(true);

                logger.log(Level.INFO, "You can start to speak...\n");

                try {
                    while (speechRecognizerThreadRunning) {
                        SpeechResult speechResult = recognizer.getResult();

                        if (!ignoreSpeechRecognitionResults) {
                            if (speechResult == null)
                                logger.log(Level.INFO, "I can't understand what you said.\n");
                            else {
                                speechRecognitionResult = speechResult.getHypothesis();

                                System.out.println("You said: [" + speechRecognitionResult + "]\n");
                                word = speechRecognitionResult;
                                icon = new ImageIcon("C:\\Users\\Admin\\Desktop\\Video\\" + word + ".gif");
                                l.setIcon(icon);
                                didYouMeanTextField.setText(speechRecognitionResult);
                                makeDecision(speechRecognitionResult, speechResult.getWords());
                            }
                        } else
                            logger.log(Level.INFO, "Ignoring Speech Recognition Results...");
                    }
                } catch (Exception ex) {
                    logger.log(Level.WARNING, null, ex);
                    speechRecognizerThreadRunning = false;
                }

                logger.log(Level.INFO, "SpeechThread has exited...");
            });
    }

    public synchronized void stopIgnoreSpeechRecognitionResults() {
        //Stop ignoring speech recognition results
        ignoreSpeechRecognitionResults = false;
    }

    public synchronized void ignoreSpeechRecognitionResults() {
        //Instead of stopping the speech recognition we are ignoring it's results
        ignoreSpeechRecognitionResults = true;
    }

    public void startResourcesThread() {

        //Check lock
        if (resourcesThreadRunning)
            logger.log(Level.INFO, "Resources Thread already running...\n");
        else
            //Submit to ExecutorService
            eventsExecutorService.submit(() -> {
                try {

                    //Lock
                    resourcesThreadRunning = true;

                    // Detect if the microphone is available
                    while (true) {

                        //Is the Microphone Available
                        if (!AudioSystem.isLineSupported(Port.Info.MICROPHONE))
                            logger.log(Level.INFO, "Microphone is not available.\n");

                        // Sleep some period
                        Thread.sleep(350);
                    }

                } catch (InterruptedException ex) {
                    logger.log(Level.WARNING, null, ex);
                    resourcesThreadRunning = false;
                }
            });
    }

    public void makeDecision(String speech , List<WordResult> speechWords) {
//        System.out.println(speech);
    }



    public boolean getIgnoreSpeechRecognitionResults() {
        return ignoreSpeechRecognitionResults;
    }

    public boolean getSpeechRecognizerThreadRunning() {
        return speechRecognizerThreadRunning;
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("ASL");
        frame.setContentPane(new frmMain().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
        frame.setSize(480, 300);

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        String arg = "C:\\Users\\Sony\\Desktop\\Video\\red.gif";
        icon = new ImageIcon(arg);
        l = new JLabel();
        l.setIcon(icon);
        pnlGIF = new JPanel();
        pnlGIF.add(l);

    }
}