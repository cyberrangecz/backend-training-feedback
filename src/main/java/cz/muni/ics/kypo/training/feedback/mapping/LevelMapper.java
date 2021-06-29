package cz.muni.ics.kypo.training.feedback.mapping;

import cz.muni.ics.kypo.training.feedback.dto.provider.LevelDTO;
import cz.muni.ics.kypo.training.feedback.model.Level;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LevelMapper {

    List<LevelDTO> mapToListLevelDTO(Collection<Level> entities);

    LevelDTO mapToDTO(Level entity);
}
