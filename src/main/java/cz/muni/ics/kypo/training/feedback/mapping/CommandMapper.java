package cz.muni.ics.kypo.training.feedback.mapping;

import cz.muni.ics.kypo.training.feedback.dto.provider.CommandDTO;
import cz.muni.ics.kypo.training.feedback.model.Command;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommandMapper {

    @IterableMapping(elementTargetType = CommandDTO.class)
    List<CommandDTO> mapToListCommandDTO(Collection<Command> entities);

}
