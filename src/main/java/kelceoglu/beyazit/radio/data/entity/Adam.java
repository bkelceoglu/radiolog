package kelceoglu.beyazit.radio.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author: beyazit kelceoglu
 * @email: beyazit <at> kelceoglu.com
 * @project: sc
 * @date: 30.06.2021 20:39
 * Doc:
 *
 * Pcall
 * PWWLo
 * Psect
 * PBand
 * RName
 * PCLub
 * Radr1 ve 2
 * Rcity
 * Rcoun
 *
 */
@Entity
@Table(name = "adam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Adam implements Serializable
{
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long oid;
    private String email;
    private String Pcall;
    private String PWWLo;
    private String Psect;
    private String PBand;
    private String RName;
    private String PCLub;
    private String Radr1;
    private String Radr2;
    private String Rcity;
    private String Rcoun;
    private int cqsop;
    private int qsorecords;
    
    
    @Column (columnDefinition = "TEXT")
    private String dosya;

}