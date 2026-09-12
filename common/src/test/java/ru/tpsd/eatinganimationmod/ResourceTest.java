package ru.tpsd.eatinganimationmod;

import org.junit.jupiter.api.Test;

final class ResourceTest {
    @Test
    void modelsAndSupportPackLoadWithMinecraftCodecs() throws Exception {
        ResourceChecks.run(false);
    }
}
