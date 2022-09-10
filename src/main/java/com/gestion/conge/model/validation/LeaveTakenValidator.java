package com.gestion.conge.model.validation;

import com.gestion.conge.exception.BadRequestException;
import com.gestion.conge.model.LeaveTaken;
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
public class LeaveTakenValidator implements Consumer<LeaveTaken> {
    private final Validator validator;

    public void accept(List<LeaveTaken> leaveTakens) {
        leaveTakens.forEach(this::accept);
    }

    @Override public void accept(LeaveTaken leaveTaken) {
        Set<ConstraintViolation<LeaveTaken>> violations = validator.validate(leaveTaken);
        if (!violations.isEmpty()) {
            String constraintMessages = violations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(". "));
            throw new BadRequestException(constraintMessages);
        }
    }
}
