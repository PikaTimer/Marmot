/*
 * Copyright (C) 2019 John Garner <segfaultcoredump@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.pikatimer.marmot;

/**
 *
 * @author John Garner <segfaultcoredump@gmail.com>
 */



import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Map;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;



public class EventWebSocketClient extends WebSocketClient {

    private static Map<String,Participant> participantMap;
    private static ObservableList<Participant> displayedParticipantsList;
    
    public EventWebSocketClient(URI serverUri, Draft draft, Map<String,Participant> p) {
            super(serverUri, draft);
            participantMap = p;
    }

    public EventWebSocketClient(URI serverURI, Map<String,Participant> p, ObservableList<Participant> l) {
            super(serverURI);
            participantMap = p;
            displayedParticipantsList = l;
    }



    @Override
    public void onOpen(ServerHandshake handshakedata) {
            System.out.println("new connection opened");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
            System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        try {
            JSONObject json = new JSONObject(message);
            json.keySet().forEach(k -> {
                System.out.println("JSON Key: " + k);
                
                switch(k) {
                    case "PARTICIPANT": 
                        Participant p = new Participant(); 
                        p.setFromJSON(json.getJSONObject(k));
                        if (participantMap.containsKey(p.getBib())) {
                            // update on the FX thread to avoid concurrent update issues. 
                            Platform.runLater( () -> participantMap.get(p.getBib()).setFromJSON(json.getJSONObject(k)));
                            System.out.println("Updated: " + p.getBib() + " -> " + p.fullNameProperty().getValueSafe());
                        } else {
                            participantMap.put(p.getBib(), p);
                            System.out.println("Added: " + p.getBib() + " -> " + p.fullNameProperty().getValueSafe());
                        }
                        ; break;
                    case "ANNOUNCER":
                        String bib = json.optString(k);
                        if (! bib.isEmpty() && participantMap.containsKey(bib)) {
                            if(participantMap.containsKey(bib)) {
                                Participant part = participantMap.get(bib);
                                Platform.runLater(() -> {
                                    if (! displayedParticipantsList.contains(part)){
                                        displayedParticipantsList.add(0, part);
                                    } 
                                });
                                System.out.println("Added " + bib + " -> " + part.toString());
                            }
                            
                        } else {
                            System.out.println("Unknown bib " + bib);
                        }
                }
                
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
            System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
            System.err.println("an error occurred:" + ex);
    }

    
}