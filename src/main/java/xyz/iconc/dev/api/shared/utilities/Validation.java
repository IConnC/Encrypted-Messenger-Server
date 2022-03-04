package xyz.iconc.dev.api.shared.utilities;

public class Validation {
    public static boolean ValidateIdentifier(long identifier) {

        return true;
    }
    
    public static boolean ValidateIdentifier(String rawIdentifier) {
        long identifier = Long.MIN_VALUE;
        try {
            identifier = Long.parseLong(rawIdentifier);
        } catch (NumberFormatException e) {
            return false;
        }
        if (identifier == Long.MIN_VALUE) return false;

        return ValidateIdentifier(identifier);
    }
}
