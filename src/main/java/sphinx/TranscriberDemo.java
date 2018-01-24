package sphinx;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import fx.Main;
import org.apache.commons.io.FileUtils;

import javax.sound.sampled.*;

public class TranscriberDemo {

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();

        configuration.setAcousticModelPath("resource:/adapt_model/en-us");
        configuration.setDictionaryPath("resource:/adapt_model/gen.dict");
        configuration.setLanguageModelPath("resource:/adapt_model/gen.lm");
/*
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
*/
      /*   StreamSpeechRecognizer recognizer = new  StreamSpeechRecognizer(configuration);

        //   InputStream stream = new FileInputStream(FileUtils.toFile(TranscriberDemo.class.getResource("/adapt_model/adapt_002.wav")));
        InputStream stream = new FileInputStream(FileUtils.toFile(TranscriberDemo.class.getResource("/test.wav")));

        recognizer.startRecognition(stream);
*/
        LiveSpeechRecognizer recognizer = new  LiveSpeechRecognizer(configuration);

        recognizer.startRecognition(true);
        SpeechResult result;
         while ((result = recognizer.getResult()) != null) {
            System.out.println(result.getHypothesis());
        }
        recognizer.stopRecognition();
        System.out.println("Stopped.");
    }


}