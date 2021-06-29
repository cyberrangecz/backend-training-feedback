package cz.muni.ics.kypo.training.feedback.service;

import cz.muni.ics.kypo.training.feedback.dto.resolver.TrainingCommand;
import cz.muni.ics.kypo.training.feedback.dto.resolver.TrainingEvent;
import cz.muni.ics.kypo.training.feedback.exceptions.EntityErrorDetail;
import cz.muni.ics.kypo.training.feedback.exceptions.EntityNotFoundException;
import cz.muni.ics.kypo.training.feedback.model.Command;
import cz.muni.ics.kypo.training.feedback.model.Level;
import cz.muni.ics.kypo.training.feedback.model.Trainee;
import cz.muni.ics.kypo.training.feedback.repository.TraineeRepository;
import cz.muni.ics.kypo.training.feedback.service.api.ElasticsearchServiceApi;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraineeService extends CRUDServiceImpl<Trainee, Long> {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeService.class);

    private static final String TRAINING_STARTED = "cz.muni.csirt.kypo.events.trainings.LevelStarted";
    private static final String TRAINING_COMPLETED = "cz.muni.csirt.kypo.events.trainings.LevelCompleted";

    private final TraineeRepository traineeRepository;
    private final ElasticsearchServiceApi elasticsearchServiceApi;
    private final CreateCommandService createCommandService;
    private final CreateTraineeGraphService createTraineeGraphService;


    @Override
    public JpaRepository<Trainee, Long> getDAO() {
        return traineeRepository;
    }

    public Trainee getTraineeBySandboxId(Long sandboxId) {
        return traineeRepository.findTraineeBySandboxId(sandboxId)
                .orElseThrow(() -> new EntityNotFoundException(new EntityErrorDetail(Trainee.class, "sandboxId", sandboxId.getClass(), sandboxId)));
    }

    public void create(Long definitionId, Long instanceId, Long sandboxId) {
        List<TrainingCommand> commands = elasticsearchServiceApi.getTrainingCommandsBySandboxId(sandboxId);
        List<TrainingEvent> events = elasticsearchServiceApi.getTrainingEventsBySandboxId(definitionId, instanceId, sandboxId);
        Trainee trainee = this.createTraineeEntity(sandboxId, events, commands);
        traineeRepository.save(trainee);
        LOG.info("Trainee with sandbox id: {} created.", trainee.getSandboxId());
    }

    private List<Level> createTraineeLevels(List<TrainingEvent> traineeEvents, List<TrainingCommand> traineeCommands, Trainee trainee) {
        List<Level> levels = new ArrayList<>();
        Map<Long, List<TrainingEvent>> eventsByLevelId = traineeEvents.stream().collect(Collectors.groupingBy(TrainingEvent::getLevel));
        eventsByLevelId.forEach((k, v) -> levels.add(createTraineeLevel(k, v, traineeCommands, trainee)));
        levels.sort(Comparator.comparing(Level::getId));
        return levels;
    }

    private Level createTraineeLevel(Long levelId, List<TrainingEvent> traineeLevelEvents, List<TrainingCommand> traineeCommands, Trainee trainee) {

        LocalDateTime levelStartTime = traineeLevelEvents.stream()
                .filter(l -> l.getType().equals(TRAINING_STARTED))
                .map(TrainingEvent::getTimestamp)
                .min(LocalDateTime::compareTo)
                .get();

        Optional<LocalDateTime> levelEndTime = traineeLevelEvents.stream()
                .filter(l -> l.getType().equals(TRAINING_COMPLETED))
                .map(TrainingEvent::getTimestamp)
                .min(LocalDateTime::compareTo);

        List<TrainingCommand> levelCommands = traineeCommands.stream()
                .filter(c -> c.getTimestamp().isAfter(levelStartTime))
                .filter(levelEndTime.isPresent() ? c -> c.getTimestamp().isBefore(levelEndTime.get()) : c -> true)
                .collect(Collectors.toList());


            List<Command> commands = new ArrayList<>();
            Level level = Level.builder()
                    .commands(commands)
                    .id(levelId)
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

    private Trainee createTraineeEntity(Long sandboxId, List<TrainingEvent> traineeEvents, List<TrainingCommand> traineeCommands) {
        Trainee trainee = Trainee.builder()
                .sandboxId(sandboxId)
                .build();
        createTraineeLevels(traineeEvents, traineeCommands, trainee).forEach(l -> trainee.getLevels().add(l));
        trainee.setTraineeGraph(createTraineeGraphService.createTraineeGraph(trainee, sandboxId));
        return trainee;
    }
}
