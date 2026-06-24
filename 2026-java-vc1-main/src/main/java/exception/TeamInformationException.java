package exception;

import util.TeamElement;

import java.util.Collections;
import java.util.Map;

public class TeamInformationException extends RuntimeException {
    private Map<TeamElement, String> errors;

    public TeamInformationException(Map<TeamElement, String> errors) {
        super("Fout bij het aanmaken van team");
        this.errors = errors;
    }

    public Map<TeamElement, String> getErrors() {
        return Collections.unmodifiableMap(errors);
    }
}
