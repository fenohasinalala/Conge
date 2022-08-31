package com.gestion.conge.model.validation;

import com.gestion.conge.exception.BadRequestException;
import com.gestion.conge.model.Worker;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class WorkerValidator implements Consumer<Worker> {
    private final Validator validator;

    public void accept(List<Worker> workers) {
        workers.forEach(this::accept);
    }

    @Override public void accept(Worker worker) {
        Set<ConstraintViolation<Worker>> violations = validator.validate(worker);
        if (!violations.isEmpty()) {
            String constraintMessages = violations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(". "));
            throw new BadRequestException(constraintMessages);
        }
    }
}
