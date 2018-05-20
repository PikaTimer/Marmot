/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pikatimer.marmot;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.util.Callback;

/**
 *
 * @author John Garner <segfaultcoredump@gmail.com>
 */
public class Participant {
    private final StringProperty firstNameProperty = new SimpleStringProperty("");
    private final StringProperty middleNameProperty= new SimpleStringProperty();
    private final StringProperty lastNameProperty= new SimpleStringProperty("");
    private final StringProperty fullNameProperty= new SimpleStringProperty();
    private final StringProperty emailProperty= new SimpleStringProperty(); 
    private final StringProperty bibProperty= new SimpleStringProperty();
    private final IntegerProperty ageProperty = new SimpleIntegerProperty();
    private final StringProperty sexProperty= new SimpleStringProperty(); 
    private final StringProperty cityProperty= new SimpleStringProperty();
    private final StringProperty stateProperty= new SimpleStringProperty();
    private final StringProperty countryProperty= new SimpleStringProperty();
    private final StringProperty zipProperty = new SimpleStringProperty();
    private final StringProperty noteProperty = new SimpleStringProperty();

    
    ObservableMap attributeMap =  FXCollections.observableHashMap();
    
    public void Participant(){
    
    }
    
    public String getBib() {
        return bibProperty.getValue(); 
    }
    public void setBib(String id) {
        bibProperty.setValue(id);
    }
    public StringProperty bibProperty() {
        return bibProperty; 
    }
    
    private void updateFullName(){
        fullNameProperty.setValue((firstNameProperty.getValueSafe() + " " + middleNameProperty.getValueSafe() + " " + lastNameProperty.getValueSafe()).replaceAll("( )+", " "));
    }
    
    public static ObservableMap<String,String> getAvailableAttributes() {
        ObservableMap<String,String> attribMap = FXCollections.observableMap(new LinkedHashMap() );
        
        attribMap.put("bib", "Bib");
        attribMap.put("first", "First Name");
        attribMap.put("middle", "Middle Name");
        attribMap.put("last", "Last Name");
        attribMap.put("age", "Age");
        attribMap.put("birth","Birthday");
        attribMap.put("sex-gender", "Sex");
        attribMap.put("city", "City");
        attribMap.put("state", "State");
        attribMap.put("zip","Zip Code");
        attribMap.put("country", "Country");
        attribMap.put("status","Status");
        attribMap.put("note","Note");
        attribMap.put("email", "EMail");
        // TODO: routine to add custom attributes based on db lookup
        return attribMap; 
    }
        public void setAttributes(Map<String, String> attribMap) {
        // bulk set routine. Everything is a string so convert as needed
        
        attribMap.entrySet().stream().forEach((Map.Entry<String, String> entry) -> {
            if (entry.getKey() != null) {
                System.out.println("processing " + entry.getKey() + " -> " + entry.getValue());
             switch(entry.getKey()) {
                 case "bib": this.setBib(entry.getValue()); break; 
                 case "first": this.setFirstName(entry.getValue()); break;
                 case "middle": this.setMiddleName(entry.getValue()); break;
                 case "last": this.setLastName(entry.getValue()); break;
                 case "age": 
                     //Setting the birthdate will also set the age, so if the age is already set just skip it.
                     try {
                        this.setAge(Integer.parseUnsignedInt(entry.getValue())); 
                     } catch (Exception e) {
                         System.out.println("Unable to parse age " + entry.getValue() );
                     }
                     break; 
                     
                 // TODO: map to selected sex translator
                 case "sex-gender": this.setSex(entry.getValue()); break; 
                 case "city": this.setCity(entry.getValue()); break; 
                 case "state": this.setState(entry.getValue()); break; 
                 case "country": this.setCountry(entry.getValue()); break;
                 case "zip": this.setZip(entry.getValue()); break;
                 
                 case "note": this.setNote(entry.getValue()); break;

                 
             }
            }
        });
        
        System.out.println("Added " + (firstNameProperty.getValueSafe() + " " + middleNameProperty.getValueSafe() + " " + lastNameProperty.getValueSafe()).replaceAll("( )+", " "));
        System.out.println("  " + fullNameProperty.getValueSafe());
    }
        
