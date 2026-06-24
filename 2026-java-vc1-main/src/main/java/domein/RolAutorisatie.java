package domein;

import java.util.EnumSet;
import java.util.Set;

public class RolAutorisatie {

    public enum NavItem { //TODO: toevoegen tasks
        GEBRUIKERS,
        TEAMS,
        SITES,
        LOGS,
        MELDINGEN,
        MACHINES,
        TAKEN
    }

    /**
     * Rechten per rol: (zie product backlog - Java)
     *
     * ADMIN           → Gebruikers, Logs, Meldingen
     * VERANTWOORDELIJKE → Teams, Meldingen, Machine, Taken (TODO)
     * MANAGER         → Teams, Sites, Logs, Meldingen, Machine
     */
    public static Set<NavItem> getZichtbareNavItems(JobTitel rol) {
        return switch (rol) {
            case ADMINISTRATOR -> EnumSet.of(
                    NavItem.GEBRUIKERS,
                    NavItem.LOGS,
                    NavItem.MELDINGEN
            );
            case MANAGER -> EnumSet.of(
                    //TODO: verwijderen, enkel admin toegang tot tab Gebruikers
                    NavItem.TEAMS,
                    NavItem.SITES,
                    NavItem.LOGS,
                    NavItem.MELDINGEN,
                    NavItem.MACHINES
            );
            case VERANTWOORDELIJKE -> EnumSet.of(
                    NavItem.TEAMS, //TODO: verantwoordelijke mag enkel aan hem toegewezen teams zien
                    NavItem.MELDINGEN,
                    NavItem.MACHINES,
                    NavItem.TAKEN
            );
            case WERKNEMER -> EnumSet.noneOf(NavItem.class);
        };
    }

    public static boolean heeftToegang(JobTitel rol, NavItem item) {
        return getZichtbareNavItems(rol).contains(item);
    }
}