package cz.muni.ics.kypo.training.feedback.mapping;

import cz.muni.ics.kypo.training.feedback.dto.provider.CommandDTO;
import cz.muni.ics.kypo.training.feedback.dto.provider.WrongCommandDTO;
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

    @IterableMapping(elementTargetType = WrongCommandDTO.class)
    List<WrongCommandDTO> map(Collection<Command> entities);

    default WrongCommandDTO map(Command command) {
        return WrongCommandDTO
                .builder()
                .fromHostIp(command.getFromHostIp())
                .mistake(command.getMistake().getMistakeType().toString())
                .options(command.getOptions())
                .build();
    }
}
