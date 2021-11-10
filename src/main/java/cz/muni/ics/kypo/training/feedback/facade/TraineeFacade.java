package cz.muni.ics.kypo.training.feedback.facade;

import cz.muni.ics.kypo.training.feedback.dto.provider.TraineeDTO;
import cz.muni.ics.kypo.training.feedback.dto.resolver.UserRefDTO;
import cz.muni.ics.kypo.training.feedback.mapping.TraineeMapper;
import cz.muni.ics.kypo.training.feedback.model.Trainee;
import cz.muni.ics.kypo.training.feedback.service.TraineeService;
import cz.muni.ics.kypo.training.feedback.service.api.UserManagementServiceApi;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class TraineeFacade {

    private final TraineeMapper traineeMapper;
    private final TraineeService traineeService;
    private final UserManagementServiceApi userManagementServiceApi;

    public List<TraineeDTO> getTraineesByTrainingInstanceId(Long instanceId) {
        Map<Long, Trainee> trainees = traineeService.getTraineesByTrainingInstanceId(instanceId).stream()
                .collect(Collectors.toMap(Trainee::getUserRefId, Function.identity()));
        List<UserRefDTO> users = userManagementServiceApi
                .getUsersByIds(trainees.keySet(), PageRequest.of(0, Integer.MAX_VALUE), null, null)
                .getContent();
        return users.stream()
                .map(user -> this.traineeMapper.mapToTraineeDTO(trainees.get(user.getUserRefId()), user))
                .collect(Collectors.toList());
    }

    public TraineeDTO getTraineeByTrainingRunId(Long trainingRunId) {
        return traineeMapper.mapTraineeToTraineeDTO(traineeService.getTraineeByTrainingRunId(trainingRunId));
    }
}
