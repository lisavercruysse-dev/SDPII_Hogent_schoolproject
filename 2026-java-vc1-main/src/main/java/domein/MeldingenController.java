package domein;

import dto.MeldingDTO;
import repository.MeldingDaoJpa;
import util.MeldingType;

import java.util.List;

public class MeldingenController {

    private final MeldingManager meldingManager;
    private final int ingelogdeWerknemerId;

    private final List<MeldingType> javaTypes = List.of(
            MeldingType.TEAM_AANGEMAAKT,
            MeldingType.WERKNEMER_TOEGEVOEGD,
            MeldingType.SYSTEEM
    );

    public MeldingenController(int ingelogdeWerknemerId) {
        this.meldingManager = new MeldingManager(new MeldingDaoJpa());
        this.ingelogdeWerknemerId = ingelogdeWerknemerId;
    }

    private MeldingDTO toDTO(Melding m) {
        return new MeldingDTO(m.getId(), m.getType().getDisplay(),
                m.getTitel(), m.getDetail(), m.getDatum(), m.isGelezen());
    }

    public List<MeldingDTO> getGefilterdeMeldingen(Boolean isGelezen, MeldingType type) {
        return meldingManager.getMeldingenVoorWerknemer(ingelogdeWerknemerId).stream()
                .filter(m -> javaTypes.contains(m.getType()))
                .filter(m -> isGelezen == null || m.isGelezen() == isGelezen)
                .filter(m -> type == null || m.getType() == type)
                .map(this::toDTO)
                .toList();
    }

    public List<String> getMeldingTypes() {
        return javaTypes.stream()
                .map(MeldingType::getDisplay)
                .toList();
    }

    public void voegMeldingToeVoorGroep(MeldingType type, String titel, String detail, List<Integer> ontvangerIds) {
        meldingManager.addMeldingVoorGroep(type, titel, detail, ontvangerIds);
    }

    public void markeerAlsGelezen(long id) {
        meldingManager.markeerAlsGelezen(id);
    }

    public void markeerAllesAlsGelezen() {
        List<Melding> javaMeldingen = meldingManager.getMeldingenVoorWerknemer(ingelogdeWerknemerId).stream()
                .filter(m -> javaTypes.contains(m.getType()))
                .toList();

        for (Melding m : javaMeldingen) {
            if (!m.isGelezen()) {
                meldingManager.markeerAlsGelezen(m.getId());
            }
        }
    }

    public void verwijderMelding(long id) {
        meldingManager.deleteMelding(id);
    }

    public long getAantalOngelezen() {
        return meldingManager.getMeldingenVoorWerknemer(ingelogdeWerknemerId).stream()
                .filter(m -> javaTypes.contains(m.getType()))
                .filter(m -> !m.isGelezen())
                .count();
    }
}