package edu.eci.arsw.blueprints.controllers;

import edu.eci.arsw.blueprints.dto.ApiResponseRecord;
import edu.eci.arsw.blueprints.exception.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.exception.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.services.BlueprintsServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/*
Segun el lab
200 OK (consultas exitosas).
201 Created (creación).
202 Accepted (actualizaciones).
400 Bad Request (datos inválidos).
404 Not Found (recurso inexistente
*/

@RestController
@RequestMapping("/api/v1/blueprints")
public class BlueprintsAPIController {

    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) { this.services = services; }

    // GET /blueprints
    //200
    @Operation(summary = "Get all blueprints", description = "Returns a list of all blueprints")
    @ApiResponse(description = "Successful retrieval of blueprints", responseCode = "200")
    @GetMapping
    public ResponseEntity<ApiResponseRecord<Set<Blueprint>>> getAll() {
        return ResponseEntity.ok(new ApiResponseRecord<>(200, "OK", services.getAllBlueprints()));
    }

    // GET /blueprints/{author}
    //200 y 404
    @Operation(summary = "Get blueprints by author", description = "Returns a list of blueprints for a specific author")
    @ApiResponse(description = "Successful retrieval of blueprints for the author", responseCode = "200")
    @ApiResponse(description = "No blueprints found for the author", responseCode = "404")
    @GetMapping("/{author}")
    public ResponseEntity<?> byAuthor(@PathVariable String author) {
        try {
            return ResponseEntity.ok(new ApiResponseRecord<>(200, "OK", services.getBlueprintsByAuthor(author)));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseRecord<>(404, e.getMessage(), null));
        }
    }

    // GET /blueprints/{author}/{bpname}
    //200 y 404
    @Operation(summary = "Get blueprint by author and name", description = "Returns a specific blueprint identified by author and name")
    @ApiResponse(description = "Successful retrieval of blueprint", responseCode = "200")
    @ApiResponse(description = "Blueprint not found", responseCode = "404")
    @GetMapping("/{author}/{bpname}")
    public ResponseEntity<?> byAuthorAndName(@PathVariable String author, @PathVariable String bpname) {
        try {
            return ResponseEntity.ok(new ApiResponseRecord<>(200, "OK", services.getBlueprint(author, bpname)));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseRecord<>(404, e.getMessage(), null));
        }
    }

    // POST /blueprints
    // 201 Y 400
    @Operation(summary = "Add a new blueprint", description = "Creates a new blueprint with the provided data")
    @ApiResponse(description = "Blueprint created successfully", responseCode = "201")
    @ApiResponse(description = "Invalid blueprint data", responseCode = "400")
    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody NewBlueprintRequest req) {
        try {
            Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
            services.addNewBlueprint(bp);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseRecord<>(201, "Created OK", bp));
        } catch (BlueprintPersistenceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseRecord<>(400, e.getMessage(), null));
        }
    }

    // PUT /blueprints/{author}/{bpname}/points
    //202 Y 404
    @Operation(summary = "Add a point to a blueprint", description = "Adds a new point to an existing blueprint identified by author and name")
    @ApiResponse(description = "Point added successfully", responseCode = "202")
    @ApiResponse(description = "Blueprint not found", responseCode = "404")
    @PutMapping("/{author}/{bpname}/points")
    public ResponseEntity<?> addPoint(@PathVariable String author, @PathVariable String bpname,
                                      @RequestBody Point p) {
        try {
            services.addPoint(author, bpname, p.x(), p.y());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseRecord<>(202, "Point Added", null));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseRecord<>(404, e.getMessage(), null));
        }
    }

    // Se añade segun los requerimientos del lab 04
    @PutMapping("/{author}/{bpname}")
    public ResponseEntity<?> updateBlueprint(@PathVariable String author, @PathVariable String bpname,
                                             @Valid @RequestBody NewBlueprintRequest req) {
        try {
            Blueprint updatedBp = new Blueprint(req.author(), req.name(), req.points());
            services.updateBlueprint(author, bpname, updatedBp);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponseRecord<>(202, "Blueprint updated successfully", updatedBp));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseRecord<>(404, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{author}/{bpname}")
    public ResponseEntity<?> deleteBlueprint(@PathVariable String author, @PathVariable String bpname) {
        try {
            services.deleteBlueprint(author, bpname);
            return ResponseEntity.ok(new ApiResponseRecord<>(200, "Blueprint deleted successfully", null));
        } catch (BlueprintNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseRecord<>(404, e.getMessage(), null));
        }
    }


    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid java.util.List<Point> points
    ) { }
}
