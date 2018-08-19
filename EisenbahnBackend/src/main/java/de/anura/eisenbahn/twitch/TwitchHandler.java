package de.anura.eisenbahn.twitch;

import de.anura.eisenbahn.ConfigReader;
import de.lukweb.twitchchat.TwitchChannel;
import de.lukweb.twitchchat.TwitchChat;

public class TwitchHandler {
    private static TwitchChat chat;
    private static TwitchChannel channel;

    public static void connect() {
        if (chat != null) return;
        chat = TwitchChat.build(ConfigReader.getUser(), ConfigReader.getOauth());
        chat.getEventManager().register(new ChatListener());
        chat.connect();
        channel = chat.getChannel(ConfigReader.getUser());
        channel.sendMessage("Bot started!");
    }

    public static void disconnect() {
        if (chat != null) {
            chat.close();
            chat = null;
        }
    }

    public static TwitchChannel getChannel() {
        return channel;
    }
}
