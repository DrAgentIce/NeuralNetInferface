/*
 *  A JavaFx application designed to take user input in the form of a drawn digit 0-9, and ask an MLP to display the value of digit drawn.
 *  Copyright (C) 2018  Kobe Weinstein
 *
 *  This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
