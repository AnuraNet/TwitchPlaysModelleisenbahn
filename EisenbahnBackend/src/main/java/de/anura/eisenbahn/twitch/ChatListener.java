package de.anura.eisenbahn.twitch;

import de.anura.eisenbahn.twitch.ToggleHandler.ToggleState;
import de.lukweb.twitchchat.TwitchUser;
import de.lukweb.twitchchat.events.Handler;
import de.lukweb.twitchchat.events.Listener;
import de.lukweb.twitchchat.events.user.UserSendMessageEvent;

public class ChatListener extends Listener {

    @Handler
    public void onMessage(UserSendMessageEvent event) {
        if (event.isEmoteMessage()) return;
        if (!event.getMessage().startsWith("!")) return;
        TwitchUser user = event.getUser();
        String[] args = event.getMessage().split(" ");
        args[0] = args[0].substring(1);
        try {
            switch (args[0]) {
                case "signal":
                    if (args.length >= 2) {
                        String change = args[1];
                        ToggleState result = ToggleHandler.tryToToggle(true, Integer.valueOf(change));
                        handleResult(result, user);
                    }
                    break;
                case "weiche":
                    if (args.length >= 2) {
                        String change = args[1];
                        ToggleState result = ToggleHandler.tryToToggle(false, Integer.valueOf(change));
                        handleResult(result, user);
                    }
                    break;
                case "eisenbahn":
                    send("Nutze die Befehle !signal oder !weiche", user);
                    break;
            }
        } catch (NumberFormatException ex) {
            send("Unbekannte ID!", user);
        }
    }

    private void handleResult(ToggleState result, TwitchUser user) {
        switch (result) {
            case NOT_FOUND:
                send("Unbekannte ID!", user);
                break;
            case TOO_FAST:
                send("Warte auf den Cooldown!", user);
                break;
            case FAILED:
                send("Umschalten fehlgeschlagen!", user);
                break;
            case DONE:
                send("Erfolgreich umgeschaltet!", user);
                break;
        }
    }

    private void send(String msg, TwitchUser user) {
        String prefix = "";
        if (user != null) prefix = "@" + user.getName() + " ";
        TwitchHandler.getChannel().sendMessage(prefix + msg);
    }
}
