package com.gestion.conge.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Month;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LeaveSummary {

    private LocalDate startDateRef;

    private LocalDate endDateRef;

    private Month monthRef;

    private Double leaveInProgress;

    private Double leaveGet;

    private Double leaveTaken;

    private Double leaveRemained;

    private Worker worker;



        /*
    public Double getleaveGet() {
        Period dif = Period.between(this.entranceDatetime, LocalDate.now());
        Duration duration = Duration.between(this.entranceDatetime,LocalDate.now());
        Integer MonthAcquisition = dif.getYears()*12+dif.getMonths()+dif.getDays()/30;
        Integer DaysAcquisition = dif.getYears()*12*365+dif.getMonths()*30+dif.getDays();
        return Double.valueOf(MonthAcquisition)*2.5;
    }


    public Double getLeaveTaken() {
        return 100D;
    }

     */
/*
    public Double getLeaveRemained(){

    }

 */
}
