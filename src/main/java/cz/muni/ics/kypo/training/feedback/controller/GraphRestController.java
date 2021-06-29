package cz.muni.ics.kypo.training.feedback.controller;

import cz.muni.ics.kypo.training.feedback.dto.provider.GraphDTO;
import cz.muni.ics.kypo.training.feedback.exceptions.errors.ApiError;
import cz.muni.ics.kypo.training.feedback.facade.GraphFacade;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "/graphs", tags = "Graphs", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value = "/graphs", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class GraphRestController {

    private final GraphFacade graphFacade;

    @ApiOperation(httpMethod = "GET",
            value = "Get graph by trainee sandbox id",
            response = GraphDTO.class,
            nickname = "getGraphBySandboxId",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Graph instance have been found.", response = GraphDTO.class),
            @ApiResponse(code = 404, message = "Graph instance have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/sandbox-id/{sandboxId}")
    public ResponseEntity<GraphDTO> getGraph(@ApiParam(value = "The trainee sandbox id", required = true, type = "long") @PathVariable long sandboxId) {
        return ResponseEntity.ok(graphFacade.getGraph(sandboxId));
    }

    @ApiOperation(httpMethod = "GET",
            value = "Get graph by label",
            notes = "For example for getting Reference graph or summary one.",
            response = GraphDTO.class,
            nickname = "getGraphByLabel",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Graph instance have been found.", response = GraphDTO.class),
            @ApiResponse(code = 404, message = "Graph instance have not been found.", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @GetMapping("/label/{label}")
    public ResponseEntity<GraphDTO> getGraph(@ApiParam(value = "Graph label", required = true, type = "string") @PathVariable String label) {
        return ResponseEntity.ok(graphFacade.getGraph(label));
    }

    @ApiOperation(httpMethod = "POST",
            value = "Create reference graph",
            notes = "This should be done only once, before first one user logged in.",
            nickname = "createReferenceGraph",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Reference graph successfully created."),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @PostMapping("/reference-graph")
    public ResponseEntity<GraphDTO> createReferenceGraph() {
        return ResponseEntity.ok(graphFacade.createReferenceGraph());
    }

    @ApiOperation(httpMethod = "POST",
            value = "Create summary graph",
            notes = "This should be done only once after all trainees end up their training",
            nickname = "createSummaryGraph",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Summary graph successfully created."),
            @ApiResponse(code = 404, message = "Trainees graphs needed for creation have been not found", response = ApiError.class),
            @ApiResponse(code = 500, message = "Unexpected condition was encountered.", response = ApiError.class)
    })
    @PostMapping("/summary-graph")
    public ResponseEntity<GraphDTO> createSummaryGraph() {
        return ResponseEntity.ok(graphFacade.createSummaryGraph());
    }

}
