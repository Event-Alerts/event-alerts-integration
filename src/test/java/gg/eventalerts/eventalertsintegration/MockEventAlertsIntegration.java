package gg.eventalerts.eventalertsintegration;

import gg.eventalerts.eventalertsintegration.messages.EAMessagesProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srnyx.annoyingapi.library.AnnoyingLibraryManager;


public class MockEventAlertsIntegration extends EventAlertsIntegration {
    public MockEventAlertsIntegration() {
        options.pluginOptions.applyMockTemplate();
    }

    @Override @Nullable
    protected AnnoyingLibraryManager createLibraryManager() {
        // Class loader can't be casted for the library manager to work
        return null;
    }

    @Override @NotNull
    public EAMessagesProvider getMessages() {
        // Registrables don't work in tests, so it would try to cast default/anonymous provider to EAMessagesProvider
        return new EAMessagesProvider(this);
    }

    @Override
    public void loadSDK() {
        // Don't connect to Event Alerts servers
    }
}
