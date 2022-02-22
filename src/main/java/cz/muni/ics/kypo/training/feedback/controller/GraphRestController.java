package cz.muni.ics.kypo.training.feedback.controller;

import cz.muni.ics.kypo.training.feedback.dto.provider.GraphDTO;
import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionLevel;
import cz.muni.ics.kypo.training.feedback.exceptions.errors.ApiError;
import cz.muni.ics.kypo.training.feedback.facade.GraphFacade;
import cz.muni.ics.kypo.training.feedback.service.MistakeAnalysisService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.junit.platform.commons.logging.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "/graphs", tags = "Graphs", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value = "/graphs", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class GraphRestController {

    private final GraphFacade graphFacade;

    @ApiOperation(httpMethod = "GET",
            value = "Get reference graph by training definition ID",
            response = GraphDTO.class,
            nickname = "getReferenceGraph",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Reference graph have been found.", response = GraphDTO.class),
            @ApiResponse(code = 404, message = "Reference graph have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/training-definitions/{definitionId}")
    public ResponseEntity<GraphDTO> getReferenceGraph(
            @ApiParam(value = "ID of the training definition.", required = true) @PathVariable Long definitionId
    ) {
        return ResponseEntity.ok(graphFacade.getReferenceGraph(definitionId));
    }

    @ApiOperation(httpMethod = "GET",
            value = "Get trainee graph by trainee training run ID",
            response = GraphDTO.class,
            nickname = "getTraineeGraph",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee graph have been found.", response = GraphDTO.class),
            @ApiResponse(code = 404, message = "Trainee graph have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/training-runs/{runId}")
    public ResponseEntity<GraphDTO> getTraineeGraph(
            @ApiParam(value = "ID of the training run.", required = true) @PathVariable Long runId
    ) {
        return ResponseEntity.ok(graphFacade.getTraineeGraph(runId));
    }

    @ApiOperation(httpMethod = "GET",
            value = "Get summary graph by training instance ID",
            response = GraphDTO.class,
            nickname = "getSummaryGraph",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Summary graph instance have been found.", response = GraphDTO.class),
            @ApiResponse(code = 404, message = "Summary graph have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/training-instances/{instanceId}")
    public ResponseEntity<GraphDTO> getSummaryGraph(
            @ApiParam(value = "Id of the training instance.", required = true) @PathVariable Long instanceId
    ) {
        return ResponseEntity.ok(graphFacade.getSummaryGraph(instanceId));
    }

    @ApiOperation(httpMethod = "POST",
            value = "Create reference graph for given training definition",
            nickname = "createReferenceGraph",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Reference graph successfully created."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @PostMapping("/training-definitions/{definitionId}")
    public ResponseEntity<GraphDTO> createReferenceGraph(
            @ApiParam(value = "ID of the training definition.", required = true) @PathVariable Long definitionId,
            @ApiParam(value = "Reference solutions of levels.") @RequestBody List<DefinitionLevel> definitionLevelList
    ) {
        return ResponseEntity.ok(graphFacade.createReferenceGraph(definitionId, definitionLevelList));
    }

    @ApiOperation(httpMethod = "POST",
            value = "Create summary graph",
            nickname = "createSummaryGraph",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Summary graph successfully created."),
            @ApiResponse(code = 404, message = "Trainees graphs needed for creation have been not found", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @PostMapping("/training-definitions/{definitionId}/training-instances/{instanceId}")
    public ResponseEntity<GraphDTO> createSummaryGraph(
            @ApiParam(value = "ID of the training definition.", required = true) @PathVariable Long definitionId,
            @ApiParam(value = "ID of the training instance.", required = true) @PathVariable Long instanceId
    ) {
        return ResponseEntity.ok(graphFacade.createSummaryGraph(definitionId, instanceId));
    }

    @ApiOperation(httpMethod = "POST",
            value = "Create trainee graph with command analysis",
            notes = "This should be done only once for every user",
            nickname = "createTraineeGraph",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee graph successfully created."),
            @ApiResponse(code = 404, message = "Training run with given ID not found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @PostMapping("/training-definitions/{definitionId}/training-instances/{instanceId}/training-runs/{runId}")
    public ResponseEntity<Void> createTraineeGraph(
            @ApiParam(value = "The training definition ID", required = true) @PathVariable Long definitionId,
            @ApiParam(value = "The training instance ID", required = true) @PathVariable Long instanceId,
            @ApiParam(value = "The trainee run ID", required = true) @PathVariable Long runId,
            @ApiParam(value = "Access token of the training instance") @RequestParam(required = false) String accessToken,
            @ApiParam(value = "Reference solutions of levels", required = true) @RequestBody List<DefinitionLevel> definitionLevels
    ) {
        if(accessToken != null) {
            graphFacade.createTraineeGraphLocalInstance(definitionId, instanceId, runId, definitionLevels, accessToken);
        } else {
            graphFacade.createTraineeGraphCloudInstance(definitionId, instanceId, runId, definitionLevels);
        }
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(httpMethod = "DELETE",
            value = "Delete reference graph",
            nickname = "deleteReferenceGraph",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Reference graph successfully deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @DeleteMapping("/reference/training-definitions/{definitionId}")
    public ResponseEntity<GraphDTO> deleteReferenceGraph(
            @ApiParam(value = "ID of the training definition.", required = true) @PathVariable Long definitionId
    ) {
        graphFacade.deleteReferenceGraph(definitionId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(httpMethod = "DELETE",
            value = "Delete all graphs by training instance",
            nickname = "deleteGraphsByTrainingInstance",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "All graphs created for trianing instance successfully deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @DeleteMapping("/training-instances/{instanceId}")
    public ResponseEntity<GraphDTO> deleteGraphsByTrainingInstance(
            @ApiParam(value = "ID of the training instance.", required = true) @PathVariable Long instanceId
    ) {
        graphFacade.deleteAllByTrainingInstance(instanceId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(httpMethod = "DELETE",
            value = "Delete summary graph",
            nickname = "deleteSummaryGraph",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Trainee graph successfully deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @DeleteMapping("/summary/training-instances/{instanceId}")
    public ResponseEntity<GraphDTO> deleteSummaryGraph(
            @ApiParam(value = "ID of the training instance.", required = true) @PathVariable Long instanceId
    ) {
        graphFacade.deleteSummaryGraph(instanceId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(httpMethod = "DELETE",
            value = "Delete trainee graph",
            nickname = "deleteTraineeGraph",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Trainee graph successfully deleted."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @DeleteMapping("/trainee/training-runs/{runId}")
    public ResponseEntity<GraphDTO> deleteTraineeGraph(
            @ApiParam(value = "ID of the training instance.", required = true) @PathVariable Long runId
    ) {
        graphFacade.deleteTraineeGraph(runId);
        return ResponseEntity.noContent().build();
    }
}
