package net.sharksystem.sensor;

/**
 * C = Celsius
 * F = Fahrenheit
 * K = Kelvin
 * P = Percent
 */
public enum Unit {
    C, F, K, P;

    public static String getStringValue(Unit u){
        switch (u){
            case C:
                return "°C";
            case P:
                return "%";
            case F:
                return "°F";
        }
        return u.toString();
    }
}

