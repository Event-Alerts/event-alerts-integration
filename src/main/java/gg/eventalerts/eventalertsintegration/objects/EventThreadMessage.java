package gg.eventalerts.eventalertsintegration.objects;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.libs.javautilities.MiscUtility;


public class EventThreadMessage extends EAObject {
    @NotNull private static final String PROP_MESSAGE_ID = "messageId";
    @NotNull private static final String PROP_EVENT = "event";
    @NotNull private static final String PROP_CHANNEL = "channel";
    @NotNull private static final String PROP_AUTHOR = "author";
    @NotNull private static final String PROP_CONTENT = "content";

    @NotNull public final String messageId;
    @NotNull public final Event event;
    @NotNull public final Channel channel;
    @NotNull public final Author author;
    @NotNull public final Content content;

    public EventThreadMessage(@NotNull JsonObject json) {
        super(json);
        this.messageId = json.get(PROP_MESSAGE_ID).getAsString();
        this.event = new Event(json.getAsJsonObject(PROP_EVENT));
        this.channel = new Channel(json.getAsJsonObject(PROP_CHANNEL));
        this.author = new Author(json.getAsJsonObject(PROP_AUTHOR));
        this.content = new Content(json.getAsJsonObject(PROP_CONTENT));
    }

    public static class Channel {
        @NotNull private static final String PROP_ID = "id";
        @NotNull private static final String PROP_NAME = "name";

        @NotNull public final String id;
        @NotNull public final String name;

        public Channel(@NotNull JsonObject json) {
            this.id = json.get(PROP_ID).getAsString();
            this.name = json.get(PROP_NAME).getAsString();
        }
    }

    public static class Author {
        @NotNull private static final String PROP_ID = "id";
        @NotNull private static final String PROP_NAME = "name";
        @NotNull private static final String PROP_EFFECTIVE_NAME = "effectiveName";
        @NotNull private static final String PROP_PLAYER = "player";

        @NotNull public final String id;
        @NotNull public final String name;
        @NotNull public final String effectiveName;
        @Nullable public final EAPlayer player;

        public Author(@NotNull JsonObject json) {
            this.id = json.get(PROP_ID).getAsString();
            this.name = json.get(PROP_NAME).getAsString();
            this.effectiveName = json.get(PROP_EFFECTIVE_NAME).getAsString();
            this.player = MiscUtility.handleException(() -> new EAPlayer(json.getAsJsonObject(PROP_PLAYER))).orElse(null);
        }
    }

    public static class Content {
        @NotNull private static final String PROP_RAW = "raw";
        @NotNull private static final String PROP_DISPLAY = "display";
        @NotNull private static final String PROP_STRIPPED = "stripped";

        @NotNull public final String raw;
        @NotNull public final String display;
        @NotNull public final String stripped;

        public Content(@NotNull JsonObject json) {
            this.raw = json.get(PROP_RAW).getAsString();
            this.display = json.get(PROP_DISPLAY).getAsString();
            this.stripped = json.get(PROP_STRIPPED).getAsString();
        }
    }
}
