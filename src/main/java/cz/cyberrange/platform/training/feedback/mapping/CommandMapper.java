package cz.cyberrange.platform.training.feedback.mapping;

import cz.cyberrange.platform.training.feedback.dto.provider.CommandDTO;
import cz.cyberrange.platform.training.feedback.model.Command;
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
