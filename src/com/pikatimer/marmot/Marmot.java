/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pikatimer.marmot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;


/**
 *
 * @author John Garner <segfaultcoredump@gmail.Marmot>
 */
public class Marmot extends Application {
    //private final Event event = Event.getInstance(); 
    private static Stage mainStage;

    
    public static final String VERSION = "1.5";
    
    /**
    * SingletonHolder is loaded on the first execution of Singleton.getInstance() 
    * or the first access to SingletonHolder.INSTANCE, not before.
    */
    private static class SingletonHolder { 
            private static final Marmot INSTANCE = new Marmot();
    }

    public static Marmot getInstance() {
            return SingletonHolder.INSTANCE;
    }
    

    
      
    public Stage getPrimaryStage() {
        return mainStage;
    }
    
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        
        mainStage=primaryStage;
        primaryStage.setTitle("Marmot Announcer");
        
        Pane myPane = (Pane)FXMLLoader.load(getClass().getResource("FXMLmarmot.fxml"));
        Scene myScene = new Scene(myPane);
        
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();  
  
        //set Stage boundaries so that the main screen is centered.                
        primaryStage.setX((primaryScreenBounds.getWidth() - primaryStage.getWidth())/2);  
        primaryStage.setY((primaryScreenBounds.getHeight() - primaryStage.getHeight())/2);  
 
        // F11 to toggle fullscreen mode
        myScene.getAccelerators().put(new KeyCodeCombination(KeyCode.F11), () -> {
            mainStage.setFullScreen(mainStage.fullScreenProperty().not().get());
        });
        
//        // Icons
//        String[] sizes = {"256","128","64","48","32"};
//        for(String s: sizes){
//            primaryStage.getIcons().add(new Image("resources/icons/Pika_"+s+".ico"));
//            primaryStage.getIcons().add(new Image("resources/icons/Pika_"+s+".png"));
//        }
        
        
        primaryStage.setScene(myScene);
        primaryStage.show();
        
        System.out.println("Exiting Marmot.start()");
        
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
