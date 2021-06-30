package cz.muni.ics.kypo.training.feedback.controller;

import cz.muni.ics.kypo.training.feedback.dto.provider.TraineeDTO;
import cz.muni.ics.kypo.training.feedback.dto.resolver.DefinitionLevel;
import cz.muni.ics.kypo.training.feedback.exceptions.errors.ApiError;
import cz.muni.ics.kypo.training.feedback.facade.TraineeFacade;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "/trainees", tags = "Trainees", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value = "/trainees", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class TraineeRestController {

    private final TraineeFacade traineeFacade;

    @ApiOperation(httpMethod = "GET",
            value = "Get all trainees instances",
            responseContainer = "List",
            response = TraineeDTO.class,
            nickname = "getTraineeInstances",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainees instances have been found.", response = TraineeDTO.class),
            @ApiResponse(code = 404, message = "None trainee instance have been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("")
    public ResponseEntity<List<TraineeDTO>> getTrainees() {
        return ResponseEntity.ok(traineeFacade.getTrainees());
    }

    @ApiOperation(httpMethod = "GET",
            value = "Get trainee by sandboxId",
            response = TraineeDTO.class,
            nickname = "getTraineeInstance",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee instance have been found.", response = TraineeDTO.class),
            @ApiResponse(code = 404, message = "Trainee instance have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/sandboxes/{sandboxId}")
    public ResponseEntity<TraineeDTO> getTraineeBySandboxId(@ApiParam(value = "The trainee sandbox id", required = true, type = "long") @PathVariable long sandboxId) {
        return ResponseEntity.ok(traineeFacade.getTraineeBySandboxId(sandboxId));
    }


    @ApiOperation(httpMethod = "POST",
            value = "Create trainee instance with computed cmdfeedback",
            notes = "This should be done only once for every user",
            nickname = "createTraineeInstance",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee instance successfully created."),
            @ApiResponse(code = 404, message = "Training run with given id not found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @PostMapping("/training-definitions/{definitionId}/training-instances/{instanceId}/sandboxes/{sandboxId}")
    public ResponseEntity<Void> createTraineeBySandboxId(@ApiParam(value = "The training definition id", required = true) @PathVariable Long definitionId,
                                                         @ApiParam(value = "The training instance id", required = true) @PathVariable Long instanceId,
                                                         @ApiParam(value = "The trainee sandbox id", required = true) @PathVariable Long sandboxId,
                                                         @ApiParam(value = "Reference solutions of levels", required = true) @RequestBody List<DefinitionLevel> definitionLevels) {
        traineeFacade.createTraineeBySandboxId(definitionId, instanceId, sandboxId, definitionLevels);
        return ResponseEntity.noContent().build();
    }

}
