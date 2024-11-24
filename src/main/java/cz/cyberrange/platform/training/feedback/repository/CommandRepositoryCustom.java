package cz.cyberrange.platform.training.feedback.repository;

import cz.cyberrange.platform.training.feedback.dto.provider.CommandPerOptions;
import cz.cyberrange.platform.training.feedback.enums.MistakeType;

import java.util.List;

/**
 * The interface Training definition repository custom.
 */
public interface CommandRepositoryCustom {

    List<CommandPerOptions> findIncorrectCommandsByTrainingRunIdsAndMistakeTypes(List<Long> trainingRunIds, List<MistakeType> mistakeTypes);

    List<CommandPerOptions> findCorrectCommandsByTrainingRunIds(List<Long> trainingRunIds);

}
