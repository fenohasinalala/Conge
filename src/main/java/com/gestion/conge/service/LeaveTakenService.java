package com.gestion.conge.service;

import com.gestion.conge.exception.BadRequestException;
import com.gestion.conge.exception.ResourceNotFoundException;
import com.gestion.conge.model.LeaveSummary;
import com.gestion.conge.model.LeaveTaken;
import com.gestion.conge.model.LeaveType;
import com.gestion.conge.model.Worker;
import com.gestion.conge.model.validation.LeaveTakenValidator;
import com.gestion.conge.repository.LeaveTakenRepository;
import com.gestion.conge.repository.LeaveTypeRepository;
import com.gestion.conge.repository.WorkerRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@AllArgsConstructor
public class LeaveTakenService {
    private LeaveTakenRepository leaveTakenRepository ;
    private LeaveTakenValidator leaveTakenValidator;
    private WorkerRepository workerRepository;
    private LeaveTypeRepository leaveRepository;

    public List<LeaveTaken> getLeaves(int page, int pageSize) {
        if(page<1){
            throw new BadRequestException("page must be >=1");
        }
        if(pageSize>200){
            throw new BadRequestException("page size too large, must be <=200");
        }
        Pageable pageable = PageRequest.of(page - 1,pageSize,
                Sort.by(ASC,"startDate"));
        return leaveTakenRepository.findAll(pageable).toList();
    }


    @Transactional
    public LeaveTaken addLeave(LeaveTaken leaveTaken) {

        if (leaveTaken.getId()!=null){
            throw new BadRequestException("post request don't need id");
        }
        leaveTakenValidator.accept(leaveTaken);
        if(leaveTaken.getStartDate().isAfter(leaveTaken.getEndDate())){
            throw new BadRequestException("LeaveTaken's startDate must be before endtDate");
        }
        Worker validWorker = workerRepository.findById(leaveTaken.getWorker().getId())
                .orElseThrow(()->new ResourceNotFoundException("Worker with id "+leaveTaken.getWorker().getId()+" does not exists"));
        LeaveType validLeaveType = leaveRepository.findById(leaveTaken.getLeaveType().getId())
                .orElseThrow(()->new ResourceNotFoundException("Leaver Type with id "+leaveTaken.getLeaveType().getId()+" does not exists"));
        LeaveTaken newLeaveTaken = leaveTaken;
        double leaveRemained = getLeaveSummaryByWorkerId(leaveTaken.getWorker().getId()).getLeaveRemained();
        if(leaveTaken.getDuration()>leaveRemained){
            throw new BadRequestException("Leave remained: "+leaveRemained +"is less than request Leave duration: "+leaveTaken.getDuration());
        }

        for (LeaveTaken i:getLeaveTakenByWorkerId(leaveTaken.getWorker().getId())) {
            if ((i.getStartDate().isBefore(leaveTaken.getStartDate()) && i.getEndDate().isAfter(leaveTaken.getStartDate())) ||
                    (i.getEndDate().isAfter(leaveTaken.getEndDate()) && i.getStartDate().isAfter(leaveTaken.getStartDate())) ||
                    (i.getStartDate().isBefore(leaveTaken.getStartDate()) && i.getEndDate().isAfter(leaveTaken.getEndDate())) ||
                    (i.getStartDate().isAfter(leaveTaken.getStartDate()) && i.getEndDate().isBefore(leaveTaken.getEndDate()))
            ){
                throw new BadRequestException("Dates must change, this person already has a leave of absence planned on these dates");
            }
        }
            newLeaveTaken.setWorker(validWorker);
            newLeaveTaken.setLeaveType(validLeaveType);
            leaveTakenRepository.save(newLeaveTaken);
        return newLeaveTaken;
    }


