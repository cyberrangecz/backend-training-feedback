package cz.muni.ics.kypo.training.feedback.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import cz.muni.ics.kypo.training.feedback.dto.provider.CommandPerOptions;
import cz.muni.ics.kypo.training.feedback.enums.MistakeType;
import cz.muni.ics.kypo.training.feedback.model.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;

/**
 * The type Training definition repository.
 */
@Repository
public class CommandRepositoryCustomImpl extends QuerydslRepositorySupport implements CommandRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Instantiates a new Training definition repository.
     */
    public CommandRepositoryCustomImpl() {
        super(Command.class);
    }

    @Override
    @Transactional
    public List<CommandPerOptions> findIncorrectCommandsByTrainingRunIdsAndMistakeTypes(List<Long> trainingRunIds, List<MistakeType> mistakeTypes) {
        Objects.requireNonNull(trainingRunIds, "Input list of training run ids must not be null.");
        Objects.requireNonNull(mistakeTypes, "Input list of mistake types must not be null.");
        QCommand qCommand = new QCommand("command");
        QMistake qMistake = new QMistake("mistake");
        QLevel qLevel = new QLevel("level");
        QTrainee qTrainee = new QTrainee("trainee");

        JPQLQuery<CommandPerOptions> query = new JPAQueryFactory(entityManager)
                .select(Projections.constructor(CommandPerOptions.class, qCommand.cmd, qCommand.commandType,
                        qCommand.options, qMistake.mistakeType, qCommand.fromHostIp, Wildcard.count))
                .from(qCommand)
                .join(qCommand.mistake, qMistake)
                .join(qCommand.level, qLevel)
                .join(qLevel.trainee, qTrainee)
                .where(qTrainee.trainingRunId.in(trainingRunIds)
                        .and(qMistake.mistakeType.in(mistakeTypes)))
                .groupBy(qCommand.cmd, qCommand.commandType, qCommand.options, qMistake.mistakeType, qCommand.fromHostIp);

        return query.fetch();
    }

    @Override
    @Transactional
    public List<CommandPerOptions> findCorrectCommandsByTrainingRunIds(List<Long> trainingRunIds) {
        Objects.requireNonNull(trainingRunIds, "Input list of training run ids must not be null.");
        QCommand qCommand = new QCommand("command");
        QLevel qLevel = new QLevel("level");
        QTrainee qTrainee = new QTrainee("trainee");

        JPQLQuery<CommandPerOptions> query = new JPAQueryFactory(entityManager)
                .select(Projections.constructor(CommandPerOptions.class, qCommand.cmd, qCommand.commandType,
                        qCommand.options, qCommand.fromHostIp, Wildcard.count))
                .from(qCommand)
                .join(qCommand.level, qLevel)
                .join(qLevel.trainee, qTrainee)
                .where(qTrainee.trainingRunId.in(trainingRunIds)
                        .and(qCommand.mistake.isNull()))
                .groupBy(qCommand.cmd, qCommand.commandType, qCommand.options, qCommand.fromHostIp);

        return query.fetch();
    }
}
