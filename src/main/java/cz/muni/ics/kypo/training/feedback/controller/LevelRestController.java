package cz.muni.ics.kypo.training.feedback.controller;

import cz.muni.ics.kypo.training.feedback.dto.provider.LevelDTO;
import cz.muni.ics.kypo.training.feedback.exceptions.errors.ApiError;
import cz.muni.ics.kypo.training.feedback.facade.LevelFacade;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "/levels", tags = "Levels", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value = "/levels", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class LevelRestController {

    private final LevelFacade levelFacade;

    @ApiOperation(httpMethod = "GET",
            value = "Get all absolved levels by trainee by his sandbox id",
            responseContainer = "List",
            response = LevelDTO.class,
            nickname = "getLevels",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Trainee level instances have been found.", response = LevelDTO.class),
            @ApiResponse(code = 404, message = "Trainee level instances have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/sandbox-id/{sandboxId}")
    public ResponseEntity<List<LevelDTO>> getLevels(@ApiParam(value = "The trainee sandbox id", required = true, type = "long") @PathVariable long sandboxId) {
        return ResponseEntity.ok(levelFacade.getLevelsByTrainee(sandboxId));
    }
}
