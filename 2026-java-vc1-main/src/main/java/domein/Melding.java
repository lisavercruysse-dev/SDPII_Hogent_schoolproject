package domein;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.MeldingType;

import java.time.LocalDate;

@Entity
@Table(name = "notificaties")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class Melding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private MeldingType type;

    @Column(name = "title")
    private String titel;

    @Column(name = "description")
    private String detail;

    @Column(name = "time")
    private LocalDate datum;

    @Column(name = "is_read")
    private boolean gelezen;

    @Column(name = "werknemer_id")
    private int werknemerId;

    public Melding(MeldingType type, String titel, String detail, LocalDate datum, boolean gelezen, int werknemerId) {
        this.type = type;
        this.titel = titel;
        this.detail = detail;
        this.datum = datum;
        this.gelezen = gelezen;
        this.werknemerId = werknemerId;
    }
}