package exception;

import util.SiteElement;

import java.util.Collections;
import java.util.Map;

public class SiteInformationException extends Exception{
    private Map<SiteElement, String> errors;

    public SiteInformationException(Map<SiteElement, String> errors) {
        super("Fout bij het aanmaken van de site");
        this.errors = errors;
    }

    public Map<SiteElement, String> getInformationRequired() {
        return Collections.unmodifiableMap(errors);
    }
}
