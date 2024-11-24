package cz.cyberrange.platform.training.feedback.facade;

import cz.cyberrange.platform.training.feedback.dto.provider.AggregatedCommandsDTO;
import cz.cyberrange.platform.training.feedback.dto.provider.CommandDTO;
import cz.cyberrange.platform.training.feedback.dto.provider.CommandPerOptions;
import cz.cyberrange.platform.training.feedback.enums.MistakeType;
import cz.cyberrange.platform.training.feedback.mapping.CommandMapper;
import cz.cyberrange.platform.training.feedback.service.CommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class CommandFacade {

    private final CommandMapper commandMapper;
    private final CommandService commandService;

    public List<AggregatedCommandsDTO> getAggregatedCorrectCommands(List<Long> trainingRunIds) {
        List<AggregatedCommandsDTO> aggregatedCommandsDTOs = new ArrayList<>();
        Map<String, Map<String, List<CommandPerOptions>>> aggregatedCorrectCommands = commandService.getAggregatedCorrectCommandsByCmdTypeAndCmd(trainingRunIds);

        for (var aggregatedByCmdType : aggregatedCorrectCommands.entrySet()) {
            for (var aggregatedByCmd : aggregatedByCmdType.getValue().entrySet()) {
                aggregatedCommandsDTOs.add(getAggregatedCommandsDTO(
                        aggregatedByCmdType.getKey(),
                        aggregatedByCmd.getKey(),
                        aggregatedByCmd.getValue())
                );
            }
        }
        aggregatedCommandsDTOs.sort(Collections.reverseOrder());
        return aggregatedCommandsDTOs;
    }

    public List<AggregatedCommandsDTO> getAggregatedIncorrectCommands(List<Long> trainingRunIds, List<MistakeType> mistakeTypes) {
        List<AggregatedCommandsDTO> aggregatedCommandsDTOs = new ArrayList<>();
        Map<String, Map<String, List<CommandPerOptions>>> aggregatedIncorrectCommands = commandService.getAggregatedIncorrectCommandsByCmdTypeAndCmd(trainingRunIds, mistakeTypes);

        for (var aggregatedByCmdType : aggregatedIncorrectCommands.entrySet())
            for (var aggregatedByCmd : aggregatedByCmdType.getValue().entrySet()) {
                aggregatedCommandsDTOs.add(getAggregatedCommandsDTO(
                        aggregatedByCmdType.getKey(),
                        aggregatedByCmd.getKey(),
                        aggregatedByCmd.getValue())
                );
            }
        aggregatedCommandsDTOs.sort(Collections.reverseOrder());
        return aggregatedCommandsDTOs;
    }

    public List<CommandDTO> getCommandsByTrainingRunAndLevel(Long trainingRunId, Long levelId) {
        return commandMapper.mapToListCommandDTO(commandService.getAllCommandsByTrainingRunAndLevel(trainingRunId, levelId));
    }

    public List<CommandDTO> getCommandsByTrainingRun(Long trainingRunId) {
        return commandMapper.mapToListCommandDTO(commandService.getAllCommandsByTrainingRun(trainingRunId));
    }

    private AggregatedCommandsDTO getAggregatedCommandsDTO(String cmdType, String cmd, List<CommandPerOptions> commands) {
        return AggregatedCommandsDTO.builder()
                .commandType(cmdType)
                .cmd(cmd)
                .aggregatedCommandsPerOptions(commands)
                .frequency(commands.stream()
                        .mapToLong(CommandPerOptions::getFrequency)
                        .sum())
                .build();
    }
}
