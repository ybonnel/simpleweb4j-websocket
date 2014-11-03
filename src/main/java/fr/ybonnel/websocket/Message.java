package fr.ybonnel.websocket;

import java.util.Date;

public class Message {
    private String user;
    private String texte;
    private Date date;

    public Message(String user, String texte) {
        this.user = user;
        this.texte = texte;
        this.date = new Date();
    }
}
