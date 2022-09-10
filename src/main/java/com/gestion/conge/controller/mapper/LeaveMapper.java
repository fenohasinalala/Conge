package com.gestion.conge.controller.mapper;

import com.gestion.conge.model.LeaveType;
import org.springframework.stereotype.Component;

@Component
public class LeaveMapper {
    public LeaveType toRestLeave(LeaveType leave){
        LeaveType restLeave = new LeaveType();
        restLeave.setId(leave.getId());
        restLeave.setType(leave.getType());
        restLeave.setDescription(leave.getDescription());
        restLeave.setMaxDuration(leave.getMaxDuration());
        return restLeave;
    }
}
