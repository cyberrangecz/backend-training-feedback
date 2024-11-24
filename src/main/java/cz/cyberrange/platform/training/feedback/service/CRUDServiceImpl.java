package cz.cyberrange.platform.training.feedback.service;

import cz.cyberrange.platform.training.feedback.exceptions.ResourceNotFoundException;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Transactional
public abstract class CRUDServiceImpl<E, I extends Serializable> implements CRUDService<E, I> {

    @Override
    public E create(@NonNull E entity) {
        return getDAO().save(entity);
    }

    @Override
    public List<E> getAll() {
        List<E> entities = getDAO().findAll();
        if (entities.isEmpty()) {
            throw new ResourceNotFoundException("There does not exist any expected object");
        }
        return entities;
    }

    @Override
    public Optional<E> find(@NonNull I id) {
        return getDAO().findById(id);
    }

    @Override
    public void delete(@NonNull I id) {
        getDAO().deleteById(id);
    }

    @Override
    public E update(@NonNull E entity) {
        return getDAO().save(entity);
    }

    public abstract JpaRepository<E, I> getDAO();
}