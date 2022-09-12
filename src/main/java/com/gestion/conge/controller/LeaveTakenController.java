package com.gestion.conge.controller;

import com.gestion.conge.model.LeaveSummary;
import com.gestion.conge.model.LeaveTaken;
import com.gestion.conge.service.LeaveTakenService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Month;
import java.time.Year;
import java.util.List;

@RestController
@AllArgsConstructor

public class LeaveTakenController {
    private LeaveTakenService leaveTakenService;

    @GetMapping("/leaves")
    public List<LeaveTaken> getLeaves(@RequestParam int page,
                                      @RequestParam(value = "page_size") int pageSize){
        return leaveTakenService.getLeaves(page, pageSize);
    }

    @PostMapping("/leaves")
    public LeaveTaken addLeave(@RequestBody LeaveTaken leaveTaken){
        return leaveTakenService.addLeave(leaveTaken);
    }

    @GetMapping("/leaves/{id}")
    public LeaveTaken getLeaveTakenById(@PathVariable Long id) throws Exception {
        return leaveTakenService.getLeaveTakenById(id);
    }

    @DeleteMapping("/leaves/{id}")
    public LeaveTaken deleteLeaveById(@PathVariable Long id) throws Exception {
        return leaveTakenService.deleteLeaveById(id);
    }

    @PutMapping("/leaves/{id}")
    public LeaveTaken modifyLeaveById(@PathVariable Long id, @RequestBody LeaveTaken leaveTaken) throws Exception {
        return leaveTakenService.putModificationLeaveById(id, leaveTaken);
    }

    @GetMapping("/workers/{id}/leaves")
    public List<LeaveTaken> getLeaveTakenByWorkerId(@PathVariable Long id) throws Exception {
        return leaveTakenService.getLeaveTakenByWorkerId(id);
    }

    @GetMapping("/workers/{id}/leaves-summary")
    public LeaveSummary getLeaveSummaryByWorkerId(@PathVariable Long id,
                                                  @RequestParam(value = "year_ref",required = false , defaultValue = "") Year yearRef,
                                                  @RequestParam(value = "month_ref",required = false , defaultValue = "") Month monthRef) throws Exception {
        return leaveTakenService.getLeaveSummaryByWorkerId(id,yearRef,monthRef);
    }
}
