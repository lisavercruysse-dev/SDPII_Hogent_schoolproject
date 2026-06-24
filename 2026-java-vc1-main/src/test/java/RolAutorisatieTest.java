import domein.JobTitel;
import domein.RolAutorisatie;
import domein.RolAutorisatie.NavItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RolAutorisatieTest {

    private Set<NavItem> adminItems;
    private Set<NavItem> managerItems;
    private Set<NavItem> verantwoordelijkeItems;
    private Set<NavItem> werknemerItems;

    @BeforeEach
    public void setUp() {
        adminItems            = RolAutorisatie.getZichtbareNavItems(JobTitel.ADMINISTRATOR);
        managerItems          = RolAutorisatie.getZichtbareNavItems(JobTitel.MANAGER);
        verantwoordelijkeItems = RolAutorisatie.getZichtbareNavItems(JobTitel.VERANTWOORDELIJKE);
        werknemerItems        = RolAutorisatie.getZichtbareNavItems(JobTitel.WERKNEMER);
    }

    // ─── ADMINISTRATOR ────────────────────────────────────────────────────────

    @Test
    public void administrator_heeftToegang_gebruikers() {
        assertTrue(adminItems.contains(NavItem.GEBRUIKERS));
    }

    @Test
    public void administrator_heeftToegang_logs() {
        assertTrue(adminItems.contains(NavItem.LOGS));
    }

    @Test
    public void administrator_heeftToegang_meldingen() {
        assertTrue(adminItems.contains(NavItem.MELDINGEN));
    }

    @Test
    public void administrator_geenToegang_teams() {
        assertFalse(adminItems.contains(NavItem.TEAMS));
    }

    @Test
    public void administrator_geenToegang_sites() {
        assertFalse(adminItems.contains(NavItem.SITES));
    }

    // ─── MANAGER ─────────────────────────────────────────────────────────────

    @Test
    public void manager_heeftToegang_teams() {
        assertTrue(managerItems.contains(NavItem.TEAMS));
    }

    @Test
    public void manager_heeftToegang_sites() {
        assertTrue(managerItems.contains(NavItem.SITES));
    }

    @Test
    public void manager_heeftToegang_meldingen() {
        assertTrue(managerItems.contains(NavItem.MELDINGEN));
    }

    /* heeft geen toegang tot gebruikers nodig volgens mij
    @Test
    public void manager_heeftToegang_gebruikers() {
        assertTrue(managerItems.contains(NavItem.GEBRUIKERS));
    }*/

    // ─── VERANTWOORDELIJKE ────────────────────────────────────────────────────

    @Test
    public void verantwoordelijke_heeftToegang_teams() {
        assertTrue(verantwoordelijkeItems.contains(NavItem.TEAMS));
    }

    @Test
    public void verantwoordelijke_heeftToegang_meldingen() {
        assertTrue(verantwoordelijkeItems.contains(NavItem.MELDINGEN));
    }

    @Test
    public void verantwoordelijke_geenToegang_gebruikers() {
        assertFalse(verantwoordelijkeItems.contains(NavItem.GEBRUIKERS));
    }

    @Test
    public void verantwoordelijke_geenToegang_sites() {
        assertFalse(verantwoordelijkeItems.contains(NavItem.SITES));
    }

    @Test
    public void verantwoordelijke_geenToegang_logs() {
        assertFalse(verantwoordelijkeItems.contains(NavItem.LOGS));
    }

    // ─── WERKNEMER ────────────────────────────────────────────────────────────

    @Test
    public void werknemer_geenEnkeleNavItemZichtbaar() {
        assertTrue(werknemerItems.isEmpty());
    }

    // ─── heeftToegang() hulpmethode ───────────────────────────────────────────

    @Test
    public void heeftToegang_correctVoorAanwezigeItem() {
        assertTrue(RolAutorisatie.heeftToegang(JobTitel.ADMINISTRATOR, NavItem.GEBRUIKERS));
    }

    @Test
    public void heeftToegang_correctVoorAfwezigeItem() {
        assertFalse(RolAutorisatie.heeftToegang(JobTitel.ADMINISTRATOR, NavItem.TEAMS));
    }

    @Test
    public void heeftToegang_werknemerHeeftNergensToegang() {
        for (NavItem item : NavItem.values()) {
            assertFalse(RolAutorisatie.heeftToegang(JobTitel.WERKNEMER, item),
                    "Werknemer mag geen toegang hebben tot: " + item);
        }
    }
}