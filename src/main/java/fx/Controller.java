package fx;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    Configuration configuration = new Configuration();
    boolean recognitionEnabled = false;
    LiveSpeechRecognizer recognizer;
    SpeechResult result;

    @FXML
    Button recordButton, clearButton;

    @FXML
    GridPane grid;
    @FXML
    TextArea logArea;

    @FXML
    ImageView nextPlayerImage;

    int[][] game = new int[3][3];
    int currentPlayer = 1;
    Image xImage, oImage;

    public void initialize(URL location, ResourceBundle resources) {

        configuration.setAcousticModelPath("resource:/adapt_model/en-us");
        configuration.setDictionaryPath("resource:/adapt_model/gen.dict");
        configuration.setLanguageModelPath("resource:/adapt_model/gen.lm");

        logArea.textProperty().addListener(new ChangeListener<Object>() {
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                logArea.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
            }
        });

        try {
            recognizer = new LiveSpeechRecognizer(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }


        xImage = new Image("X.png");
        oImage = new Image("O.png");

        nextPlayerImage.setImage(oImage);

    }

    @FXML
    void startRecord() throws LineUnavailableException {
        recognitionEnabled = true;
        recordButton.setDisable(true);

        new Thread(new Runnable() {
            public void run() {

                recognizer.startRecognition(true);
                log("Start recognition... ", 0);
                while ((result = recognizer.getResult()) != null && recognitionEnabled) {
                    System.out.println(result.getHypothesis());
                    log("Input: " + result.getHypothesis(), 0);
                    command(result.getHypothesis());
                }
                recognizer.stopRecognition();
                log("End of recognition.", 0);
                Platform.runLater(new Runnable() {
                    public void run() {
                        recordButton.setDisable(false);

                        try {
                            recognizer = new LiveSpeechRecognizer(configuration);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    @FXML
    void clear() {
        logArea.setText("");
    }

    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }


    void log(String s, int level) {
        String indent = "";
        for (int i = 0; i < level; i++) {
            indent += "\t";
        }
        logArea.appendText("\n" + indent + s);
        logArea.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom

    }

    void command(String input) {
        int x = 1, y = 1;
        if (input.contains("new game")) {
            log("Command: New game", 1);
            newGame();
            return;
        } else if (input.contains("place a mark")) {
            log("Command: Mark", 1);
            if (input.contains("center")) {
                x = 1;
                y = 1;
            } else {
                if (input.contains("top")) {
                    x = 0;
                } else if (input.contains("bottom")) {
                    x = 2;
                }

                if (input.contains("left")) {
                    y = 0;
                } else if (input.contains("right")) {
                    y = 2;
                }
            }

            if (!input.contains("center") && x == 1 && y == 1) {
                log("No valid field.\n", 2);
                return;
            }

            log("X = " + x, 2);
            log("Y = " + y, 2);

            mark(x, y);
        } else {
            log("Command not valid.\n", 1);
        }
    }

    private void mark(final int x, final int y) {
        if (game[x][y] != 0) {
            log("Field taken!", 3);
            return;
        } else {
            game[x][y] = currentPlayer;

            final ImageView mark = new ImageView();
            mark.setFitHeight(100);
            mark.setFitWidth(100);
            mark.setImage(currentPlayer == 1 ? oImage : xImage);
            Platform.runLater(new Runnable() {
                public void run() {
                    grid.add(mark, y, x);
                }
            });
            log("Player " + (currentPlayer == 1 ? "O" : "X") + " made a move.", 0);
            currentPlayer = currentPlayer == 1 ? 2 : 1;
            Platform.runLater(new Runnable() {
                public void run() {
                    nextPlayerImage.setImage(currentPlayer == 1 ? oImage : xImage);
                }
            });
        }
        logGameState();
    }

    private void logGameState() {
        log("------------------------------------------", 0);
        log("Game state:", 0);
        for (int i = 0; i < 3; i++) {
            String line = "";
            for (int j = 0; j < 3; j++) {
                if (game[i][j] == 1) line += "O ";
                if (game[i][j] == 2) line += "X ";
                if (game[i][j] == 0) line += "_ ";
            }
            log(line, 1);

        }
        log("Next player is " + (currentPlayer == 1 ? "O.\n" : "X.\n"), 0);
    }


    private void newGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                game[i][j] = 0;
            }
        }
        currentPlayer = 1;
        Platform.runLater(new Runnable() {
            public void run() {
                grid.getChildren().clear();
                nextPlayerImage.setImage(oImage);
            }
        });

        log("\n\n\nNew game started.", 0);

        logGameState();
    }
}
