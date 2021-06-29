package cz.muni.ics.kypo.training.feedback.mapping;

import cz.muni.ics.kypo.training.feedback.dto.provider.GraphDTO;
import cz.muni.ics.kypo.training.feedback.model.Graph;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GraphMapper {

    @Mapping(target = "graph", expression = "java(entity.toString())")
    GraphDTO mapGraphToGraphDTO(Graph entity);
}
