package cz.muni.ics.kypo.training.feedback.controller;

import cz.muni.ics.kypo.training.feedback.dto.provider.AggregatedCommandsDTO;
import cz.muni.ics.kypo.training.feedback.dto.provider.CommandDTO;
import cz.muni.ics.kypo.training.feedback.enums.MistakeType;
import cz.muni.ics.kypo.training.feedback.exceptions.errors.ApiError;
import cz.muni.ics.kypo.training.feedback.facade.CommandFacade;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Commands", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(path = "/commands", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class CommandRestController {

    private final CommandFacade commandFacade;

    @ApiOperation(httpMethod = "GET",
            value = "Get aggregated correct commands from given trainees",
            responseContainer = "List",
            response = AggregatedCommandsDTO.class,
            nickname = "getAggregatedCorrectCommands",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Aggregated correct commands have been found.", response = AggregatedCommandsDTO.class),
            @ApiResponse(code = 404, message = "Aggregated correct commands have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/correct")
    public ResponseEntity<List<AggregatedCommandsDTO>> getAggregatedCorrectCommands(
            @ApiParam(value = "The trainees training run ids", required = true) @RequestParam List<Long> runIds
    ) {
        return ResponseEntity.ok(commandFacade.getAggregatedCorrectCommands(runIds));
    }

    @ApiOperation(httpMethod = "GET",
            value = "Get all commands entered in the given training run",
            responseContainer = "List",
            response = CommandDTO.class,
            nickname = "getCommandsByTrainingRun",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee commands have been found.", response = CommandDTO.class),
            @ApiResponse(code = 404, message = "Trainee commands have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/training-runs/{runId}")
    public ResponseEntity<List<CommandDTO>> getCommandsByTrainingRun(
            @ApiParam(value = "The trainee's training run id", required = true) @PathVariable Long runId
    ) {
        return ResponseEntity.ok(commandFacade.getCommandsByTrainingRun(runId));
    }

    @ApiOperation(httpMethod = "GET",
            value = "Get all commands entered in the given training run and specified level",
            responseContainer = "List",
            response = CommandDTO.class,
            nickname = "getCommandsByTrainingRunAndLevel",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee commands have been found.", response = CommandDTO.class),
            @ApiResponse(code = 404, message = "Trainee commands have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/training-runs/{runId}/levels/{levelId}")
    public ResponseEntity<List<CommandDTO>> getCommandsByTrainingRunAndLevel(
            @ApiParam(value = "The trainee sandbox id", required = true) @PathVariable Long runId,
            @ApiParam(value = "The trainees level id", required = true) @PathVariable Long levelId
    ) {
        return ResponseEntity.ok(commandFacade.getCommandsByTrainingRunAndLevel(runId, levelId));
    }

    @ApiOperation(httpMethod = "GET",
            value = "Get aggregated incorrect commands from given trainees and with specific mistake type",
            responseContainer = "List",
            response = AggregatedCommandsDTO.class,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainees invalid commands have been found.", response = CommandDTO.class),
            @ApiResponse(code = 404, message = "Trainees invalid commands have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/incorrect")
    public ResponseEntity<List<AggregatedCommandsDTO>> getAggregatedIncorrectCommands(
            @ApiParam(value = "The trainees sandbox ids", required = true) @RequestParam List<Long> runIds,
            @ApiParam(value = "Mistake types", required = true) @RequestParam List<MistakeType> mistakeTypes
    ) {
        return ResponseEntity.ok(commandFacade.getAggregatedIncorrectCommands(runIds, mistakeTypes));
    }
}