    public LeaveTaken getLeaveTakenById(Long id) {
        LeaveTaken leaveTaken = leaveTakenRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("LeaveTaken with id "+id+" does not exists"));
        return leaveTaken;
    }


    public LeaveTaken deleteLeaveById(Long id) {
        LeaveTaken leaveTaken = leaveTakenRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("LeaveTaken with id "+id+" does not exists"));
        leaveTakenRepository.delete(leaveTaken);
        return leaveTaken;
    }


    public LeaveTaken putModificationLeaveById(Long id, LeaveTaken leaveTaken) {
        leaveTakenValidator.accept(leaveTaken);
        if(leaveTaken.getStartDate().isAfter(leaveTaken.getEndDate())){
            throw new BadRequestException("LeaveTaken's startDate must be before endtDate");
        }
        Worker validWorker = workerRepository.findById(leaveTaken.getWorker().getId())
                .orElseThrow(()->new ResourceNotFoundException("Worker with id "+leaveTaken.getWorker().getId()+" does not exists"));
        LeaveType validLeaveType = leaveRepository.findById(leaveTaken.getLeaveType().getId())
                .orElseThrow(()->new ResourceNotFoundException("Leaver Type with id "+leaveTaken.getLeaveType().getId()+" does not exists"));
        LeaveTaken newLeaveTaken = leaveTakenRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("LeaveTaken with id "+id+" does not exists"));

        for (LeaveTaken i:getLeaveTakenByWorkerId(leaveTaken.getWorker().getId())) {
            if (!i.equals(newLeaveTaken)){
                if ((i.getStartDate().isBefore(leaveTaken.getStartDate()) && i.getEndDate().isAfter(leaveTaken.getStartDate())) ||
                        (i.getEndDate().isAfter(leaveTaken.getEndDate()) && i.getStartDate().isAfter(leaveTaken.getStartDate())) ||
                        (i.getStartDate().isBefore(leaveTaken.getStartDate()) && i.getEndDate().isAfter(leaveTaken.getEndDate())) ||
                        (i.getStartDate().isAfter(leaveTaken.getStartDate()) && i.getEndDate().isBefore(leaveTaken.getEndDate()))
                ){
                    throw new BadRequestException("Dates must change, this person already has a leave of absence planned on these dates");
                }
            }
        }
            newLeaveTaken.setWorker(validWorker);
            newLeaveTaken.setLeaveType(validLeaveType);
            leaveTakenRepository.save(newLeaveTaken);

        return newLeaveTaken;

    }


    public List<LeaveTaken> getLeaveTakenByWorkerId(Long id) {
        Worker validWorker = workerRepository.findById(id)
            .orElseThrow(()->new ResourceNotFoundException("Worker with id "+id+" does not exists"));
        return leaveTakenRepository.findByWorkerId(id);
    }

    public LeaveSummary getLeaveSummaryByWorkerId(Long id) {
        Worker validWorker = workerRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Worker with id "+id+" does not exists"));

        List<LeaveTaken> listLeave = getLeaveTakenByWorkerId(id);

        // period to check
        LocalDate date = LocalDate.now();
        // reference period June 1 to May 31 of the following year
        LocalDate DebutRef;
        LocalDate EndRef;
        LocalDate PreviousDebutRef;
        LocalDate PreviousEndRef;

        if(date.isBefore(LocalDate.of(date.getYear(), Month.MAY, 31))){
            DebutRef = LocalDate.of(date.getYear()-1, Month.JUNE, 1);
            EndRef = LocalDate.of(date.getYear(), Month.MAY, 31);
            PreviousDebutRef = LocalDate.of(date.getYear()-2, Month.JUNE, 1);
            PreviousEndRef = LocalDate.of(date.getYear()-1, Month.MAY, 31);
        }else {
            DebutRef = LocalDate.of(date.getYear(), Month.JUNE, 1);
            EndRef = LocalDate.of(date.getYear()+1, Month.MAY, 31);
            PreviousDebutRef = LocalDate.of(date.getYear()-1, Month.JUNE, 1);
            PreviousEndRef = LocalDate.of(date.getYear(), Month.MAY, 31);
        }
        LeaveSummary leaveSummary = new LeaveSummary();

        double totalLeaveInProgress = (2.5*(double) ChronoUnit.MONTHS.between(DebutRef.withDayOfMonth(1),date.withDayOfMonth(1)));
        double totalLeaveNotPaidTaken=0;
        double daysOfWork=DAYS.between(DebutRef.withDayOfMonth(1),date.withDayOfMonth(1));
        leaveSummary.setLeaveInProgress(calculateLeaveGet(listLeave, totalLeaveNotPaidTaken, DebutRef,  date,  daysOfWork,  totalLeaveInProgress));

        double totalLeavePaidTaken=0;
        for (LeaveTaken e: listLeave) {
            if (e.getLeaveType().getId()!=1){//"congé payé"
                totalLeavePaidTaken = totalLeavePaidTaken+getTotalLeaveTaken(e,DebutRef,date);
            }
        }
        leaveSummary.setLeaveTaken(totalLeavePaidTaken);

        double totalLeaveGet=30;
        double totalPreviousLeaveNotPaidTaken=0;
        double PreviousDaysOfWork=52*6; //52weeks of work per month * 6days per week
        leaveSummary.setLeaveGet(calculateLeaveGet(listLeave, totalPreviousLeaveNotPaidTaken, PreviousDebutRef,  PreviousEndRef,  PreviousDaysOfWork,  totalLeaveGet));

        leaveSummary.setLeaveRemained(leaveSummary.getLeaveGet()-leaveSummary.getLeaveTaken());
        leaveSummary.setWorker(validWorker);
        leaveSummary.setStartDateRef(DebutRef);
        leaveSummary.setEndDateRef(EndRef);
        leaveSummary.setMonthRef(date.getMonth());
        return leaveSummary;
    }


    public Double getTotalLeaveTaken(LeaveTaken e, LocalDate DebutRef, LocalDate date) {
        double totalLeaveTaken = 0;
        if (e.getStartDate().isBefore(DebutRef) && e.getEndDate().isAfter(DebutRef) && e.getEndDate().isBefore(date)){
            totalLeaveTaken = totalLeaveTaken + DAYS.between(DebutRef,e.getEndDate());
        } else if (e.getStartDate().isAfter(DebutRef) && e.getEndDate().isBefore(date)) {
            if (DAYS.between(e.getStartDate(),e.getEndDate())==0){
                totalLeaveTaken += 1;
            }else {
                totalLeaveTaken = totalLeaveTaken + DAYS.between(e.getStartDate(),e.getEndDate());
            }
        } else if (e.getStartDate().isBefore(DebutRef) && e.getEndDate().isAfter(date)) {
            totalLeaveTaken = totalLeaveTaken + DAYS.between(DebutRef,date);
        } else if (e.getStartDate().isBefore(date) && e.getStartDate().isAfter(DebutRef) && e.getEndDate().isAfter(date)) {
            totalLeaveTaken = totalLeaveTaken + DAYS.between(e.getStartDate(),date);
        }
        return totalLeaveTaken;
    }


    public Double calculateLeaveGet(List<LeaveTaken> listLeave, Double totalLeaveNotPaidTaken,LocalDate DebutRef, LocalDate date, Double daysOfWork, Double totalLeaveInProgress){
        for (LeaveTaken e: listLeave) {
            if (e.getLeaveType().getId()!=1){//"congé payé"
                totalLeaveNotPaidTaken = totalLeaveNotPaidTaken+getTotalLeaveTaken(e,DebutRef,date);
            }
        }
        if(totalLeaveNotPaidTaken<24){
            return totalLeaveInProgress;
        }else {
            daysOfWork=daysOfWork-totalLeaveNotPaidTaken;
            totalLeaveInProgress=daysOfWork*30/288; // 288days = 12months * 4weeks of work per month * 6days per week
            return totalLeaveInProgress;
        }
    }
}
