import domein.EntityStatus;
import domein.TaakType;
import domein.Taaktemplate;
import domein.TaaktemplateManager;
import dto.TaaktemplateDTO;
import dto.TaaktemplateInputDTO;
import exception.TaaktemplateInformationException;
import lombok.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.TaaktemplateDao;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaakTemplateTest {

    @Mock
    private TaaktemplateDao templateRepo;
    private TaaktemplateManager templateManager;

    private static final TaakType TYPE = TaakType.HERSTELLING;
    private static final String OMSCHRIJVING = "Herstelling machine";
    private static final int DUURTIJD = 45;
    private static final EntityStatus STATUS = EntityStatus.ACTIEF;
    private static final int ID = 1;

    static Stream<Arguments> templateCorrect() {
        return Stream.of(
                //omschrijving
                Arguments.of("templ", DUURTIJD),
                Arguments.of("nieuwe template", DUURTIJD),
                Arguments.of("Preventief onderhoud transportband lijn A 033 west", DUURTIJD),

                //duurtijd
                Arguments.of(OMSCHRIJVING, 15),
                Arguments.of(OMSCHRIJVING, 45),
                Arguments.of(OMSCHRIJVING, 240)
        );
    }

    static Stream<Arguments> templateIncorrect() {
        return Stream.of(
                //omschrijving
                Arguments.of("kort", DUURTIJD),
                Arguments.of("Dit is een net te lange omschrijving Test test test", DUURTIJD),
                Arguments.of(null, DUURTIJD),
                Arguments.of("", DUURTIJD),

                //duurtijd
                Arguments.of(OMSCHRIJVING, -15),
                Arguments.of(OMSCHRIJVING, -6),
                Arguments.of(OMSCHRIJVING, 14),
                Arguments.of(OMSCHRIJVING, 241),
                Arguments.of(OMSCHRIJVING, 40),
                Arguments.of(OMSCHRIJVING, null)
        );
    }

    private TaaktemplateInputDTO defaultTemplateInputDTO(Integer id) {
        return new TaaktemplateInputDTO(id, TYPE, OMSCHRIJVING, DUURTIJD);
    }

    private TaaktemplateDTO defaultTemplateDTO() {
        return new TaaktemplateDTO(1, TYPE, OMSCHRIJVING, DUURTIJD, STATUS);
    }

    private Taaktemplate createTemplate() {
        return new Taaktemplate(TYPE, OMSCHRIJVING, DUURTIJD);
    }

    @BeforeEach
    void setUp() {
        templateManager = new TaaktemplateManager(templateRepo);
        lenient().when(templateRepo.get(1)).thenReturn(createTemplate());
    }

    @Test
    void addTemplate_transactionCommitted() {
        templateManager.saveTemplate(defaultTemplateInputDTO(null));
        verify(templateRepo).startTransaction();
        verify(templateRepo).commitTransaction();
        verify(templateRepo, never()).rollbackTransaction();
    }

    @Test
    void addTemplate_insertWordtAangeroepen() {
        templateManager.saveTemplate(defaultTemplateInputDTO(null));
        verify(templateRepo, times(1)).insert(any(Taaktemplate.class));
    }

    @Test
    void addTemplate_transactionRolledBackOnError() {
        doThrow(new RuntimeException("DB fout")).when(templateRepo).insert(any());
        assertThrows(RuntimeException.class, () -> templateManager.saveTemplate(defaultTemplateInputDTO(null)));
        verify(templateRepo).rollbackTransaction();
        verify(templateRepo, never()).commitTransaction();
    }

    @ParameterizedTest
    @EnumSource(TaakType.class)
    void addTemplate_correctType_maaktTemplate(TaakType type) {
        TaaktemplateInputDTO dto = new TaaktemplateInputDTO(null, type, OMSCHRIJVING, DUURTIJD);
        Taaktemplate result = templateManager.saveTemplate(dto);
        assertEquals(type, result.getType());
    }

    @ParameterizedTest
    @MethodSource("templateCorrect")
    void addTemplate_correcteOmschrijvingEnDuurtijd_maaktTemplate(String omschrijving, Integer duurtijd) {
        TaaktemplateInputDTO dto = new TaaktemplateInputDTO(null, TYPE, omschrijving, duurtijd);
        Taaktemplate result = templateManager.saveTemplate(dto);
        assertEquals(duurtijd, result.getDuurTijd());
        assertEquals(omschrijving, result.getOmschrijving());
    }

    @ParameterizedTest
    @MethodSource("templateIncorrect")
    void addTemplate_ongeldigeOmschrijvingEnDuurtijd_gooitException(String omschrijving,  Integer duurtijd) {
        TaaktemplateInputDTO dto = new TaaktemplateInputDTO(null, TYPE, omschrijving,  duurtijd);
        assertThrows(TaaktemplateInformationException.class, () -> templateManager.saveTemplate(dto));
    }

    @Test
    void editTemplate_roeptUpdateAan() {
        Taaktemplate template = createTemplate();
        when(templateRepo.get(1)).thenReturn(template);
        templateManager.saveTemplate(defaultTemplateInputDTO(1));
        verify(templateRepo).update(template);
    }

    @Test
    void editTemplate_transactionCommitted() {
        templateManager.saveTemplate(defaultTemplateInputDTO(1));
        verify(templateRepo).startTransaction();
        verify(templateRepo).commitTransaction();
        verify(templateRepo, never()).rollbackTransaction();
    }

    @Test
    void editTemplate_transactionRollbackOnError() {
        doThrow(new RuntimeException("DB fout")).when(templateRepo).update(any());
        assertThrows(RuntimeException.class, () -> templateManager.saveTemplate(defaultTemplateInputDTO(1)));
        verify(templateRepo).rollbackTransaction();
        verify(templateRepo, never()).commitTransaction();
    }

    @ParameterizedTest
    @EnumSource(TaakType.class)
    void editTemplate_correctType_wijzigtType(TaakType type) {
        Taaktemplate template = createTemplate();
        TaaktemplateInputDTO dto = new TaaktemplateInputDTO(1, type, OMSCHRIJVING, 45);
        when(templateRepo.get(1)).thenReturn(template);
        templateManager.saveTemplate(dto);
        assertEquals(type, template.getType());
    }

    @ParameterizedTest
    @MethodSource("templateCorrect")
    void editTemplate_correcteOmschrijvingEnDuurtijd_wijzigtVelden(String omschrijving, Integer duurtijd) {
        Taaktemplate template = createTemplate();
        TaaktemplateInputDTO dto = new TaaktemplateInputDTO(1, TYPE, omschrijving, duurtijd);
        when(templateRepo.get(1)).thenReturn(template);
        templateManager.saveTemplate(dto);
        assertEquals(duurtijd, template.getDuurTijd());
        assertEquals(omschrijving, template.getOmschrijving());
    }

    @ParameterizedTest
    @MethodSource("templateIncorrect")
    void editTemplate_incorrecteOmschrijvingEnDuurtijd_gooitException(String omschrijving,  Integer duurtijd) {
        TaaktemplateInputDTO dto = new TaaktemplateInputDTO(1, TYPE, omschrijving, duurtijd);
        assertThrows(TaaktemplateInformationException.class, () -> templateManager.saveTemplate(dto));
    }

    @Test
    void deleteTemplate_zetStatusInactief() {
        Taaktemplate template = createTemplate();
        when(templateRepo.get(1)).thenReturn(template);
        templateManager.deleteTemplate(defaultTemplateDTO());
        assertEquals(EntityStatus.INACTIEF, template.getStatus());
    }

    @Test
    void deleteTemplate_roeptUpdateAan() {
        Taaktemplate template = createTemplate();
        when(templateRepo.get(1)).thenReturn(template);
        templateManager.deleteTemplate(defaultTemplateDTO());
        verify(templateRepo).update(template);
    }

    @Test
    void deleteTemplate_roeptGeenHardDeleteAan() {
        templateManager.deleteTemplate(defaultTemplateDTO());
        verify(templateRepo).startTransaction();
        verify(templateRepo).commitTransaction();
        verify(templateRepo, never()).delete(any());
    }

    @Test
    void deleteTemplate_transactionCommitted() {
        templateManager.deleteTemplate(defaultTemplateDTO());
        verify(templateRepo).startTransaction();
        verify(templateRepo).commitTransaction();
        verify(templateRepo, never()).rollbackTransaction();
    }

    @Test
    void deleteTemplate_transactionRollbackOnError() {
        doThrow(new RuntimeException("DB fout")).when(templateRepo).update(any());
        assertThrows(RuntimeException.class, () -> templateManager.deleteTemplate(defaultTemplateDTO()));
        verify(templateRepo).rollbackTransaction();
        verify(templateRepo, never()).commitTransaction();
    }
}
