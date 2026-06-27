package gg.eventalerts.eventalertsintegration.messages;

import gg.eventalerts.eventalertsintegration.EventAlertsIntegration;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.message.AnnoyingMessages;
import xyz.srnyx.annoyingapi.message.MessagesProvider;


public class EAMessagesProvider extends MessagesProvider {
    @NotNull private final EventAlertsIntegration plugin;
    private EAMessages messages;

    public EAMessagesProvider(@NotNull EventAlertsIntegration plugin) {
        this.plugin = plugin;

        builder(b -> b.config(new EAMessages(plugin)));
        defaults
                .prefix("&6&lEA &8&l| &e")
                .p("&e")
                .s("&6");
    }

    @Override @NotNull
    public EventAlertsIntegration getAnnoyingPlugin() {
        return plugin;
    }

    @Override
    public void accept(@NotNull AnnoyingMessages messages) {
        this.messages = (EAMessages) messages;
    }

    @Override @NotNull
    public EAMessages get() {
        return messages;
    }
}
