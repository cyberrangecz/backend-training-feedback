package cz.muni.ics.kypo.training.feedback.service;

import cz.muni.ics.kypo.training.feedback.dto.resolver.TrainingCommand;
import cz.muni.ics.kypo.training.feedback.model.Command;
import cz.muni.ics.kypo.training.feedback.model.Level;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateCommandService {

    private final MistakeAnalysisService mistakeAnalysisService;

    public List<Command> createCommands(List<TrainingCommand> trainingCommands, Level level) {
        List<Command> commands = new ArrayList<>();
        for (TrainingCommand trainingCommand : trainingCommands) {
            commands.add(this.createCommand(trainingCommand, level));
        }
        return commands;
    }

    private Command createCommand(TrainingCommand trainingCommand, Level level) {
        String options = trainingCommand.getCommandArguments().replace("\"", "\\\"");
        Command command = Command.builder()
                .cmd(trainingCommand.getCommand().replace("\"", "\\\""))
                .commandType(trainingCommand.getCmdType())
                .fromHostIp(trainingCommand.getIp())
                .level(level)
                .options(options.length() > 255 ? options.substring(0,254) : options)
                .timestamp(trainingCommand.getTimestamp())
                .build();
        return mistakeAnalysisService.analyzeCommand(command);
    }

}
