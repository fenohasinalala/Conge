package com.gestion.conge.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.DayOfWeek;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class LeaveTaken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate startDate;
    //@JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate endDate;
    // Durée à afficher

    @Transient
    private Integer duration;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "leave_id", nullable = false)
    private LeaveType leaveType;

    @ManyToOne
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;


    public Integer getDuration() {
        Integer d = 1;
        long t = DAYS.between(this.startDate, this.endDate);
        LocalDate current = this.startDate;
        if(t==0){
            return d;
        }else {
            for (int i = 0; i < t; i++) {
                current = current.plusDays(1);
                if(current.getDayOfWeek()!= DayOfWeek.SUNDAY){
                    d+=1;
                }
            }
            //Period.between(this.startDate, this.endDate).getDays();
            return d;
        }

    }
}
