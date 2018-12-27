//Kobe Weinstein
//WeinstienKobe2137@gmail.com
//12/17/2018
//NeuralNetInterface
//Cs17.11
//UI controller of Neural Network Interface. Creates a window where the user can draw a digit 0-9 and a NeuralNet,
//object will guess the digit the user drew. The interface also allows the user to build a new Neural net and train it to
//recognize digits.

package edu.srjc.weinstein.kobe.nninterface.ui;

import edu.srjc.weinstein.kobe.nninterface.neuralnet.NeuralNet;
import edu.srjc.weinstein.kobe.nninterface.neuralnet.data.MNISTReader;
import edu.srjc.weinstein.kobe.nninterface.neuralnet.data.NNDataSet;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class UiController implements Initializable
{
    private final File initialDir = new File("resources");
    private File weights = new File("resources/weights.txt");
    private File trainLabelFile = new File("resources/train-labels.idx1-ubyte");
    private File trainImgFile = new File("resources/train-images.idx3-ubyte");
    private File testLabelFile = new File("resources/t10k-labels.idx1-ubyte");
    private File testImgFile = new File("resources/t10k-images.idx3-ubyte");

    //The default NeuralNet
    private NeuralNet neuralNet = new NeuralNet(784, new int[]{60}, 10);

    @FXML
    private Canvas canvasInput;

    @FXML
    private Label guessOutput;

    @FXML
    private Label weightsFilename;

    @FXML
    private Button loadWeightBttn;

    @FXML
    private Label numOfNodes;

    @FXML
    private Slider hiddenLayerSlider;

    @FXML
    private Label trainImageFilename;

    @FXML
    private Label trainLabelFilename;

    @FXML
    private Label testImageFilename;

    @FXML
    private Label testLabelFilename;

    @FXML
    private Spinner<Integer> epochSpinner;

    @FXML
    private Spinner<Double> learningRateSpinner;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private Label nnAccuracy;

    @FXML
    private Button newWeightBttn;

    @FXML
    private Label guessBttnWrapper;

    @FXML
    private Button guessBttn;

    @FXML
    private Hyperlink MNISTLink;


    @FXML
    void openMNIST ()
    {
        try
        {
            Desktop.getDesktop().browse(URI.create("http://yann.lecun.com/exdb/mnist/"));
        }
        catch (Exception e)
        {
            Alert error = new Alert(Alert.AlertType.ERROR, "An error Occured. If this error persists the MNIST site can be accessed at \"http://yann.lecun.com/exdb/mnist/\"");
            error.showAndWait();
        }
        MNISTLink.setVisited(false);
    }


    //Build a new Neural net with hidden layers specified by the value of the slider.
    @FXML
    void buildNetwork ()
    {
        int[] hiddenlayer = {(int) hiddenLayerSlider.getValue()};
        neuralNet = new NeuralNet(784, hiddenlayer, 10);
    }


    //train the current Neural net using data supplied by the user.
    @FXML
    void train ()
    {

        //Confirm that all of the Files required exist.
        progressLabel.getScene().setCursor(Cursor.WAIT);
        if (testImgFile == null)
        {
            Alert err = new Alert(Alert.AlertType.ERROR, "Please, select an image data file for testing.");
            err.showAndWait();
            return;
        }
        else if (testLabelFile == null)
        {
            Alert err = new Alert(Alert.AlertType.ERROR, "Please, select an label data file for testing.");
            err.showAndWait();
            return;
        }
        else if (trainImgFile == null)
        {
            Alert err = new Alert(Alert.AlertType.ERROR, "Please, select an image data file for training.");
            err.showAndWait();
            return;
        }
        else if (trainLabelFile == null)
        {
            Alert err = new Alert(Alert.AlertType.ERROR, "Please, select an label data file for training.");
            err.showAndWait();
            return;
        }

        NeuralNet temp;
        try
        {
            temp = (NeuralNet) neuralNet.clone();
        }
        catch (CloneNotSupportedException e)
        {
            Alert error = new Alert(Alert.AlertType.ERROR, "Something went wrong with Training the network.");
            error.showAndWait();
            return;
        }
        double learningRate = learningRateSpinner.getValue();
        int totalEpochs = epochSpinner.getValue();

        //Set up a task that can be run on another thread, so that the JavaFX application thread is not frozen.
        //Example implementation: https://openjfx.io/javadoc/11/javafx.graphics/javafx/concurrent/Task.html
        Task<NeuralNet> train = new Task<>()
        {

            @Override
            protected NeuralNet call () throws Exception
            {

                updateProgress(0, totalEpochs);

                NNDataSet trainingSet;
                NNDataSet testSet;

                trainingSet = new MNISTReader(trainImgFile, trainLabelFile).getNNistSet();
                testSet = new MNISTReader(testImgFile, testLabelFile).getNNistSet();

                for (int epoch = 0; epoch < totalEpochs; )
                {
                    temp.train(1, trainingSet, learningRate);
                    double accuracy = temp.testSet(testSet);
                    epoch++;
                    updateProgress(epoch, totalEpochs);
                    updateMessage(String.format("%.2f%%", accuracy));
                }
                return temp;
            }
        };
        //Execute the task
        Thread thread = new Thread(train);
        thread.setDaemon(true);
        thread.start();

        //display current progress, and accuracy of the neural net being trained.
        progressBar.progressProperty().bind(train.progressProperty());
        progressLabel.textProperty().bind(train.progressProperty()
                .multiply(totalEpochs).asString("%.0f").concat(String.format("/%s", totalEpochs)));
        nnAccuracy.textProperty().bind(train.messageProperty());

        //If the task is successful do this.
        //Credit: James_D (https://stackoverflow.com/users/2189127/james-d)
        //https://stackoverflow.com/questions/29800410/make-javafx-application-thread-wait-for-another-thread-to-finish
        train.setOnSucceeded(new EventHandler<>()
        {
            @Override
            public void handle (WorkerStateEvent workerStateEvent)
            {
                try
                {
                    neuralNet = (NeuralNet) train.getValue().clone();
                    progressLabel.getScene().setCursor(Cursor.DEFAULT);

                    guessBttnWrapper.setDisable(true);
                    guessBttn.setDisable(false);
                    Tooltip.uninstall(guessBttnWrapper, guessBttnWrapper.getTooltip());
                    newWeightBttn.setDisable(false);

                }
                catch (CloneNotSupportedException e)
                {
                    Alert error = new Alert(Alert.AlertType.ERROR, "Something went wrong with Training the network.");
                    error.showAndWait();
                }
            }
        });

        //If the task fails the datafiles where not valid, alert the user.
        train.setOnFailed(new EventHandler<>()
        {
            @Override
            public void handle (WorkerStateEvent workerStateEvent)
            {
                progressLabel.getScene().setCursor(Cursor.DEFAULT);
                Alert error = new Alert(Alert.AlertType.ERROR, "The data files are invalid. " +
                                                               "Confirm that the files conform to the .idx format listed on the MNIST site, and that the correct files" +
                                                               " have been selected.");
                error.showAndWait();
            }
        });

    }

    @FXML
    void loadTestingImageData ()
    {
        File temp = loadFile(".idx3-ubyte", testImageFilename.getScene().getWindow());
        if (temp != null)
        {
            testImgFile = temp;
            testImageFilename.setText(temp.getName());
        }
    }

    @FXML
    void loadTestingLabelData ()
    {
        File temp = loadFile(".idx1-ubyte", testLabelFilename.getScene().getWindow());
        if (temp != null)
        {
            testLabelFile = temp;
            testLabelFilename.setText(temp.getName());
        }
    }

    @FXML
    void loadTrainingImageData ()
    {
        File temp = loadFile(".idx3-ubyte", trainLabelFilename.getScene().getWindow());
        if (temp != null)
        {
            trainImgFile = temp;
            trainImageFilename.setText(temp.getName());
        }
    }

    @FXML
    void loadTrainingLabelData ()
    {
        File temp = loadFile(".idx1-ubyte", trainLabelFilename.getScene().getWindow());
        if (temp != null)
        {
            trainLabelFile = temp;
            trainLabelFilename.setText(temp.getName());
        }
    }

    @FXML
    void loadWeights ()
    {
        File temp = loadFile(".txt", weightsFilename.getScene().getWindow());
        if (temp != null)
        {
            weights = temp;
            weightsFilename.setText(temp.getName());

            try
            {
                neuralNet.loadweights(weights);

                guessBttnWrapper.setDisable(true);
                guessBttn.setDisable(false);
                Tooltip.uninstall(guessBttnWrapper, guessBttnWrapper.getTooltip());

            }
            catch (Exception e)
            {
                if (e.getClass() == FileNotFoundException.class)
                {
                    Alert error = new Alert(Alert.AlertType.ERROR, String.format("Weights not loaded.\n" +
                                                                                 "File \"%s\" not found. Please reselect the file, and try again.", weights.getName()));
                    error.showAndWait();
                }
                else if (e.getClass() == Exception.class)
                {
                    Alert error = new Alert(Alert.AlertType.ERROR, String.format("Weights not loaded.\n" +
                                                                                 "File \"%s\" is invalid. " +
                                                                                 "Please confirm that the network is initialized to the right number of Hidden Nodes.", weights.getName()));
                    error.showAndWait();
                }
            }
        }
    }

    @FXML
    void newWeights ()
    {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileFormat = new FileChooser.ExtensionFilter(".txt", "*.txt");
        fileChooser.getExtensionFilters().add(fileFormat);
        fileChooser.setInitialDirectory(initialDir);
        File temp = fileChooser.showSaveDialog(loadWeightBttn.getScene().getWindow());
        if (temp != null)
        {
            try
            {
                neuralNet.saveweights(temp);
            }
            catch (IOException e)
            {
                Alert error = new Alert(Alert.AlertType.ERROR, String.format("The weights where not saved. Please try again."));
                error.showAndWait();
            }
        }
    }

    @FXML
    void canvasClear ()
    {
        canvasInput.getGraphicsContext2D().clearRect(0, 0, canvasInput.getWidth(), canvasInput.getHeight());
    }

    @FXML
    public void guess ()
    {

        //get the current contents of the canvas as a pixel array.
        int[] pixels = getInputfromCanvas();

        NNDataSet userInput = new NNDataSet();
        userInput.addSet(0, pixels);
        //userInput.printImage(0,28,28);


        //Make a guess at the value and print it to the location on the ui.
        guessOutput.setText(String.format("%s", neuralNet.test(userInput)));
    }

    @FXML
    public void drawCanvas (MouseEvent event)
    {
        double x = event.getX();
        double y = event.getY();
        int brushsize = 38;
        canvasInput.getGraphicsContext2D().fillOval(x - (brushsize / 2), y - (brushsize / 2), brushsize, brushsize);

    }

    @Override
    public void initialize (URL url, ResourceBundle rb)
    {
        try
        {
            neuralNet.loadweights(weights);
        }
        catch (Exception e)
        {
            guessBttn.setDisable(true);
            guessBttnWrapper.setDisable(false);
            Tooltip tooltip = new Tooltip();
            tooltip.setShowDelay(Duration.ZERO);
            tooltip.setText("Sorry there was an error loading the default weight file. Please load a valid weights file, in the settings Menu.");
            Tooltip.install(guessBttnWrapper, tooltip);
        }

        initializeFiles();

//disallow invalid characters for the spinners.
        learningRateSpinner.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>()
        {
            @Override
            public void handle (KeyEvent keyEvent)
            {
                if (! keyEvent.getCharacter().matches("[0-9.]")
                    && keyEvent.getCode() != KeyCode.ENTER
                    && keyEvent.getCode() != KeyCode.BACK_SPACE)
                {
                    keyEvent.consume();
                }
            }
        });

        epochSpinner.addEventFilter(KeyEvent.ANY, new EventHandler<KeyEvent>()
        {
            @Override
            public void handle (KeyEvent keyEvent)
            {
                if (! keyEvent.getCharacter().matches("\\d*")
                    && keyEvent.getCode() != KeyCode.ENTER
                    && keyEvent.getCode() != KeyCode.BACK_SPACE)
                {
                    keyEvent.consume();
                }
            }
        });


        //fix the way learningRateSpinner displays and receives values, to allow for 4 decimal places.
        //disallows invalid combinations of characters for the learningRateSpinner
        learningRateSpinner.getValueFactory().setConverter(new StringConverter<>()
        {
            @Override
            public String toString (Double aDouble)
            {
                return String.format("%.4f", aDouble);
            }

            @Override
            public Double fromString (String s)
            {
                if (s.matches("\\d*[.]?\\d*") && ! s.matches("^[.]$"))
                {
                    double input = Double.parseDouble(s);
                    if (input <= (((SpinnerValueFactory.DoubleSpinnerValueFactory) learningRateSpinner.getValueFactory()).getMax())
                        && input >= (((SpinnerValueFactory.DoubleSpinnerValueFactory) learningRateSpinner.getValueFactory()).getMin()))
                    {
                        return input;
                    }
                }
                learningRateSpinner.getEditor().setText(String.format("%.4f", learningRateSpinner.getValue()));
                return learningRateSpinner.getValue();
            }
        });
        learningRateSpinner.getEditor().setText(String.format("%.4f", learningRateSpinner.getValue()));


        //fix the epoch spinner to not accept invalid input.
        //disallows invalid combinations of characters for the epochSpinner
        epochSpinner.getValueFactory().setConverter(new StringConverter<>()
        {
            @Override
            public String toString (Integer aInt)
            {
                return String.format("%s", aInt);
            }

            @Override
            public Integer fromString (String s)
            {
                if (! s.matches("^[0]*$"))
                {
                    int input = Integer.parseInt(s);
                    if (input <= (((SpinnerValueFactory.IntegerSpinnerValueFactory) epochSpinner.getValueFactory()).getMax())
                        && input >= (((SpinnerValueFactory.IntegerSpinnerValueFactory) epochSpinner.getValueFactory()).getMin()))
                    {
                        return input;
                    }
                }
                epochSpinner.getEditor().setText(epochSpinner.getValue().toString());
                return epochSpinner.getValue();
            }
        });

        learningRateSpinner.setEditable(true);
        epochSpinner.setEditable(true);

        //allow the hiddenLayerSlider to display its value on a label.
        numOfNodes.textProperty().bind(hiddenLayerSlider.valueProperty().asString("%.0f"));

    }

    private void initializeFiles ()
    {
        weightsFilename.setText(weights.getName());
        testImageFilename.setText(testImgFile.getName());
        testLabelFilename.setText(testLabelFile.getName());
        trainImageFilename.setText(trainImgFile.getName());
        trainLabelFilename.setText(trainLabelFile.getName());
    }

    private int[] getInputfromCanvas ()
    {
        NNImagePreProcessor image = new NNImagePreProcessor(canvasInput.snapshot(null, null));

        //Crop the whitespace out of the image.
        image.crop();

        //resize the image preserving aspect ratio.
        image.scaleWithAspectRatio(20);

        //Place the current Image in a new one of the given size, centered by the center of mass.
        image.placeByCenterOfMass(28, 28);

        return image.getImgSet();
    }

    private File loadFile (String format, Window window)
    {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter fileFormat = new FileChooser.ExtensionFilter(format, "*" + format);
        fileChooser.getExtensionFilters().add(fileFormat);
        fileChooser.setInitialDirectory(initialDir);
        return fileChooser.showOpenDialog(window);
    }

}