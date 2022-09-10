package com.gestion.conge.service;

import com.gestion.conge.exception.ResourceNotFoundException;
import com.gestion.conge.model.LeaveType;
import com.gestion.conge.repository.LeaveTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Service
@AllArgsConstructor
public class LeaveTypeService {

    private LeaveTypeRepository leaveRepository ;

    public List<LeaveType> getLeavesType(int page, int pageSize, String type, String description) {
        if(page<1){
            throw new RuntimeException("page must be >=1");
        }
        if(pageSize>200){
            throw new RuntimeException("page size too large, must be <=200");
        }
        Pageable pageable = PageRequest.of(page - 1,pageSize,
                Sort.by(ASC,"type"));
        return leaveRepository.findByTypeContainingIgnoreCaseAndDescriptionContainingIgnoreCase(type, description, pageable);

        //return leaveRepository.findAll();
    }

    public LeaveType addLeaveType(LeaveType newleave){

        Optional<LeaveType> leave = leaveRepository.findLeaveByType(newleave.getType());
        if (leave.isPresent()){
            throw new RuntimeException("Leave type already exists");
        }
        leaveRepository.save(newleave);
        return newleave;
    }

    public LeaveType getLeaveTypeById(Long id)  {
        LeaveType leave = leaveRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("LeaveType with id "+id+" does not exists"));
        return leave;
    }

    public LeaveType deleteLeaveTypeById(Long id) {
        LeaveType leave = leaveRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("LeaveType with id "+id+" does not exists"));
        leaveRepository.deleteById(id);
        return leave;
    }

    @Transactional
    public LeaveType modifyLeaveById(Long id, LeaveType newLeave) {
        LeaveType leave = leaveRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("LeaveType with id "+id+" does not exists"));
        if (newLeave.getType()!=null && newLeave.getType().length()>0 && !Objects.equals(newLeave.getType(),leave.getType())){
            leave.setType(newLeave.getType());
        }
        if (newLeave.getDescription()!=null && newLeave.getDescription().length()>0 && !Objects.equals(newLeave.getDescription(),leave.getDescription())){
            leave.setDescription(newLeave.getDescription());
        }
        if (newLeave.getMaxDuration()>0 && !Objects.equals(newLeave.getMaxDuration(),leave.getMaxDuration())){
            leave.setMaxDuration(newLeave.getMaxDuration());
        }
        return leave;
    }
}
