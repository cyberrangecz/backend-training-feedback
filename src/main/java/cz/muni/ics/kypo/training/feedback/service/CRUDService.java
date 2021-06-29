package cz.muni.ics.kypo.training.feedback.service;

import java.util.List;
import java.util.Optional;

public interface CRUDService<E, IDENTIFIER_TYPE> {

    E create(E entity);

    List<E> getAll();

    Optional<E> find(IDENTIFIER_TYPE id);

    E update(E entity);

    void delete(IDENTIFIER_TYPE id);
}