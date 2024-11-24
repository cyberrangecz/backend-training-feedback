package cz.cyberrange.platform.training.feedback.mapping;

import cz.cyberrange.platform.training.feedback.dto.provider.TraineeDTO;
import cz.cyberrange.platform.training.feedback.dto.resolver.UserRefDTO;
import cz.cyberrange.platform.training.feedback.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TraineeMapper {

    TraineeDTO mapTraineeToTraineeDTO(Trainee entity);

    List<TraineeDTO> mapToListTraineeDTO(Collection<Trainee> entities);

    @Mapping(source = "trainee.userRefId", target = "userRefId")
    @Mapping(source = "user.userRefSub", target = "sub")
    @Mapping(source = "user.userRefFullName", target = "fullName")
    TraineeDTO mapToTraineeDTO(Trainee trainee, UserRefDTO user);
}