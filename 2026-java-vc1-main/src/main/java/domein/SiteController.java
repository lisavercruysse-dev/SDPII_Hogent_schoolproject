package domein;

import dto.SiteDTO;
import dto.SiteInputDTO;
import exception.SiteInformationException;
import repository.SiteDaoJpa;
import util.SiteElement;

import java.util.List;
import java.util.Map;

public class SiteController {
    private final SiteManager siteManager;

    // Constructor voor productie
    public SiteController() {
        this.siteManager = new SiteManager(new SiteDaoJpa());
    }

    // Constructor voor testen (Mockito)
    public SiteController(SiteManager siteManager) {
        this.siteManager = siteManager;
    }

    public SiteDTO getSiteById(int id) {
        Site site = siteManager.getSiteById(id);
        return toDTO(site);
    }

    public List<SiteDTO> getAllSites() {
        List<Site> sites = siteManager.getAllSites();
        return sites.stream()
                .map(this::toDTO)
                .toList();
    }

    public SiteDTO getSiteFromTeam(int teamId) {
        Site site = siteManager.getSiteFromTeam(teamId);
        return toDTO(site);
    }

    public void voegSiteToe(SiteInputDTO dto) throws SiteInformationException{
        if (bestaatNaamAl(dto.name())) {
            throw new SiteInformationException(
                    Map.of(SiteElement.NAME, "Er bestaat al een site met deze naam.")
            );
        }
        siteManager.addSite(dto);
    }

    public void wijzigSite(int id, SiteInputDTO dto) throws SiteInformationException{
        Site huidige = findById(id);
        // Als de naam gewijzigd is → check of nieuwe naam al bestaat
        if (!huidige.getName().equalsIgnoreCase(dto.name()) && bestaatNaamAl(dto.name())) {
            throw new SiteInformationException(
                    Map.of(SiteElement.NAME, "Er bestaat al een site met deze naam.")
            );
        }
        siteManager.updateSite(id, dto);
    }

    public void verwijderSite(int id) {
        siteManager.deleteSite(id);
    }

    // ── hulpmethode ────────────────────────────────────────────────────────────
    private SiteDTO toDTO(Site site) {
        return new SiteDTO(
                site.getId(),
                site.getName(),
                site.getLocatie(),
                site.getLand(),
                site.getCapaciteit(),
                site.getOperationeleStatus() != null
                        ? site.getOperationeleStatus().name()
                        : OperationeleStatus.INACTIEF.name(),
                site.getSiteProductieStatus() != null
                        ? site.getSiteProductieStatus().name()
                        : SiteProductieStatus.OFFLINE.name(),
                site.getBreedtegraad(),
                site.getLengtegraad()
        );
    }

    private boolean bestaatNaamAl(String naam) {
        if (naam == null || naam.isBlank()) return false;

        return siteManager.existsByNaamIgnoreCase(naam.trim());
    }

    private Site findById(int id) {
        Site site = siteManager.getSiteById(id);
        if (site == null) {
            throw new IllegalArgumentException("Site met id " + id + " niet gevonden.");
        }
        return site;
    }
}
