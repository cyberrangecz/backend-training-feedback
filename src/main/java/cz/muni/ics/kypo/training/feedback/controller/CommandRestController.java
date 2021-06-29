package cz.muni.ics.kypo.training.feedback.controller;

import cz.muni.ics.kypo.training.feedback.dto.provider.AggregatedCommandDTO;
import cz.muni.ics.kypo.training.feedback.dto.provider.AggregatedWrongCommandsDTO;
import cz.muni.ics.kypo.training.feedback.dto.provider.CommandDTO;
import cz.muni.ics.kypo.training.feedback.dto.provider.WrongCommandDTO;
import cz.muni.ics.kypo.training.feedback.enums.MistakeType;
import cz.muni.ics.kypo.training.feedback.exceptions.errors.ApiError;
import cz.muni.ics.kypo.training.feedback.facade.CommandFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Commands", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class CommandRestController {

    private final CommandFacade commandFacade;

    @ApiOperation(httpMethod = "GET",
            value = "Get aggregated commands from all trainees.",
            responseContainer = "List",
            response = AggregatedCommandDTO.class,
            nickname = "getAggregatedCommands",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Aggregated commands have been found.", response = AggregatedCommandDTO.class),
            @ApiResponse(code = 404, message = "Aggregated commands have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/commands")
    public ResponseEntity<List<AggregatedCommandDTO>> getCommands() {
        return ResponseEntity.ok(commandFacade.getCommands());
    }

    @ApiOperation(httpMethod = "GET",
            value = "Get all valid commands of trainee.",
            responseContainer = "List",
            response = CommandDTO.class,
            nickname = "getTraineeValidCommands",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee commands have been found.", response = CommandDTO.class),
            @ApiResponse(code = 404, message = "Trainee commands have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/commands/sandbox-id/{sandboxId}")
    public ResponseEntity<List<CommandDTO>> getCommands(@ApiParam(value = "The trainee sandbox id", required = true, type = "long") @PathVariable long sandboxId) {
        return ResponseEntity.ok(commandFacade.getCommands(sandboxId));
    }

    @ApiOperation(httpMethod = "GET",
            value = "Get all valid commands of trainee in some level",
            responseContainer = "List",
            response = CommandDTO.class,
            nickname = "getTraineeValidCommandsInSpecificLevel",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee commands have been found.", response = CommandDTO.class),
            @ApiResponse(code = 404, message = "Trainee commands have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/commands/sandbox-id/{sandboxId}/level-id/{levelId}")
    public ResponseEntity<List<CommandDTO>> getCommands(@ApiParam(value = "The trainee sandbox id", required = true, type = "long") @PathVariable long sandboxId,
                                                        @ApiParam(value = "The trainees level id", required = true, type = "long") @PathVariable long levelId) {
        return ResponseEntity.ok(commandFacade.getCommands(sandboxId, levelId));
    }

    @ApiOperation(httpMethod = "GET",
            value = "Get commands with specific mistake type for group of trainees",
            responseContainer = "List",
            response = WrongCommandDTO.class,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainees invalid commands have been found.", response = CommandDTO.class),
            @ApiResponse(code = 404, message = "Trainees invalid commands have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/error-commands")
    public ResponseEntity<List<AggregatedWrongCommandsDTO>> getWrongCommands(@ApiParam(value = "The trainees sandbox ids", required = true, type = "List of numbers") @RequestParam List<Long> sandboxIds,
                                                                             @ApiParam(value = "Mistake type", required = true, type = "List of strings") @RequestParam List<MistakeType> mistakeTypes) {
        return ResponseEntity.ok(commandFacade.getAggregatedWrongCommands(sandboxIds, mistakeTypes));
    }

}


