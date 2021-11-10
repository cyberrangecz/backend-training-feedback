package cz.muni.ics.kypo.training.feedback.service;

import cz.muni.ics.kypo.training.feedback.dto.provider.CommandPerOptions;
import cz.muni.ics.kypo.training.feedback.enums.MistakeType;
import cz.muni.ics.kypo.training.feedback.exceptions.EntityErrorDetail;
import cz.muni.ics.kypo.training.feedback.exceptions.EntityNotFoundException;
import cz.muni.ics.kypo.training.feedback.model.Command;
import cz.muni.ics.kypo.training.feedback.repository.CommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
@Service
@Transactional
public class CommandService extends CRUDServiceImpl<Command, Long> {

    private final CommandRepository commandRepository;

    public List<Command> getAllCommandsByTrainingRunAndLevel(Long trainingRunId, Long levelId) {

        List<Command> commands = commandRepository.findByLevelIdAndLevelTraineeTrainingRunId(levelId, trainingRunId);
        if (commands.isEmpty()) {
            throw new EntityNotFoundException(new EntityErrorDetail(Command.class, "levelId", levelId.getClass(), levelId));
        }
        return commands;
    }

    public List<Command> getAllCommandsByTrainingRun(Long trainingRunId) {
        List<Command> commands = commandRepository.findByLevelTraineeTrainingRunIdOrderByTrainingTime(trainingRunId);
        if (commands.isEmpty()) {
            throw new EntityNotFoundException(new EntityErrorDetail(Command.class, "trainingRunId", trainingRunId.getClass(), trainingRunId));
        }
        return commands;
    }

    public List<CommandPerOptions> getAggregatedCorrectCommands(List<Long> trainingRunIds) {
        return commandRepository.findCorrectCommandsByTrainingRunIds(trainingRunIds);
    }

    public Map<String, Map<String, List<CommandPerOptions>>> getAggregatedCorrectCommandsByCmdTypeAndCmd(List<Long> trainingRunIds) {
        return commandRepository.findCorrectCommandsByTrainingRunIds(trainingRunIds).stream()
                .collect(groupingBy(CommandPerOptions::getCommandType, groupingBy(CommandPerOptions::getCmd)));
    }


    public List<CommandPerOptions> getAggregatedIncorrectCommands(List<Long> trainingRunIds, List<MistakeType> mistakeTypes) {
        return commandRepository.findIncorrectCommandsByTrainingRunIdsAndMistakeTypes(trainingRunIds, mistakeTypes);
    }

    public Map<String, Map<String, List<CommandPerOptions>>> getAggregatedIncorrectCommandsByCmdTypeAndCmd(List<Long> trainingRunIds, List<MistakeType> mistakeTypes) {
        return commandRepository.findIncorrectCommandsByTrainingRunIdsAndMistakeTypes(trainingRunIds, mistakeTypes).stream()
                .collect(groupingBy(CommandPerOptions::getCommandType, groupingBy(CommandPerOptions::getCmd)));
    }

    @Override
    public JpaRepository<Command, Long> getDAO() {
        return commandRepository;
    }
}
