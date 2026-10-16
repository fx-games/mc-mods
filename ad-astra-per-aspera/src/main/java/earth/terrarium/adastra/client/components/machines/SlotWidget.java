package earth.terrarium.adastra.client.components.machines;

import earth.terrarium.adastra.common.menus.configuration.SlotConfiguration;

public class SlotWidget extends ConfigurationWidget {

    public SlotWidget(SlotConfiguration configuration) {
        super(configuration, configuration.width(), configuration.height());
    }
}
