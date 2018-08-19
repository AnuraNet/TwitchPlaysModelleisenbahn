package de.anura.eisenbahn.twitch;

import de.anura.eisenbahn.devices.Device;
import java.util.HashMap;
import java.util.Map;

public class ToggleHandler {

    private static final int COOLDOWN = 1000 * 10;

    private static final Map<Integer, Device> signals = new HashMap<>();
    private static final Map<Integer, Device> switches = new HashMap<>();

    private static final Map<Device, Long> cooldown = new HashMap<>();

    static {
        signals.put(3, Device.SIGNAL_3);
        signals.put(4, Device.SIGNAL_4);
        signals.put(5, Device.SIGNAL_5);

        switches.put(9, Device.SWITCH_9);
        switches.put(11, Device.SWITCH_11);
        switches.put(13, Device.SWITCH_13);
        switches.put(15, Device.SWITCH_15);
        switches.put(17, Device.SWITCH_17);
    };

    public static ToggleState tryToToggle(boolean signal, int num) {
        Device dev = signal ? signals.get(num) : switches.get(num);
        if (dev == null) {
            return ToggleState.NOT_FOUND;
        }

        if (dev.getType() != Device.DataType.BOOLEAN) {
            return ToggleState.NOT_FOUND;
        }

        if (cooldown.containsKey(dev) && cooldown.get(dev) > System.currentTimeMillis()) {
            return ToggleState.TOO_FAST;
        }

        cooldown.put(dev, System.currentTimeMillis() + COOLDOWN);

        if (!dev.toggle()) {
            return ToggleState.FAILED;
        }

        return ToggleState.DONE;
    }

    public static enum ToggleState {
        NOT_FOUND,
        TOO_FAST,
        FAILED,
        DONE;
    }
}
