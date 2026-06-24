package domein;

import repository.MeldingDao;
import util.MeldingType;

import java.time.LocalDate;
import java.util.List;

public class MeldingManager {
    private final MeldingDao meldingRepo;

    public MeldingManager(MeldingDao meldingRepo) {
        this.meldingRepo = meldingRepo;
    }

    public List<Melding> getMeldingenVoorWerknemer(int werknemerId) {
        return meldingRepo.findAllForWerknemer(werknemerId);
    }

    public void addMeldingVoorGroep(MeldingType type, String titel, String detail, List<Integer> ontvangerIds) {
        if (ontvangerIds == null || ontvangerIds.isEmpty()) return;

        meldingRepo.startTransaction();
        for (int werknemerId : ontvangerIds) {
            Melding nieuweMelding = new Melding(type, titel, detail, LocalDate.now(), false, werknemerId);
            meldingRepo.insert(nieuweMelding);
        }
        meldingRepo.commitTransaction();
    }

    public void markeerAlsGelezen(long id) {
        Melding m = meldingRepo.get(id);
        if (m != null) {
            m.setGelezen(true);
            meldingRepo.startTransaction();
            meldingRepo.update(m);
            meldingRepo.commitTransaction();
        }
    }

    public void markeerAllesAlsGelezen(int werknemerId) {
        List<Melding> meldingen = getMeldingenVoorWerknemer(werknemerId);
        meldingRepo.startTransaction();
        for (Melding m : meldingen) {
            if (!m.isGelezen()) {
                m.setGelezen(true);
                meldingRepo.update(m);
            }
        }
        meldingRepo.commitTransaction();
    }

    public void deleteMelding(long id) {
        Melding m = meldingRepo.get(id);
        if (m != null) {
            meldingRepo.startTransaction();
            meldingRepo.delete(m);
            meldingRepo.commitTransaction();
        }
    }
}