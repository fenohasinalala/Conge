package com.gestion.conge.model.validation;

import com.gestion.conge.exception.BadRequestException;
import com.gestion.conge.model.Post;
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
public class PostValidator implements Consumer<Post> {
    private final Validator validator;

    public void accept(List<Post> posts) {
        posts.forEach(this::accept);
    }

    @Override public void accept(Post post) {
        Set<ConstraintViolation<Post>> violations = validator.validate(post);
        if (!violations.isEmpty()) {
            String constraintMessages = violations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(". "));
            throw new BadRequestException(constraintMessages);
        }
    }
}
