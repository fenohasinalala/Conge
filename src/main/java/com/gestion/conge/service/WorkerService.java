package com.gestion.conge.service;

import com.gestion.conge.exception.BadRequestException;
import com.gestion.conge.exception.ResourceNotFoundException;
import com.gestion.conge.model.Worker;
import com.gestion.conge.model.validation.WorkerValidator;
import com.gestion.conge.repository.WorkerRepository;
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
public class WorkerService {

    private WorkerRepository workerRepository;

    WorkerValidator workerValidator;
    private PostService postService;

    public List<Worker> getWorkers(int page, int pageSize, String firstName, String lastName) {
        if(page<1){
            throw new BadRequestException("page must be >=1");
        }
        if(pageSize>200){
            throw new BadRequestException("page size too large, must be <=200");
        }
        Pageable pageable = PageRequest.of(page - 1,pageSize,
                Sort.by(ASC,"firstName"));
        return workerRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName, pageable);

    }

    public Worker addWorker(Worker worker) {
        workerValidator.accept(worker);
        Optional<Worker> workerByEmail = workerRepository.findWorkerByEmail(worker.getEmail());
        Optional<Worker> workerByPhone = workerRepository.findWorkerByPhone(worker.getPhone());
        Optional<Worker> workerByCin = workerRepository.findWorkerByCin(worker.getCin());
        if (workerByEmail.isPresent()){
            throw new BadRequestException("email already taken");
        }
        if (workerByPhone.isPresent()){
            throw new BadRequestException("phone already taken");
        }
        if (workerByCin.isPresent()){
            throw new BadRequestException("Cin already taken");
        }
        workerRepository.save(worker);
        return worker;
    }

    public Worker getWorkerById(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Worker with id "+id+" does not exists"));
        return worker;
    }

    public Worker deleteWorkerById(Long id) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Worker with id "+id+" does not exists"));
        workerRepository.delete(worker);
        return worker;
    }

    @Transactional
    public Worker putModificationWorkerById(Long id, Worker newWorker) {
        workerValidator.accept(newWorker);
        Worker worker = workerRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Worker with id "+id+" does not exists"));
        // Test sans !=null et .length>0
        if(!Objects.equals(newWorker.getFirstName(),worker.getFirstName())){
            worker.setFirstName(newWorker.getFirstName());
        }
        if(!Objects.equals(newWorker.getLastName(),worker.getLastName())){
            worker.setLastName(newWorker.getLastName());
        }
        if(!Objects.equals(newWorker.getPhone(),worker.getPhone())){
            worker.setPhone(newWorker.getPhone());
        }
        if(!Objects.equals(newWorker.getEntranceDatetime(),worker.getEntranceDatetime())){
            worker.setEntranceDatetime(newWorker.getEntranceDatetime());
        }
        if(!Objects.equals(newWorker.getEmail(),worker.getEmail())){
            worker.setEmail(newWorker.getEmail());
        }
        if(!Objects.equals(newWorker.getCin(),worker.getCin())){
            worker.setCin(newWorker.getCin());
        }
        if(!Objects.equals(newWorker.getBirthDate(),worker.getBirthDate())){
            worker.setBirthDate(newWorker.getBirthDate());
        }
        if(!Objects.equals(newWorker.getBirthDate(),worker.getBirthDate())){
            worker.setBirthDate(newWorker.getBirthDate());
        }
        if(!Objects.equals(newWorker.getSex(),worker.getSex())){
            worker.setSex(newWorker.getSex());
        }
        if(!Objects.equals(newWorker.getAddress(),worker.getAddress())){
            worker.setAddress(newWorker.getAddress());
        }
        if(!Objects.equals(newWorker.getPost().getId(),worker.getPost().getId())){
            worker.setPost(postService.getPostById(newWorker.getPost().getId()));
        }
        return worker;
    }
}
