package kelceoglu.beyazit.radio.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: beyazit kelceoglu
 * @email: beyazit <at> kelceoglu.com
 * @project: sc
 * @date: 30.06.2021 20:39
 * Doc:
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegValsEntity implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "oid", nullable = false)
    private Long oid;

    private String Tdate;
    private String PWWLo;
    private String PBand;
    private String PCLub;
    private String Rcity;
    private String Rcoun;
    private String cqsos;
    private int cqsop;
    @ElementCollection(targetClass=String.class)
    private List<String> qsorecords = new ArrayList<> ();
    
}
