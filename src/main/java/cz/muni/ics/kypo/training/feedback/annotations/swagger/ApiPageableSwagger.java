package cz.muni.ics.kypo.training.feedback.annotations.swagger;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The interface Api pageable swagger.
 */
@Target({METHOD, ANNOTATION_TYPE, TYPE})
@Retention(RUNTIME)
@ApiImplicitParams({
        @ApiImplicitParam(name = "page", dataType = "integer", dataTypeClass = Integer.class, paramType = "query", value = "Results page you want to retrieve (0..N)", example = "0"),
        @ApiImplicitParam(name = "size", dataType = "integer", dataTypeClass = Integer.class, paramType = "query", value = "Number of records per page.", example = "20"),
        @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", dataTypeClass = String.class, paramType = "query", value = "Sorting criteria in the format: property(,asc|desc). "
                + "Default sort order is ascending. " + "Multiple sort criteria are supported.", example = "asc")})
public @interface ApiPageableSwagger {
}

