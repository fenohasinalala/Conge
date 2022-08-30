package com.gestion.conge.controller;

import com.gestion.conge.controller.mapper.WorkerMapper;
import com.gestion.conge.model.Worker;
import com.gestion.conge.service.WorkerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor

public class WorkerController {
    private WorkerService workerService;
    private WorkerMapper workerMapper;

    @GetMapping("/workers")
    public List<Worker> getWorkers(@RequestParam int page,
                                   @RequestParam(value = "page_size") int pageSize,
                                   @RequestParam(value = "first_name",required = false , defaultValue = "") String firstName,
                                   @RequestParam(value = "last_name",required = false , defaultValue = "") String lastName){
        return workerService.getWorkers(page, pageSize, firstName, lastName)
                .stream()
                .map(workerMapper::toRestWorker)
                .toList();
    }

    @PostMapping("/workers")
    public Worker addWorker(@RequestBody Worker Worker){
        return workerService.addWorker(Worker);
    }

    @GetMapping("/workers/{id}")
    public Worker getWorkerById(@PathVariable Long id) throws Exception {
        return workerService.getWorkerById(id);
    }

    @DeleteMapping("/workers/{id}")
    public Worker deleteWorkerById(@PathVariable Long id) throws Exception {
        return workerService.deleteWorkerById(id);
    }

    @PutMapping("/workers/{id}")
    public Worker modifyWorkerById(@PathVariable Long id, @RequestBody Worker newWorker) throws Exception {
        return workerService.putModificationWorkerById(id, newWorker);
    }


}
