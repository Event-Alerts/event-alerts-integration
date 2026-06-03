package gg.eventalerts.eventalertsintegration.objects;

import org.jetbrains.annotations.Nullable;

import java.util.List;


public class EventThreadMessage extends EAObject {
    @Nullable public Event event;
    @Nullable public Channel channel;
    @Nullable public Author author;
    @Nullable public Message message;

    public static class Channel extends EAObject {
        @Nullable public Long id;
        @Nullable public String name;
    }

    public static class Author extends EAObject {
        @Nullable public Long id;
        @Nullable public String name;
        @Nullable public String effectiveName;
        @Nullable public EAPlayer player;
    }

    public static class Message extends EAObject {
        @Nullable public Long id;
        @Nullable public Content content;
        @Nullable public List<Attachment> attachments;

        public static class Content extends EAObject {
            @Nullable public String raw;
            @Nullable public String display;
            @Nullable public String stripped;
        }

        public static class Attachment extends EAObject {
            @Nullable public Long id;
            @Nullable public String name;
            @Nullable public String url;
            @Nullable public String proxyUrl;
        }
    }
}
