package com.gestion.conge.service;

import com.gestion.conge.model.Leave;
import com.gestion.conge.model.LeaveTaken;
import com.gestion.conge.model.Worker;
import com.gestion.conge.repository.LeaveRepository;
import com.gestion.conge.repository.LeaveTakenRepository;
import com.gestion.conge.repository.WorkerRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@AllArgsConstructor
public class LeaveTakenService {

    private LeaveTakenRepository leaveTakenRepository ;
    private WorkerRepository workerRepository;

    private LeaveRepository leaveRepository;

    public List<LeaveTaken> getLeaves(int page, int pageSize) {
        if(page<1){
            throw new RuntimeException("page must be >=1");
        }
        if(pageSize>200){
            throw new RuntimeException("page size too large, must be <=200");
        }
        Pageable pageable = PageRequest.of(page - 1,pageSize,
                Sort.by(ASC,"startDate"));
        return leaveTakenRepository.findAll(pageable).toList();
    }

    @Transactional
    public LeaveTaken addLeave(LeaveTaken leaveTaken) {
        Worker validWorker = workerRepository.findById(leaveTaken.getWorker().getId())
                .orElseThrow();
        Leave validLeaveType = leaveRepository.findById(leaveTaken.getLeave().getId())
                .orElseThrow();
        LeaveTaken newLeaveTaken = leaveTaken;
        if( Period.between(LocalDate.now(), leaveTaken.getStartDate()).getDays()>=0){
            newLeaveTaken.setWorker(validWorker);
            newLeaveTaken.setLeave(validLeaveType);
            leaveTakenRepository.save(newLeaveTaken);
        }
        return newLeaveTaken;
    }

    public LeaveTaken getLeaveTakenById(Long id) {
        LeaveTaken leaveTaken = leaveTakenRepository.findById(id)
                .orElseThrow(()->new RuntimeException("LeaveTaken with id "+id+" does not exists"));
        return leaveTaken;
    }

    public LeaveTaken deleteLeaveById(Long id) {
        LeaveTaken leaveTaken = leaveTakenRepository.findById(id)
                .orElseThrow(()->new RuntimeException("LeaveTaken with id "+id+" does not exists"));
        leaveTakenRepository.delete(leaveTaken);
        return leaveTaken;
    }
}
