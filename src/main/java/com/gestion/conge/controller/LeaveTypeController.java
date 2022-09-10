package com.gestion.conge.controller;

import com.gestion.conge.controller.mapper.LeaveMapper;
import com.gestion.conge.model.LeaveType;
import com.gestion.conge.service.LeaveTypeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
public class LeaveTypeController {
    private LeaveTypeService leaveService;
    private LeaveMapper leaveMapper;

    @GetMapping("/leave-type")
    public List<LeaveType> getLeavesType(@RequestParam int page,
                                         @RequestParam(value = "page_size") int pageSize,
                                         @RequestParam(value = "type",required = false , defaultValue = "") String type,
                                         @RequestParam(value = "description",required = false , defaultValue = "") String description){
        return leaveService.getLeavesType(page, pageSize, type, description)
                .stream()
                .map(leaveMapper::toRestLeave)
                .toList();
    }


    @PostMapping("/leave-type")
    public LeaveType addLeaveType(@Valid @RequestBody LeaveType newleave) throws Exception {
        return leaveMapper.toRestLeave(leaveService.addLeaveType(newleave)) ;
    }

    @GetMapping("/leave-type/{id}")
    public LeaveType getLeaveTypeById(@PathVariable Long id) throws Exception {
        return leaveService.getLeaveTypeById(id);
    }

    @DeleteMapping("/leave-type/{id}")
    public LeaveType deleteLeaveTypeById(@PathVariable Long id) throws Exception {
        return leaveService.deleteLeaveTypeById(id);
    }

    @PutMapping("/leave-type/{id}")
    public LeaveType modifyLeaveTypeById(@PathVariable Long id, @Valid @RequestBody LeaveType newLeave) throws Exception {
        return leaveService.modifyLeaveById(id, newLeave);
    }

    /*
     */
}
