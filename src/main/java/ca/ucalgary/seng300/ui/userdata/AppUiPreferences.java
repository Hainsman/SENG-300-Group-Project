package ca.ucalgary.seng300.ui.userdata;

/**
 * Global UI preferences applied to the whole app (text scale, colorblind accents).
 */
public final class AppUiPreferences {

    private AppUiPreferences() {
    }

    /** Visual scale factor for root transform and window sizing (0.9–1.25). */
    public static double textScaleFactor = 1.0;

    public static boolean colorblindMode = false;

    public static double factorFromLabel(String label) {
        if (label == null) {
            return 1.0;
        }
        return switch (label) {
            case "Small (90%)" -> 0.9;
            case "Large (110%)" -> 1.1;
            case "Extra Large (125%)" -> 1.25;
            default -> 1.0;
        };
    }

    public static String labelFromFactor(double factor) {
        if (Math.abs(factor - 0.9) < 0.02) {
            return "Small (90%)";
        }
        if (Math.abs(factor - 1.1) < 0.02) {
            return "Large (110%)";
        }
        if (Math.abs(factor - 1.25) < 0.02) {
            return "Extra Large (125%)";
        }
        return "Normal (100%)";
    }
}
