package domein;

import dto.SiteInputDTO;
import repository.SiteDao;

import java.util.List;

public class SiteManager {
    private final SiteDao siteRepo;

    public SiteManager(SiteDao siteRepo) {
        this.siteRepo = siteRepo;
    }

    public Site getSiteById(int id) {
        return siteRepo.get(id);
    }

    public List<Site> getAllSites() {
        List<Site> sites = siteRepo.findAllActive();
        return sites;
    }

    public Site getSiteFromTeam(int teamId) {
        return siteRepo.getSiteFromTeam(teamId);
    }

    public void addSite(SiteInputDTO dto) {
        siteRepo.startTransaction();
        try {
            OperationeleStatus  os = OperationeleStatus.valueOf(dto.operationeleStatus().toUpperCase());
            SiteProductieStatus ps = SiteProductieStatus.valueOf(dto.siteProductieStatus().toUpperCase());

            Site site = Site.builder()
                    .name(dto.name())
                    .locatie(dto.locatie())
                    .land(dto.land())
                    .capaciteit(dto.capaciteit())
                    .operationeleStatus(os)
                    .siteProductieStatus(ps)
                    .breedtegraad(dto.breedtegraad())
                    .lengtegraad(dto.lengtegraad())
                    .build();

            siteRepo.insert(site);
            siteRepo.commitTransaction();
        } catch (Exception e) {
            siteRepo.rollbackTransaction();
            throw e;
        }
    }

    public void updateSite(int id, SiteInputDTO dto) {
        siteRepo.startTransaction();
        try {
            Site site = siteRepo.get(id);
            if (site != null) {
                OperationeleStatus  os = OperationeleStatus.valueOf(dto.operationeleStatus().toUpperCase());
                SiteProductieStatus ps = SiteProductieStatus.valueOf(dto.siteProductieStatus().toUpperCase());

                site.setName(dto.name());
                site.setLocatie(dto.locatie());
                site.setLand(dto.land());
                site.setCapaciteit(dto.capaciteit());
                site.setOperationeleStatus(os);
                site.setSiteProductieStatus(ps);
                site.setBreedtegraad(dto.breedtegraad());
                site.setLengtegraad(dto.lengtegraad());

                siteRepo.update(site);
            }
            siteRepo.commitTransaction();
        } catch (Exception e) {
            siteRepo.rollbackTransaction();
            throw e;
        }
    }

    public void deleteSite(int id) {
        siteRepo.startTransaction();
        try {
            Site site = siteRepo.get(id);
            if (site != null) {
                site.softDelete();
                siteRepo.update(site);
            }
            siteRepo.commitTransaction();
        } catch (Exception e) {
            siteRepo.rollbackTransaction();
            throw e;
        }
    }

    public boolean existsByNaamIgnoreCase(String naam) {
        return siteRepo.existsByNaamIgnoreCase(naam);
    }
}
