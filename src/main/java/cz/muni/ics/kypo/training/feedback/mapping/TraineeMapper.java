package cz.muni.ics.kypo.training.feedback.mapping;

import cz.muni.ics.kypo.training.feedback.dto.provider.TraineeDTO;
import cz.muni.ics.kypo.training.feedback.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TraineeMapper {

    TraineeDTO mapTraineeToTraineeDTO(Trainee entity);

    List<TraineeDTO> mapToListTraineeDTO(Collection<Trainee> entities);


}