package com.gestion.conge.controller.mapper;

import com.gestion.conge.model.Worker;
import org.springframework.stereotype.Component;

@Component
public class WorkerMapper {
    public  Worker toRestWorker(Worker worker){
        Worker restWorker = new Worker();
        restWorker.setAddress(worker.getAddress());
        restWorker.setBirthDate(worker.getBirthDate());
        restWorker.setPhone(worker.getPhone());
        restWorker.setCin(worker.getCin());
        restWorker.setEmail(worker.getEmail());
        restWorker.setSex(worker.getSex());
        restWorker.setLastName(worker.getLastName());
        restWorker.setFirstName(worker.getFirstName());
        restWorker.setEntranceDatetime(worker.getEntranceDatetime());
        restWorker.setId(worker.getId());
        restWorker.setLeaveGet(worker.getleaveGet());
        restWorker.setLeaveTaken(worker.getLeaveTaken());
        restWorker.setLeaveRemained(worker.getLeaveRemained());
        restWorker.setPost(worker.getPost());
        return restWorker;
    }
}
