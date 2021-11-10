package cz.muni.ics.kypo.training.feedback.repository;

import cz.muni.ics.kypo.training.feedback.dto.provider.CommandPerOptions;
import cz.muni.ics.kypo.training.feedback.enums.MistakeType;

import java.util.List;

/**
 * The interface Training definition repository custom.
 */
public interface CommandRepositoryCustom {

    List<CommandPerOptions> findIncorrectCommandsByTrainingRunIdsAndMistakeTypes(List<Long> trainingRunIds, List<MistakeType> mistakeTypes);

    List<CommandPerOptions> findCorrectCommandsByTrainingRunIds(List<Long> trainingRunIds);

}
