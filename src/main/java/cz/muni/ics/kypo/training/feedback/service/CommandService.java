package cz.muni.ics.kypo.training.feedback.service;

import cz.muni.ics.kypo.training.feedback.dto.provider.AggregatedWrongCommandsDTO;
import cz.muni.ics.kypo.training.feedback.enums.MistakeType;
import cz.muni.ics.kypo.training.feedback.exceptions.EntityErrorDetail;
import cz.muni.ics.kypo.training.feedback.exceptions.EntityNotFoundException;
import cz.muni.ics.kypo.training.feedback.mapping.CommandMapper;
import cz.muni.ics.kypo.training.feedback.model.Command;
import cz.muni.ics.kypo.training.feedback.repository.CommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
@Service
@Transactional
public class CommandService extends CRUDServiceImpl<Command, Long> {

    private final CommandRepository commandRepository;
    private final CommandMapper commandMapper;

    public List<Command> getSuccessCommands() {
        List<Command> commands = commandRepository.findByMistakeIsNull();
        if (commands.isEmpty()) {
            throw new EntityNotFoundException(new EntityErrorDetail(Command.class, "Does not exist any command without mistake."));
        }
        return commands;
    }

    public List<Command> getAllCommands(Long sandboxId, Long levelId) {

        List<Command> commands = commandRepository.findByLevelIdAndLevelTraineeSandboxId(levelId, sandboxId);
        if (commands.isEmpty()) {
            throw new EntityNotFoundException(new EntityErrorDetail(Command.class, "levelId", levelId.getClass(), levelId));
        }
        return commands;
    }

    public List<Command> getAllCommands(Long sandboxId) {
        List<Command> commands = commandRepository.findByLevelTraineeSandboxId(sandboxId);
        if (commands.isEmpty()) {
            throw new EntityNotFoundException(new EntityErrorDetail(Command.class, "sandboxId", sandboxId.getClass(), sandboxId));
        }
        return commands;
    }

    public List<Command> getWrongCommands(Long sandboxId, MistakeType mistakeType) {
        List<Command> commands = commandRepository.findByLevelTraineeSandboxIdAndMistakeMistakeType(sandboxId, mistakeType);
        if (commands.isEmpty()) {
            throw new EntityNotFoundException(new EntityErrorDetail(Command.class, "mistakeType", mistakeType.getClass(), mistakeType));
        }
        return commands;
    }

    public List<Command> getWrongCommands(Long sandboxId) {
        List<Command> commands = commandRepository.findByLevelTraineeSandboxIdAndMistakeIsNotNull(sandboxId);
        if (commands.isEmpty()) {
            throw new EntityNotFoundException(new EntityErrorDetail(Command.class, "sandboxId", sandboxId.getClass(), sandboxId));
        }
        return commands;
    }

    public List<Command> getAllWrongCommands() {
        List<Command> commands = commandRepository.findByMistakeIsNotNull();
        if (commands.isEmpty()) {
            throw new EntityNotFoundException(new EntityErrorDetail(Command.class, "Does not exist any command with some mistake."));
        }
        return commands;
    }

    public List<AggregatedWrongCommandsDTO> getAggregatedWrongCommands(List<Long> sandboxIds, List<MistakeType> mistakeTypes) {
        List<Command> commands = new ArrayList<>();
        for (Long sandboxId : sandboxIds) {
            commands.addAll(commandRepository.findByLevelTraineeSandboxIdAndMistakeIsNotNull(sandboxId).stream()
                    .filter(c -> mistakeTypes.contains(c.getMistake().getMistakeType()))
                    .collect(Collectors.toList()));
        }
        List<AggregatedWrongCommandsDTO> aggregatedWrongCommandsDTOS = new ArrayList<>();
        Map<String, Map<String, List<Command>>> groupedCommands = commands.stream().collect(groupingBy(Command::getCommandType, groupingBy(Command::getCmd)));
        for (String commandType : groupedCommands.keySet())
            for (String cmd : groupedCommands.get(commandType).keySet()) {
                List<Command> commandList = groupedCommands.get(commandType).get(cmd);
                aggregatedWrongCommandsDTOS.add(AggregatedWrongCommandsDTO.builder()
                        .cmd(cmd)
                        .commandType(commandType)
                        .wrongCommandDTOS(commandMapper.map(commandList))
                        .frequency((long) commandList.size())
                        .build());
            }

        if (aggregatedWrongCommandsDTOS.isEmpty()) {
            throw new EntityNotFoundException(new EntityErrorDetail(Command.class, "mistakeType", mistakeTypes.getClass(), mistakeTypes));
        }
        Collections.sort(aggregatedWrongCommandsDTOS, Collections.reverseOrder());
        return aggregatedWrongCommandsDTOS;
    }

    public List<Command> getWrongCommands(List<Long> sandboxIds) {
        List<Command> commands = new ArrayList<>();
        for (Long sandboxId : sandboxIds) {
            commands.addAll(commandRepository.findByLevelTraineeSandboxIdAndMistakeIsNotNull(sandboxId));
        }
        if (commands.isEmpty()) {
            throw new EntityNotFoundException(new EntityErrorDetail(Command.class, "sandboxIds", sandboxIds.getClass(), sandboxIds));
        }
        return commands;

    }

    @Override
    public JpaRepository<Command, Long> getDAO() {
        return commandRepository;
    }
}
