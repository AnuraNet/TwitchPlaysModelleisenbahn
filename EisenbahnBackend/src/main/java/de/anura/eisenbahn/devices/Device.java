package de.anura.eisenbahn.devices;

public enum Device {

    SIGNAL_3(7, 1),
    SIGNAL_4(8, 9),
    SIGNAL_5(0, 2),
    SWITCH_17(4, 3),
    SWITCH_9(28, 29),
    SWITCH_11(27, 25),
    SWITCH_13(23, 24),
    SWITCH_15(26, 22),
    POWER_TOP('A'),
    POWER_INNER('B'),
    POWER_OUTER('C');

    private int pinOff, pinOn, pinToggle = -1;
    private boolean isOn;
    private char output;
    private int level;
    private final DataType type;

    private Device(int pinOff, int pinOn) {
        this.pinOff = pinOff;
        this.pinOn = pinOn;
        this.isOn = false;
        this.type = DataType.BOOLEAN;
    }

    private Device(int pinToggle) {
        this.pinToggle = pinToggle;
        this.isOn = true;
        this.type = DataType.BOOLEAN;
    }

    private Device(char output) {
        this.output = output;
        this.type = DataType.INT;
    }

    public boolean setLevel(int level) {
        if (type == DataType.BOOLEAN) return false;
        if (this.level == level) return false;
        if (OutputManager.setLevel(output, level)) {
            this.level = level;
            return true;
        }
        return false;
    }

    public boolean toggle() {
        if (type == DataType.INT) return false;
        int pin;
        if (pinToggle > -1) {
            if (!OutputManager.switchPin(pinToggle, !isOn)) {
                return false;
            }
        } else {
            if (isOn) {
                pin = pinOff;
            } else {
                pin = pinOn;
            }
            if (!OutputManager.pulsePin(pin)) {
                return false;
            }
        }
        isOn = !isOn;
        return true;
    }

    private void doInit() {
        if (pinToggle > -1) {
            OutputManager.initPin(pinToggle);
        } else {
            OutputManager.initPin(pinOff);
            OutputManager.initPin(pinOn);
            OutputManager.pulsePin(pinOff);
        }
    }

    public boolean isOn() {
        return isOn;
    }

    public int getLevel() {
        return level;
    }

    public DataType getType() {
        return type;
    }

    public static void init() {
        for (Device dev : Device.values()) {
            if (dev.getType() == DataType.BOOLEAN) {
                dev.doInit();
            }
        }
    }

    public static enum DataType {
        BOOLEAN,
        INT;
    }
}
