package gg.eventalerts.eventalertsintegration;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.codehaus.plexus.util.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;


public class MockTestSupport {
    protected static MockEventAlertsIntegration PLUGIN;

    @BeforeAll
    static void setUpMockBukkit() {
        MockBukkit.mock();
        PLUGIN = MockBukkit.load(MockEventAlertsIntegration.class);
    }

    @AfterAll
    static void tearDownMockBukkit() {
        MockBukkit.unmock();
    }

    @BeforeEach
    void cleanDataFolder() {
        try {
            FileUtils.cleanDirectory(PLUGIN.getDataFolder());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
