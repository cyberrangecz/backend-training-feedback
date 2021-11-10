package cz.muni.ics.kypo.training.feedback.controller;

import cz.muni.ics.kypo.training.feedback.dto.provider.TraineeDTO;
import cz.muni.ics.kypo.training.feedback.exceptions.errors.ApiError;
import cz.muni.ics.kypo.training.feedback.facade.TraineeFacade;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(value = "/trainees", tags = "Trainees", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(path = "/trainees", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class TraineeRestController {

    private final TraineeFacade traineeFacade;

    @ApiOperation(httpMethod = "GET",
            value = "Get all trainees by training instance ID",
            responseContainer = "List",
            response = TraineeDTO.class,
            nickname = "getTraineesByTrainingInstanceId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainees have been found.", response = TraineeDTO.class),
            @ApiResponse(code = 404, message = "None trainee instance have been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/training-instances/{instanceId}")
    public ResponseEntity<List<TraineeDTO>> getTraineesByTrainingInstanceId(
            @ApiParam(value = "ID of the training instance", required = true, type = "long")
            @PathVariable Long instanceId
    ) {
        return ResponseEntity.ok(traineeFacade.getTraineesByTrainingInstanceId(instanceId));
    }

    @ApiOperation(httpMethod = "GET",
            value = "Get trainee by training run ID",
            response = TraineeDTO.class,
            nickname = "getTraineeByTrainingRunId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee has been found.", response = TraineeDTO.class),
            @ApiResponse(code = 404, message = "Trainee has not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/training-runs/{runId}")
    public ResponseEntity<TraineeDTO> getTraineeByTrainingRunId(
            @ApiParam(value = "ID of the training run", required = true, type = "long") @PathVariable Long runId
    ) {
        return ResponseEntity.ok(traineeFacade.getTraineeByTrainingRunId(runId));
    }

}