    public String getFirstName() {
        return firstNameProperty.getValueSafe();
    }
    public void setFirstName(String fName) {
        firstNameProperty.setValue(fName);
        updateFullName();
    }
    public StringProperty firstNameProperty() {
        return firstNameProperty; 
    }
 
    
    public String getLastName() {
        return lastNameProperty.getValueSafe();
    }
    public void setLastName(String fName) {
        lastNameProperty.setValue(fName);
        updateFullName();
    }
    public StringProperty lastNameProperty() {
        return lastNameProperty;
    }
    
    public String getMiddleName() {
        return middleNameProperty.getValueSafe();
    }
    public void setMiddleName(String mName) {
        middleNameProperty.setValue(mName);
        updateFullName();
    }
    public StringProperty middleNameProperty() {
        return middleNameProperty;
    }
    
    public StringProperty fullNameProperty(){
        return fullNameProperty;
    }
    
    public String getEmail() {
        return emailProperty.getValueSafe();
    }
    public void setEmail(String fName) {
        emailProperty.setValue(fName);
    }
    public StringProperty emailProperty() {
        return emailProperty; 
    }
   
    public Integer getAge () {
        return ageProperty.getValue();
    }
    public void setAge (Integer a) {
        ageProperty.setValue(a);
    }
    public IntegerProperty ageProperty() {
        return ageProperty; 
    }
    
    
    public String getSex() {
        return sexProperty.getValueSafe();
    }
    public void setSex(String s) {
        //Set to an upper case M or F for now
        //TODO: Switch this to the allowable values for a SEX 
        if (s == null) return;
        if (s.startsWith("M") || s.startsWith("m")) sexProperty.setValue("M");
        else if (s.startsWith("F") || s.startsWith("f")) sexProperty.setValue("F");
        else sexProperty.setValue(s);
    }
    public StringProperty sexProperty() {
        return sexProperty;
    }
    
    public String getCity() {
        return cityProperty.getValueSafe();
    }
    public void setCity(String c) {
        cityProperty.setValue(c);
    }
    public StringProperty cityProperty() {
        return cityProperty; 
    }
    
    public String getState() {
        return stateProperty.getValueSafe();
    }
    public void setState(String s) {
        stateProperty.setValue(s);
    }
    public StringProperty stateProperty(){
        return stateProperty;
    }
    
    public String getZip() {
        return zipProperty.getValueSafe();
    }
    public void setZip(String s) {
        zipProperty.setValue(s);
    }
    public StringProperty zipProperty(){
        return zipProperty;
    }
    
    public String getCountry() {
        return countryProperty.getValueSafe();
    }
    public void setCountry(String s) {
        countryProperty.setValue(s);
    }
    public StringProperty countryProperty(){
        return countryProperty;
    }
    
    public String getNote() {
        return noteProperty.getValueSafe();
    }
    public void setNote(String s) {
        noteProperty.setValue(s);
    }
    public StringProperty noteProperty(){
        return noteProperty;
    }        
    
    
    
    public static Callback<Participant, Observable[]> extractor() {
        return (Participant p) -> new Observable[]{p.firstNameProperty,p.middleNameProperty,p.lastNameProperty,p.bibProperty,p.ageProperty,p.sexProperty,p.cityProperty,p.stateProperty,p.countryProperty};
    }

    @Override
    public String toString(){
        return fullNameProperty.getValueSafe();
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.bibProperty.getValue());

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Participant other = (Participant) obj;
        if (!Objects.equals(this.bibProperty.getValue(),other.bibProperty.getValue())) {
            return false; 
        }
        return true;
    }
    
}
