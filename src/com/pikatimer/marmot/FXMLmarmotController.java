/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pikatimer.marmot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.h2.tools.Csv;


/**
 * FXML Controller class
 *
 * @author John Garner <segfaultcoredump@gmail.com>
 */
public class FXMLmarmotController{

    @FXML private GridPane rootGridPane;
    @FXML private Button loadButton;
    @FXML private TextField bibTextField;
    @FXML private Label resultLabel;
    @FXML private Label participantCountLabel;
    @FXML private Button clearListButton;
    @FXML private Spinner<Integer> fontSizeSpinner;
    @FXML private ToggleButton fullScreenToggleButton;
    
    @FXML private ListView<Participant> finisherListView;
    
    Preferences prefs = Preferences.userRoot().node("PikaTimer");
    
    ObservableList displayedParticipantsList = FXCollections.observableArrayList();
    Map<String,Participant> participantMap = new HashMap();
    
    Integer partcicipantCount = 0;
    
    Integer baseFontSize = 36;
    
    Stage primaryStage = Marmot.getInstance().getPrimaryStage();
    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // TODO
        loadButton.setOnAction((ActionEvent event) -> {
            loadParticipants();
        });
        
        clearListButton.setOnAction((ActionEvent event) -> {
            displayedParticipantsList.clear();
        });
        participantCountLabel.setText("0");
        finisherListView.setItems(displayedParticipantsList);
        bibTextField.setOnAction((event) -> {
            showParticipant();
        });
        
        // if the bib text field looses focus, pull it back
        bibTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal.equals(false)){
                if (!participantMap.isEmpty()) 
                    Platform.runLater(() -> bibTextField.requestFocus());
            }
        });
        
        primaryStage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
            fullScreenToggleButton.setSelected(newVal);
        });
        fullScreenToggleButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (primaryStage.fullScreenProperty().get() != newVal)
                primaryStage.setFullScreen(newVal);
        });

        finisherListView.setStyle("-fx-font-size:" + baseFontSize.toString() + ";");
        
