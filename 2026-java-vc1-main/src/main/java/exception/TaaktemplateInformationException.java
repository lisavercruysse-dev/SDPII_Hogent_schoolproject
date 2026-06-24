package exception;

import util.TaaktemplateElement;

import java.util.Collections;
import java.util.Map;

public class TaaktemplateInformationException extends RuntimeException {
    private Map<TaaktemplateElement, String> errors;

    public TaaktemplateInformationException(Map<TaaktemplateElement, String> errors) {
        super("Fout bij het aanmaken van taaktemplate");
        this.errors = errors;
    }

    public Map<TaaktemplateElement, String> getErrors() {
        return Collections.unmodifiableMap(errors);
    }
}
