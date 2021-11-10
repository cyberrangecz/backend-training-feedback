package cz.muni.ics.kypo.training.feedback.service;

import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionLevel;
import cz.muni.ics.kypo.training.feedback.dto.resolver.TrainingCommand;
import cz.muni.ics.kypo.training.feedback.dto.resolver.TrainingEvent;
import cz.muni.ics.kypo.training.feedback.exceptions.EntityErrorDetail;
import cz.muni.ics.kypo.training.feedback.exceptions.EntityNotFoundException;
import cz.muni.ics.kypo.training.feedback.model.Command;
import cz.muni.ics.kypo.training.feedback.model.Level;
import cz.muni.ics.kypo.training.feedback.model.Trainee;
import cz.muni.ics.kypo.training.feedback.repository.TraineeRepository;
import cz.muni.ics.kypo.training.feedback.service.api.ElasticsearchServiceApi;
import cz.muni.ics.kypo.training.feedback.service.graph.TraineeGraphService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraineeService extends CRUDServiceImpl<Trainee, Long> {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeService.class);

    private static final String LEVEL_STARTED = "cz.muni.csirt.kypo.events.trainings.LevelStarted";
    private static final String LEVEL_COMPLETED = "cz.muni.csirt.kypo.events.trainings.LevelCompleted";

    private final TraineeRepository traineeRepository;
    private final CreateCommandService createCommandService;


    @Override
    public JpaRepository<Trainee, Long> getDAO() {
        return traineeRepository;
    }

    public Trainee getTraineeByTrainingRunId(Long trainingRunId) {
        return traineeRepository.findTraineeByTrainingRunId(trainingRunId)
                .orElseThrow(() -> new EntityNotFoundException(new EntityErrorDetail(Trainee.class, "trainingRunId", trainingRunId.getClass(), trainingRunId)));
    }

    public List<Trainee> getTraineesByTrainingInstanceId(Long instanceId) {
        return traineeRepository.findAllByTraineeGraphTrainingInstanceId(instanceId);
    }

    public Trainee createTraineeEntity(Long trainingRunId,
                                       List<TrainingEvent> traineeEvents,
                                       List<TrainingCommand> traineeCommands) {
        TrainingEvent trainingRunStartedEvent = traineeEvents.get(0);
        Trainee trainee = Trainee.builder()
                .trainingRunId(trainingRunId)
                .userRefId(trainingRunStartedEvent.getUserRefId())
                .sandboxId(trainingRunStartedEvent.getSandboxId())
                .build();
        createTraineeLevels(traineeEvents, traineeCommands, trainee).forEach(l -> trainee.getLevels().add(l));
        return trainee;
    }

    private List<Level> createTraineeLevels(List<TrainingEvent> traineeEvents, List<TrainingCommand> traineeCommands, Trainee trainee) {
        List<Level> levels = new ArrayList<>();
        Map<Long, List<TrainingEvent>> eventsByLevelId = traineeEvents.stream().collect(Collectors.groupingBy(TrainingEvent::getLevel));
        eventsByLevelId.forEach((k, v) -> levels.add(createTraineeLevel(k, v, traineeCommands, trainee)));
        levels.sort(Comparator.comparing(Level::getStartTime));
        return levels;
    }

    private Level createTraineeLevel(Long levelId, List<TrainingEvent> traineeLevelEvents, List<TrainingCommand> traineeCommands, Trainee trainee) {

        LocalDateTime levelStartTime = traineeLevelEvents.stream()
                .filter(l -> l.getType().equals(LEVEL_STARTED))
                .map(TrainingEvent::getTimestamp)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(new EntityErrorDetail(LocalDateTime.class,
                        "Start time of the level (ID: " + levelId  + " has not been found.")));

        Optional<LocalDateTime> levelEndTime = traineeLevelEvents.stream()
                .filter(l -> l.getType().equals(LEVEL_COMPLETED))
                .map(TrainingEvent::getTimestamp)
                .min(LocalDateTime::compareTo);

        List<TrainingCommand> levelCommands = traineeCommands.stream()
                .filter(c -> c.getTimestamp().isAfter(levelStartTime))
                .filter(levelEndTime.isPresent() ? c -> c.getTimestamp().isBefore(levelEndTime.get()) : c -> true)
                .collect(Collectors.toList());


        List<Command> commands = new ArrayList<>();
        Level level = Level.builder()
                .commands(commands)
                .levelRefId(levelId)
                .endTime(levelEndTime.orElse(null))
                .startTime(levelStartTime)
                .trainee(trainee)
                .build();
        if (!levelCommands.isEmpty()) {
            commands.addAll(createCommandService.createCommands(levelCommands, level));
            commands.sort(Comparator.comparing(Command::getTimestamp));
        }
        return level;
    }
}