//        Broken due to a JDK bug not fixed until 9
//        Platform.runLater(() -> {
//            System.out.println("Adding Accelerators");
//            primaryStage = Marmot.getInstance().getPrimaryStage();
//            primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN), () -> {
//                baseFontSize++;
//                if (baseFontSize < 99) baseFontSize = 99;
//                System.out.println("Font size is now " + baseFontSize.toString());
//                updateFontSize();
//                
//            });
//            primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN), () -> {
//                baseFontSize--;
//                if (baseFontSize < 8) baseFontSize = 8;
//                System.out.println("Font size is now " + baseFontSize.toString());
//                updateFontSize();
//            });
//            System.out.println(primaryStage.getScene().getAccelerators().size() + " Accelerators");
//        });
        
        fontSizeSpinner.setStyle("-fx-font-size: 18;");
        fontSizeSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        fontSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(14,99,baseFontSize));
        fontSizeSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            System.out.println("New spinner value: "+newValue);
            baseFontSize=newValue;
            updateFontSize();
        });
        
        finisherListView.setCellFactory(param -> new ListCell<Participant>() {
            Label bib = new Label("");
            Label fullName = new Label("");
            Label city = new Label("");
            Label state = new Label("");
            Label country = new Label("");
            Label time = new Label("");
            Label note = new Label("");
            
            VBox toVBox = new VBox();
            HBox nameHBox = new HBox();
            HBox detailsHBox = new HBox();
            
            @Override
            protected void updateItem(Participant to, boolean empty) {
                super.updateItem(to, empty);
                
                if (empty || to == null || to.getBib() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    
                    bib.setText("  (" + to.getBib() +")");
                    fullName.textProperty().bind(to.fullNameProperty());
                    fullName.setStyle("-fx-font-weight: bold;");
                    nameHBox.setSpacing(5);
                    nameHBox.getChildren().setAll(fullName,bib);
                    
                    city.textProperty().bind(Bindings.concat(to.cityProperty(),","));
                    state.textProperty().bind(to.stateProperty());
                    country.textProperty().bind(to.countryProperty());
                    
                    note.textProperty().bind(to.noteProperty());

                    detailsHBox.setSpacing(5);
                    detailsHBox.getChildren().setAll(city, state, country);
                    
                    if (to.getNote().isEmpty()) toVBox.getChildren().setAll(nameHBox,detailsHBox);
                    else {
                        note.setText(to.getNote());
                        toVBox.getChildren().setAll(nameHBox,detailsHBox,note);
                    }
                    
                    setText(null);
                    setGraphic(toVBox);
                    
                }
            }
        });
        
    }    
    
    private void showParticipant(){
        System.out.println("showParticipant() with " + bibTextField.getText());
        String bib = bibTextField.getText();
       if (! bib.isEmpty() ){
           if(participantMap.containsKey(bib)) {
               Participant p = participantMap.get(bib);
               if (displayedParticipantsList.contains(p)){
                   displayedParticipantsList.remove(p);
               } 
               displayedParticipantsList.add(0, p);
               System.out.println("Added " + bib + " -> " + p.toString());
               resultLabel.setText("");
           } else {
               resultLabel.setText("Bib " + bib + " not found"); 
           }
           bibTextField.setText("");
       }
    }
    
    private void updateFontSize(){
        //fontSizeSpinner.getValueFactory().setValue(baseFontSize);
        finisherListView.setStyle("-fx-font-size:" + baseFontSize.toString() + ";");
    }
    
    private void loadParticipants()  {
        partcicipantCount = 0;
        participantMap.clear();
        
        final FileChooser fileChooser = new FileChooser();
        
        fileChooser.setTitle("Open CSV File");
        
        File lastEventFolder = new File(prefs.get("PikaEventHome", System.getProperty("user.home")));
        if (!lastEventFolder.exists() ) {
            // we have a minor problem
            lastEventFolder= new File(System.getProperty("user.home"));
        } else if (lastEventFolder.exists() && lastEventFolder.isFile()){
            lastEventFolder = new File(lastEventFolder.getParent());
           
        }
        
        System.out.println("Using initial directory of " + lastEventFolder.getAbsolutePath());

        fileChooser.setInitialDirectory(lastEventFolder); 
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV/TXT Files", "*.csv", "*.txt"),
                new FileChooser.ExtensionFilter("All files", "*")
            );
        File file = fileChooser.showOpenDialog(rootGridPane.getScene().getWindow());
        System.out.println("Opening existing file....");
        if (file != null && file.exists() && file.isFile() && file.canRead()) {
            
            
            // Let's play the "What type of text file is this..." game
            // Try UTF-8 and see if it blows up on the decode. If it does, default down to a platform specific type and then hope for the best
            // TODO: fix the "platform specific" part to not assume Windows in the US
            CharsetDecoder uft8Decoder = StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
            String charset = "UTF-8"; 
            try {
                String result = new BufferedReader(new InputStreamReader(new FileInputStream(file),uft8Decoder)).lines().collect(Collectors.joining("\n"));
            } catch (Exception ex) {
                System.out.println("Not UTF-8: " + ex.getMessage());
                charset = "Cp1252"; // Windows standard txt file stuff
            }

            Map<String,String> attributeMap = Participant.getAvailableAttributes();
            Map<String,String> reverseAttributeMap = new HashMap();
            attributeMap.keySet().forEach(k -> {reverseAttributeMap.put(attributeMap.get(k).toLowerCase().replace(" ", ""),k);});
            
            ResultSet rs;

            try {
                rs = new Csv().read(file.getPath(),null,charset);
                ResultSetMetaData meta = rs.getMetaData();
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    System.out.println("Column: " + meta.getColumnLabel(i+1));
                }

                while (rs.next()) {
                    Map<String,String> attributes = new HashMap();

                    for (int i = 0; i < meta.getColumnCount(); i++) {
                        
                        if (reverseAttributeMap.containsKey(meta.getColumnLabel(i+1).toLowerCase().replace(" ", ""))) {
                            String key = reverseAttributeMap.get(meta.getColumnLabel(i+1).toLowerCase().replace(" ", ""));
                            if (!"".equals(rs.getString(i+1))) {
                                attributes.put(key,rs.getString(i+1));
                                System.out.println("  " + key + " -> " + rs.getString(i+1));
                            }
                        }
                    }
                    Participant p = new Participant(); 
                    p.setAttributes(attributes);
                    System.out.println("Map: " + p.getBib() + " -> " + p.fullNameProperty().getValueSafe());
                    participantMap.put(p.getBib(), p);
                    partcicipantCount++;
                }

                System.out.println("Loaded " + partcicipantCount + " participants");
                participantCountLabel.setText(partcicipantCount.toString());
            } catch (SQLException ex) {
                // TODO: Exception Dialog Box
            }
        }    
    }
    
}
