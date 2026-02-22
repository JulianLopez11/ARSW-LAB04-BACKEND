package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.exception.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.exception.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
@Primary
public class PostgresBlueprintPersistence implements BlueprintPersistence {

    private final BluePrintRepository repository;

    public PostgresBlueprintPersistence(BluePrintRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {

        if (repository.findByAuthorAndName(bp.getAuthor(), bp.getName()).isPresent()) {
            throw new BlueprintPersistenceException(
                    "Blueprint already exists: " + bp.getAuthor() + "/" + bp.getName());
        }

        repository.save(bp);
    }

    @Override
    public Blueprint getBlueprint(String author, String name)
            throws BlueprintNotFoundException {

        return repository.findByAuthorAndName(author, name)
                .orElseThrow(() ->
                        new BlueprintNotFoundException(
                                "Blueprint not found: " + author + "/" + name));
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author)
            throws BlueprintNotFoundException {

        var list = repository.findByAuthor(author);

        if (list.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints for author: " + author);
        }

        return new HashSet<>(list);
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        return new HashSet<>(repository.findAll());
    }

    @Override
    public void addPoint(String author, String name, int x, int y)
            throws BlueprintNotFoundException {

        Blueprint bp = getBlueprint(author, name);
        bp.addPoint(new Point(x, y));
        repository.save(bp);
    }

    @Override
    public void updateBlueprint(String author, String name, Blueprint bp) throws BlueprintNotFoundException {
        Blueprint existing = getBlueprint(author, name);
        existing.setPoints(bp.getPoints());
        repository.save(existing);
    }
    @Override
    public void deleteBlueprint(String author, String name) throws BlueprintNotFoundException {
        Blueprint bp = getBlueprint(author, name);
        repository.delete(bp);
    }
}
