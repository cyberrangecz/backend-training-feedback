package cz.muni.ics.kypo.training.feedback.facade;

import cz.muni.ics.kypo.training.feedback.dto.provider.AggregatedCommandDTO;
import cz.muni.ics.kypo.training.feedback.dto.provider.AggregatedWrongCommandsDTO;
import cz.muni.ics.kypo.training.feedback.dto.provider.CommandDTO;
import cz.muni.ics.kypo.training.feedback.dto.provider.WrongCommandDTO;
import cz.muni.ics.kypo.training.feedback.enums.MistakeType;
import cz.muni.ics.kypo.training.feedback.model.Command;
import cz.muni.ics.kypo.training.feedback.mapping.CommandMapper;
import cz.muni.ics.kypo.training.feedback.service.CommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
@Service
public class CommandFacade {

    private final CommandMapper commandMapper;
    private final CommandService commandService;

    public List<AggregatedCommandDTO> getCommands() {
        List<AggregatedCommandDTO> aggregatedCommands = new ArrayList<>();
        Map<String, Map<String, Map<String, Long>>> groupedByCommandTypeMap = commandService.getSuccessCommands().stream()
                .collect(groupingBy(Command::getCommandType, groupingBy(Command::getCmd, groupingBy(Command::getOptions,  counting()))));

        for (String cmdType: groupedByCommandTypeMap.keySet()) {
            Map<String, Map<String, Long>> groupedByCommandMap = groupedByCommandTypeMap.get(cmdType);

            for (String cmd: groupedByCommandMap.keySet()) {
                Map<String, Long>  groupedByCommandOptions = groupedByCommandMap.get(cmd);
                aggregatedCommands.add(AggregatedCommandDTO.builder()
                        .commandType(cmdType)
                        .cmd(cmd)
                        .frequency(groupedByCommandOptions.values().stream().reduce(0L, Long::sum))
                        .granularityPerOption(groupedByCommandOptions)
                        .build());
                }
            }
        Collections.sort(aggregatedCommands, Collections.reverseOrder());
        return aggregatedCommands;
    }

    public List<CommandDTO> getCommands(Long sandboxId, Long levelId) {
        return commandMapper.mapToListCommandDTO(commandService.getAllCommands(sandboxId, levelId));
    }

    public List<CommandDTO> getCommands(Long sandboxId) {
        return commandMapper.mapToListCommandDTO(commandService.getAllCommands(sandboxId));
    }

    public List<WrongCommandDTO> getWrongCommands(Long sandboxId, MistakeType mistakeType) {
        return commandMapper.map(commandService.getWrongCommands(sandboxId, mistakeType));
    }

    public List<WrongCommandDTO> getWrongCommands(Long sandboxId) {
        return commandMapper.map(commandService.getWrongCommands(sandboxId));
    }

    public List<WrongCommandDTO> getWrongCommands() {
        return commandMapper.map(commandService.getAllWrongCommands());
    }

    public List<AggregatedWrongCommandsDTO> getAggregatedWrongCommands(List<Long> sandboxIds, List<MistakeType> mistakeTypes) {
        return commandService.getAggregatedWrongCommands(sandboxIds, mistakeTypes);
    }

    public List<WrongCommandDTO> getWrongCommands(List<Long> sandboxIds) {
        return commandMapper.map(commandService.getWrongCommands(sandboxIds));
    }
}
