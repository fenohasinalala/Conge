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
import java.time.Year;
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

    private LeaveTypeService leaveTypeService;

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
            throw new BadRequestException("LeaveTaken's start Date must be before end Date");
        }
        Worker validWorker = workerRepository.findById(leaveTaken.getWorker().getId())
                .orElseThrow(()->new ResourceNotFoundException("Worker with id "+leaveTaken.getWorker().getId()+" does not exists"));
        LeaveType validLeaveType = leaveRepository.findById(leaveTaken.getLeaveType().getId())
                .orElseThrow(()->new ResourceNotFoundException("Leaver Type with id "+leaveTaken.getLeaveType().getId()+" does not exists"));
        LeaveTaken newLeaveTaken = leaveTaken;
        double leaveRemained = getLeaveSummaryByWorkerId(leaveTaken.getWorker().getId(),Year.now(),LocalDate.now().getMonth()).getLeaveRemained();
        if (DAYS.between(leaveTaken.getStartDate(),validWorker.getEntranceDatetime())>0){
            throw new BadRequestException("Leave should be after worker entrance date");
        }
        if(leaveTaken.getLeaveType().getId()==1 && leaveTaken.getDuration()>leaveRemained){
            throw new BadRequestException("Leave remained: "+leaveRemained +" is less than request Leave duration: "+leaveTaken.getDuration());
        }
        if (leaveTaken.getDuration()>validLeaveType.getMaxDuration()){
            throw new BadRequestException("Duration: "+leaveTaken.getDuration() +" exceed permit duration for this type of leave: "+validLeaveType.getMaxDuration());
        }
        for (LeaveTaken i:getLeaveTakenByWorkerId(leaveTaken.getWorker().getId())) {
                if (((i.getStartDate().isBefore(leaveTaken.getStartDate())|| DAYS.between(i.getStartDate(),leaveTaken.getStartDate())==0) &&  i.getEndDate().isBefore(leaveTaken.getEndDate()) && (i.getEndDate().isAfter(leaveTaken.getStartDate()) || DAYS.between(i.getEndDate(),leaveTaken.getStartDate())==0))
                        || (i.getStartDate().isAfter(leaveTaken.getStartDate()) && i.getEndDate().isBefore(leaveTaken.getEndDate()))
                        || ((i.getStartDate().isBefore(leaveTaken.getStartDate())|| DAYS.between(i.getStartDate(),leaveTaken.getStartDate())==0) && (i.getEndDate().isAfter(leaveTaken.getEndDate()) || DAYS.between(i.getEndDate(),leaveTaken.getEndDate())==0))
                        || ((i.getStartDate().isBefore(leaveTaken.getEndDate())|| DAYS.between(i.getStartDate(),leaveTaken.getEndDate())==0) &&  i.getStartDate().isAfter(leaveTaken.getStartDate()) && (i.getEndDate().isAfter(leaveTaken.getEndDate()) || DAYS.between(i.getEndDate(),leaveTaken.getEndDate())==0)))
                {
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
            throw new BadRequestException("LeaveTaken's start Date must be before end Date");
        }
        Worker validWorker = workerRepository.findById(leaveTaken.getWorker().getId())
                .orElseThrow(()->new ResourceNotFoundException("Worker with id "+leaveTaken.getWorker().getId()+" does not exists"));
        LeaveType validLeaveType = leaveRepository.findById(leaveTaken.getLeaveType().getId())
                .orElseThrow(()->new ResourceNotFoundException("Leaver Type with id "+leaveTaken.getLeaveType().getId()+" does not exists"));
        LeaveTaken newLeaveTaken = leaveTakenRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("LeaveTaken with id "+id+" does not exists"));
        if (DAYS.between(leaveTaken.getStartDate(),validWorker.getEntranceDatetime())>0){
            throw new BadRequestException("Leave should be after worker entrance date");
        }
        if (leaveTaken.getDuration()>validLeaveType.getMaxDuration()){
            throw new BadRequestException("Duration: "+leaveTaken.getDuration() +" exceed permit duration for this type of leave: "+validLeaveType.getMaxDuration());
        }
        for (LeaveTaken i:getLeaveTakenByWorkerId(leaveTaken.getWorker().getId())) {
            if (i.getId()!=newLeaveTaken.getId()){
                if (((i.getStartDate().isBefore(leaveTaken.getStartDate())|| DAYS.between(i.getStartDate(),leaveTaken.getStartDate())==0) &&  i.getEndDate().isBefore(leaveTaken.getEndDate()) && (i.getEndDate().isAfter(leaveTaken.getStartDate()) || DAYS.between(i.getEndDate(),leaveTaken.getStartDate())==0))
                        || (i.getStartDate().isAfter(leaveTaken.getStartDate()) && i.getEndDate().isBefore(leaveTaken.getEndDate()))
                        || ((i.getStartDate().isBefore(leaveTaken.getStartDate())|| DAYS.between(i.getStartDate(),leaveTaken.getStartDate())==0) && (i.getEndDate().isAfter(leaveTaken.getEndDate()) || DAYS.between(i.getEndDate(),leaveTaken.getEndDate())==0))
                        || ((i.getStartDate().isBefore(leaveTaken.getEndDate())|| DAYS.between(i.getStartDate(),leaveTaken.getEndDate())==0) &&  i.getStartDate().isAfter(leaveTaken.getStartDate()) && (i.getEndDate().isAfter(leaveTaken.getEndDate()) || DAYS.between(i.getEndDate(),leaveTaken.getEndDate())==0)))
                {
                    throw new BadRequestException("Dates must change, this person already has a leave of absence planned on these dates");
                }
            }
        }
        newLeaveTaken.setStartDate(leaveTaken.getStartDate());
        newLeaveTaken.setEndDate(leaveTaken.getEndDate());
        newLeaveTaken.setComment(leaveTaken.getComment());
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

    public LeaveSummary getLeaveSummaryByWorkerId(Long id, Year YearRef, Month MonthRef) {
        Worker validWorker = workerRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Worker with id "+id+" does not exists"));
        List<LeaveTaken> listLeave = getLeaveTakenByWorkerId(id);
        LocalDate periodeRef;
        if(YearRef!=null && MonthRef !=null){
            periodeRef = LocalDate.of(YearRef.getValue(),MonthRef,1);
        } else if(YearRef==null && MonthRef==null){
            periodeRef = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        } else {
            throw new BadRequestException("Year reference and month reference must be specified together or not");
        }

        // period to check
        LocalDate date = periodeRef.plusMonths(1);
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
        if (DAYS.between(date,validWorker.getEntranceDatetime())>0){
            throw new BadRequestException("Year reference and month reference must be after worker entrance date");
        }
        if (YearRef.getValue()==validWorker.getEntranceDatetime().getYear()){
            DebutRef= validWorker.getEntranceDatetime();
            PreviousDebutRef= validWorker.getEntranceDatetime();
        }
        LeaveSummary leaveSummary = new LeaveSummary();

        double totalLeaveInProgress = (2.5*(double) ChronoUnit.MONTHS.between(DebutRef.withDayOfMonth(1),date.withDayOfMonth(1)));
        double totalLeaveNotPaidTaken=0;
        double daysOfWork=DAYS.between(DebutRef.withDayOfMonth(1),date.withDayOfMonth(1));
        leaveSummary.setLeaveInProgress(calculateLeaveGet(listLeave, totalLeaveNotPaidTaken, DebutRef,  date,  daysOfWork,  totalLeaveInProgress));

        double totalLeavePaidTaken=0;
        for (LeaveTaken e: listLeave) {
            if (e.getLeaveType().getId()==1){//"congé payé"
                totalLeavePaidTaken = totalLeavePaidTaken+getTotalLeaveTaken(e,DebutRef,date);
            }
        }
        leaveSummary.setLeaveTaken(totalLeavePaidTaken);

        double totalLeaveGet=30;
        double totalPreviousLeaveNotPaidTaken=0;
        double PreviousDaysOfWork=52*6; //52weeks of work per month * 6days per week
        if (YearRef.getValue()==validWorker.getEntranceDatetime().getYear()){
            totalLeaveGet= 0;
            PreviousDaysOfWork= 0;
        }
        leaveSummary.setLeaveGet(calculateLeaveGet(listLeave, totalPreviousLeaveNotPaidTaken, PreviousDebutRef,  PreviousEndRef,  PreviousDaysOfWork,  totalLeaveGet));

        leaveSummary.setLeaveRemained(leaveSummary.getLeaveGet()-leaveSummary.getLeaveTaken());
        leaveSummary.setWorker(validWorker);
        leaveSummary.setStartDateRef(DebutRef);
        leaveSummary.setEndDateRef(EndRef);
        leaveSummary.setMonthRef(periodeRef.getMonth());
        return leaveSummary;
    }


    public Double getTotalLeaveTaken(LeaveTaken e, LocalDate DebutRef, LocalDate date) {
        double totalLeaveTaken = 0;
        if ((e.getStartDate().isBefore(DebutRef)|| DAYS.between(e.getStartDate(),DebutRef)==0) &&  e.getEndDate().isBefore(date) && (e.getEndDate().isAfter(DebutRef) || DAYS.between(e.getEndDate(),DebutRef)==0)){
            if (DAYS.between(e.getEndDate(),DebutRef)==0){
                totalLeaveTaken += 1;
            }else {
                totalLeaveTaken = totalLeaveTaken + DAYS.between(DebutRef,e.getEndDate())+1;
            }

        } else if (e.getStartDate().isAfter(DebutRef) && e.getEndDate().isBefore(date)) {
            if (DAYS.between(e.getStartDate(),e.getEndDate())==0){
                totalLeaveTaken += 1;
            }else {
                totalLeaveTaken = totalLeaveTaken + DAYS.between(e.getStartDate(),e.getEndDate())+1;
            }
        } else if ((e.getStartDate().isBefore(DebutRef)|| DAYS.between(e.getStartDate(),DebutRef)==0) && (e.getEndDate().isAfter(date) || DAYS.between(e.getEndDate(),date)==0)) {
            totalLeaveTaken = totalLeaveTaken + DAYS.between(DebutRef,date)+1;
        } else if ((e.getStartDate().isBefore(date)|| DAYS.between(e.getStartDate(),date)==0) &&  e.getStartDate().isAfter(DebutRef) && (e.getEndDate().isAfter(date) || DAYS.between(e.getEndDate(),date)==0)){
            if (DAYS.between(e.getStartDate(),date)==0){
                totalLeaveTaken += 1;
            }else {
                totalLeaveTaken = totalLeaveTaken + DAYS.between(e.getStartDate(),date)+1;
            }
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
            return Double.valueOf(Math.round(totalLeaveInProgress));
        }
    }
}
