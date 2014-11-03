package fr.ybonnel.websocket;

import fr.ybonnel.simpleweb4j.handlers.RouteParameters;
import fr.ybonnel.simpleweb4j.handlers.websocket.WebSocketListener;
import fr.ybonnel.simpleweb4j.handlers.websocket.WebSocketSession;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.start;
import static fr.ybonnel.simpleweb4j.SimpleWeb4j.websocket;

public class WebSocket {

    public static void main(String[] args) {
        routes();

        start();
    }

    public static void routes() {
        websocket("/chat/:name", WebSocket::buildListenner);
    }

    protected static void resetSessions() {
        sessionOpenned.clear();
    }

    // On garde les sessions ouvertes
    private static Set<WebSocketSession<Message>> sessionOpenned =
            Collections.newSetFromMap(new ConcurrentHashMap<>());

    static WebSocketListener<String, Message> buildListenner(
            RouteParameters routeParameters) {
        // Création du builder
        return WebSocketListener.<String, Message>newBuilder(String.class)
                // Sur onConnect, on ajoute la session aux sessions ouvertes.
                .onConnect(sessionOpenned::add)
                // Sur onMessage, on envoi un nouveau message à toutes les sessions
                .onMessage(texte ->
                        sendMessageToAllSession(
                                new Message(routeParameters.getParam("name"), texte)))
                // Sur onClose, on supprimer la session des sessions ouvertes.
                .onClose(sessionOpenned::remove)
                // On construit le listener.
                .build();
    }

    static void sendMessageToAllSession(Message message) {
        // Pour chaque session on envoie le message.
        sessionOpenned.forEach(session -> {
            try {
                session.sendMessage(message);
            } catch (IOException ignore) {
            }
        });
    }
}
