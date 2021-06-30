package cz.muni.ics.kypo.training.feedback.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.ics.kypo.training.feedback.dto.resolver.mistakes.*;
import cz.muni.ics.kypo.training.feedback.enums.MistakeType;
import cz.muni.ics.kypo.training.feedback.exceptions.InternalServerErrorException;
import cz.muni.ics.kypo.training.feedback.model.Command;
import cz.muni.ics.kypo.training.feedback.model.Mistake;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MistakeAnalysisService {

    public static final long ANY_ORDER = -1L;
    public static final long INFINITE = -2L;
    public static final String SYNTAX_FILES_PATH = "classpath:syntax/*.json";
    public static final String SEMANTIC_FILE_WITH_PATH = "semantic/kobylka.json";
    public static final String VALID_IP_ADDRESS_REGEX = "((?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}(?:[01]?\\d\\d?|2[0-4]\\d|25[0-5])";
    private static final Logger LOG = LoggerFactory.getLogger(MistakeAnalysisService.class);
    private final ResourcePatternResolver resourceResolver;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, CommandSyntax> syntaxes = new HashMap<>();
    private TrainingSemantic trainingSemantic;

    public Command analyzeCommand(Command command) {
        if (!syntaxes.containsKey(command.getCmd())) {
            return invalidCommand(command, MistakeType.SYNTAX_UNKNOWN_COMMAND);
        }
        CommandSyntax commandSyntax = syntaxes.get(command.getCmd());
        if (!commandSyntax.getCmdType().equals(command.getCommandType())) {
            return invalidCommand(command, MistakeType.SYNTAX_UNKNOWN_COMMAND);
        }

        List<SyntaxArgumentSet> argumentSets = setOfArgumentsSorted(commandSyntax);
        boolean noneArgumentSets = argumentSets.isEmpty();
        long argCount = 0L;
        String placement = "before";
        Stack<String> stackOptionsAndArguments = new Stack<>();

        stackOptionsAndArguments.addAll(Arrays.asList(command.getOptions().split(" ")).stream().sorted(Collections.reverseOrder()).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
        while (!stackOptionsAndArguments.empty()) {
            String optOrArg = stackOptionsAndArguments.pop();

            if (isOption(optOrArg)) {
                placement = "after";
                stackOptionsAndArguments.push(optOrArg);
                Optional<Command> invalidCommand = processOption(command, commandSyntax.getOptions(), stackOptionsAndArguments);
                if (invalidCommand.isPresent()) {
                    return invalidCommand.get();
                }
                // is argument
            } else {
                argumentSets = validArgumentSets(optOrArg, argumentSets, argCount++, placement);
            }
        }
        if (!noneArgumentSets) {
            Optional<Command> invalidCommand = validateArgumentSets(argumentSets, command);
            if (invalidCommand.isPresent()) {
                return invalidCommand.get();
            }
        }
        return semanticCheck(command).orElse(command);
    }

    @PostConstruct
    private void init() {
        try {
            Resource resource = new ClassPathResource(SEMANTIC_FILE_WITH_PATH);
            trainingSemantic = objectMapper.readValue(resource.getInputStream(), TrainingSemantic.class);
        } catch (IOException e) {
            throw new InternalServerErrorException("Cannot load training semantic definition file.");
        }
        try {
            Resource[] resources = resourceResolver.getResources(SYNTAX_FILES_PATH);
            if (resources == null || resources.length == 0) {
                throw new InternalServerErrorException("Cannot load syntax definition files.");
            }
            for (Resource resource : resources) {
                try {

                    String[] cmdNameAndType = resource.getFilename().substring(0, resource.getFilename().length() - 5).split("_");
                    String cmdName = cmdNameAndType[0];
                    String cmdType = cmdNameAndType[1];
                    CommandSyntax commandSyntax = objectMapper.readValue(resource.getInputStream(), CommandSyntax.class);
                    commandSyntax.setCmdType(cmdType);
                    syntaxes.put(cmdName, commandSyntax);
                    LOG.debug(String.format("Command %s loaded successfully.", cmdName));
                } catch (IOException e) {
                    e.printStackTrace();
                    LOG.error(String.format("Cannot properly load and parse command from file: %s", resource.getFilename()));
                }
            }

        } catch (IOException e) {
            throw new InternalServerErrorException("Cannot load syntax definition files.");
        }
    }

    private Optional<Command> semanticCheck(Command command) {
        Pattern pattern = Pattern.compile(VALID_IP_ADDRESS_REGEX);
        Matcher matcher = pattern.matcher(command.getOptions());
        if (matcher.find()) {
            if (!trainingSemantic.getAddresses().contains(matcher.group())) {
                return Optional.of(invalidCommand(command, MistakeType.SEMANTIC_UNKNOWN_IP));
            }
            for (CommandIp commandIp : trainingSemantic.getCommandIps()) {
                if (commandIp.getCmd().equals(command.getCmd()) && !commandIp.getIp().equals(matcher.group(1))) {
                    return Optional.of(invalidCommand(command, MistakeType.SEMANTIC_WRONG_IP));
                }
            }
        }
        return Optional.empty();
    }

    private Optional<Command> validateArgumentSets(List<SyntaxArgumentSet> argumentSets, Command command) {
        if (argumentSets.isEmpty()) {
            return Optional.of(invalidCommand(command, MistakeType.SYNTAX_INVALID_ARGUMENTS));
        } else {
            boolean foundValidSet = false;
            for (SyntaxArgumentSet argumentSet : argumentSets) {
                List<SyntaxArgument> arguments = argumentSet.getValues();
                if (arguments.isEmpty() || (arguments.size() == 1 && arguments.get(0).getOrder().equals(-2L))) {
                    foundValidSet = true;
                    break;
                }
            }
            if (!foundValidSet) {
                return Optional.of(invalidCommand(command, MistakeType.SYNTAX_INVALID_ARGUMENTS));
            }
            return Optional.empty();
        }
    }

    private Optional<Command> processOption(Command command, List<SyntaxCommandOption> syntaxOptions, Stack<String> stack) {
        String option = stack.pop();
        if (option.startsWith("--")) {
            if (option.contains("=")) {
                String[] optionAndParam = option.split("=", 2);
                stack.push(optionAndParam[1]);
                return processLongOption(command, syntaxOptions, stack, optionAndParam[0]);
            }
            return processLongOption(command, syntaxOptions, stack, option);
        } else {
            return processShortOption(command, syntaxOptions, stack, Arrays.asList(option.substring(1).split("")));
        }
    }

    private Optional<Command> processLongOption(Command command, List<SyntaxCommandOption> syntaxOptions, Stack<String> stack, String option) {
        List<SyntaxCommandOption> options = syntaxOptions.stream().filter(o -> o.getLongName().contains(option)).collect(Collectors.toList());
        if (options.isEmpty()) {
            return Optional.of(invalidCommand(command, MistakeType.SYNTAX_INVALID_OPTION));
        } else if (options.size() > 1) {
            LOG.error(MessageFormat.format("Invalid syntax file for command {0}", command.getCmd()));
            return Optional.empty();
        } else {
            for (SyntaxOptionParameter parameter : options.get(0).getParameters()) {
                if (parameter.isOptional()) {
                    if (!stack.empty()) {
                        String executedParameter = stack.pop();
                        if (!executedParameter.matches(parameter.getValue())) {
                            stack.push(executedParameter);
                        }
                    }
                } else {
                    if (stack.empty()) {
                        return Optional.of(invalidCommand(command, MistakeType.SYNTAX_MISSING_OPTION_PARAM));
                    }
                    String executedParameter = stack.pop();
                    if (!executedParameter.matches(parameter.getValue())) {
                        return Optional.of(invalidCommand(command, MistakeType.SYNTAX_INVALID_OPTION_PARAM));
                    }
                }


            }

        }
        return Optional.empty();
    }

    private Optional<Command> processShortOption(Command command, List<SyntaxCommandOption> syntaxOptions, Stack<String> stack, List<String> options) {
        for (String option : options) {
            Optional<SyntaxCommandOption> syntaxOption = findRightSyntaxShortOption(syntaxOptions, option);
            if (!syntaxOption.isPresent()) {
                return Optional.of(invalidCommand(command, MistakeType.SYNTAX_INVALID_OPTION));
            }
            for (SyntaxOptionParameter parameter : syntaxOption.get().getParameters()) {
                if (parameter.isOptional()) {
                    if (!stack.empty()) {
                        String executedParameter = stack.pop();
                        if (!executedParameter.matches(parameter.getValue())) {
                            stack.push(executedParameter);
                        }
                    }
                } else {
                    if (stack.empty()) {
                        return Optional.of(invalidCommand(command, MistakeType.SYNTAX_MISSING_OPTION_PARAM));
                    }
                    String executedParameter = stack.pop();
                    if (!executedParameter.matches(parameter.getValue())) {
                        return Optional.of(invalidCommand(command, MistakeType.SYNTAX_INVALID_OPTION_PARAM));
                    }
                }


            }
        }
        return Optional.empty();
    }

    private Optional<SyntaxCommandOption> findRightSyntaxShortOption(List<SyntaxCommandOption> syntaxOptions, String option) {
        for (SyntaxCommandOption syntaxCommandOption : syntaxOptions) {
            if (syntaxCommandOption.isCaseSensitive()) {
                if (syntaxCommandOption.getShortNames().contains(option)) {
                    return Optional.of(syntaxCommandOption);
                }
            } else {
                List<String> lowerCaseSyntaxOptions = syntaxCommandOption.getShortNames().stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
                if (lowerCaseSyntaxOptions.contains(option.toLowerCase())) {
                    return Optional.of(syntaxCommandOption);
                }
            }
        }
        return Optional.empty();
    }

    private List<SyntaxArgumentSet> setOfArgumentsSorted(CommandSyntax commandSyntax) {
        List<SyntaxArgumentSet> copyOfArguments = new ArrayList<>();
        for (SyntaxArgumentSet syntaxArgumentSet : commandSyntax.getArguments()) {
            List<SyntaxArgument> syntaxArguments = new ArrayList<>();
            for (SyntaxArgument syntaxArgument : syntaxArgumentSet.getValues()) {
                syntaxArguments.add(new SyntaxArgument(syntaxArgument.getOrder(), syntaxArgument.getValue(), syntaxArgument.getPlacement()));
            }
            copyOfArguments.add(new SyntaxArgumentSet(syntaxArgumentSet.getArgumentSetId(), syntaxArguments));
        }
        Comparator<SyntaxArgument> comparator = Comparator.comparing(SyntaxArgument::getOrder);
        copyOfArguments.forEach(arg -> arg.getValues().sort(comparator.reversed()));
        return copyOfArguments;
    }

    private List<SyntaxArgumentSet> validArgumentSets(String argument, List<SyntaxArgumentSet> argumentSets, Long order, String placement) {
        List<SyntaxArgumentSet> newSet = new ArrayList<>();
        for (SyntaxArgumentSet set : argumentSets) {
            for (SyntaxArgument syntaxArgument : set.getValues()) {
                if (syntaxArgument.getPlacement().equals(placement)) {
                    try {
                        boolean argumentMatches = argument.matches(syntaxArgument.getValue());
                        if (argumentMatches) {
                            if (syntaxArgument.getOrder().equals(order) || syntaxArgument.getOrder().equals(ANY_ORDER)) {
                                set.getValues().remove(syntaxArgument);
                                newSet.add(set);
                                break;
                            }
                            if (syntaxArgument.getOrder().equals(INFINITE)) {
                                newSet.add(set);
                                break;
                            }
                        }
                    } catch (PatternSyntaxException e) {
                        LOG.error("Wrong syntax of pattern: " + syntaxArgument.getValue());
                        return argumentSets;
                    }
                }

            }
        }
        return newSet;
    }

    private Command invalidCommand(Command command, MistakeType mistakeType) {
        Mistake unknownCommandMistake = Mistake.builder().mistakeType(mistakeType).build();
        unknownCommandMistake.getCommands().add(command);
        command.setMistake(unknownCommandMistake);
        return command;
    }

    private boolean isOption(String optOrArg) {
        return optOrArg.startsWith("-");
    }
}
