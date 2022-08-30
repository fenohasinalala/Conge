package com.gestion.conge.model;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    private String phone;

    @NotBlank(message = "CIN number is mandatory")
    @Column(unique=true)
    private String cin;

    private LocalDate birthDate;

    //for Date type
    //@Temporal(TemporalType.DATE)
    //@JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate entranceDatetime;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @NotBlank(message = "Address is mandatory")
    private String address;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Transient
    private Double leaveGet;

    @Transient
    private Double leaveTaken;

    @Transient
    private Double leaveRemained;


    public enum Sex {
        F,M;
    }

    public Double getleaveGet() {
        Period dif = Period.between(this.entranceDatetime, LocalDate.now());
        Integer MonthAcquisition = dif.getYears()*12+dif.getMonths()+dif.getDays()/30;

        return 1D;
    }



    public Double getLeaveTaken() {
        return 100D;
    }
/*
    public Double getLeaveRemained(){

    }

 */

}
