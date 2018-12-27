//Kobe Weinstein
//WeinstienKobe2137@gmail.com
//12/17/2018
//NeuralNetInterface
//Cs17.11
//Launches the UiController from the ui package for the Neural Network Interface.

package edu.srjc.weinstein.kobe.nninterface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{

    @Override
    public void start (Stage stage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("ui/UiLayout.fxml"));

        Scene scene = new Scene(root);
        stage.setTitle("Neural Net Interface");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main (String[] args)
    {
        launch(args);
    }

}
