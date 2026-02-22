package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.exception.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.exception.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.model.Blueprint;
import java.util.Set;

public interface BlueprintPersistence {

    void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException;

    Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException;

    Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException;

    Set<Blueprint> getAllBlueprints();

    void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException;

    void updateBlueprint(String author, String name, Blueprint bp) throws BlueprintNotFoundException;

    void deleteBlueprint(String author, String name) throws BlueprintNotFoundException;
}
