package cz.cyberrange.platform.training.feedback.facade;

import cz.cyberrange.platform.training.feedback.dto.provider.TraineeDTO;
import cz.cyberrange.platform.training.feedback.dto.resolver.UserRefDTO;
import cz.cyberrange.platform.training.feedback.mapping.TraineeMapper;
import cz.cyberrange.platform.training.feedback.model.Trainee;
import cz.cyberrange.platform.training.feedback.service.TraineeService;
import cz.cyberrange.platform.training.feedback.service.api.UserManagementServiceApi;
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
