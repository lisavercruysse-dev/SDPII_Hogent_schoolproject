package exception;

import util.WerknemerElement;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class WerknemerInformationException extends Exception {
    private Map<WerknemerElement, String> errors;

    public WerknemerInformationException(Map<WerknemerElement, String> errors) {
        super("Fout bij het aanmaken van werknemer");
        this.errors = errors;
    }

    public Map<WerknemerElement, String> getInformationRequired() {
        return Collections.unmodifiableMap(errors);
    }
}
