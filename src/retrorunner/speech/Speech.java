package retrorunner.speech;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;


public class Speech
{
    // Variables
    private String result;

    // Threads
    Thread speechThread;
    Thread resourcesThread;
    // LiveRecognizer
    private LiveSpeechRecognizer recognizer;

    public String r = null;

    public Speech()
    {

        // Loading Message
        System.out.println("Loading..\n");

        // Configuration
        Configuration configuration = new Configuration();

        // Load model from the jar
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");

        // if you want to use LanguageModelPath disable the 3 lines after which
        // are setting a custom grammar->
        // configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin")
        // Grammar
        configuration.setGrammarPath("grammars");
        configuration.setGrammarName("grammar");
        configuration.setUseGrammar(true);

        try {
            recognizer = new LiveSpeechRecognizer(configuration);
        } catch (IOException ex) {
            System.out.println(ex);
        }

        // Start recognition process pruning previously cached data.
        recognizer.startRecognition(true);

        // Start the Thread
        startSpeechThread();
        startResourcesThread();
    }

    /**
     * Starting the main Thread of speech recognition
     */
    protected void startSpeechThread()
    {

        // alive?
        if (speechThread != null && speechThread.isAlive()) {
            return;
        }

        // initialise
        speechThread = new Thread(() -> {
            System.out.println("You can start to speak...\n");
            try {
                while (true) {
                    /*
                     * This method will return when the end of speech is
                     * reached. Note that the end pointer will determine the end
                     * of speech.
                     */
                    SpeechResult speechResult = recognizer.getResult();
                    if (speechResult != null) {

                        result = speechResult.getHypothesis();
                        //System.out.println("You said: [" + result + "]\n");
                        // logger.log(Level.INFO, "You said: " + result + "\n")
                        r = result.toString();

                    } else {
                        System.out.println("I can't understand what you said.\n");
                    }

                }
            } catch (Exception ex) {
                System.out.println(ex);;
            }

            System.out.println("SpeechThread has exited...");
        });

        // Start
        speechThread.start();

    }

    /**
     * Starting a Thread that checks if the resources needed to the
     * SpeechRecognition library are available
     */
    protected void startResourcesThread()
    {

        // alive?
        if (resourcesThread != null && resourcesThread.isAlive()) {
            return;
        }

        resourcesThread = new Thread(() -> {
            try {

                // Detect if the microphone is available
                while (true) {
                    if (AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
                        // logger.log(Level.INFO, "Microphone is available.\n")
                    } else {
                        // logger.log(Level.INFO, "Microphone is not
                        // available.\n")

                    }

                    // Sleep some period
                    Thread.sleep(350);
                }

            } catch (InterruptedException ex) {
                System.out.println(ex);
                resourcesThread.interrupt();
            }
        });

        // Start
        resourcesThread.start();
    }
}
