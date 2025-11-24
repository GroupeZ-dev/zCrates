package fr.traqueur.crates.api.storage.repositories;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A generic repository interface for managing entities of type T with an identifier of type ID.
 * Provides methods for saving, deleting, and retrieving entities asynchronously.
 *
 * @param <T>  the type of the entity
 * @param <ID> the type of the entity's identifier
 */
public interface Repository<T, ID> {

    /**
     * Initializes the repository asynchronously.
     *
     * @return a CompletableFuture that completes with true if initialization was successful, false otherwise
     */
    CompletableFuture<Boolean> init();

    /**
     * Retrieves all items asynchronously.
     *
     * @return a CompletableFuture that completes with a list of all items
     */
    CompletableFuture<List<T>> findAll();

    /**
     * Saves the given item asynchronously.
     *
     * @param item the item to save
     * @return a CompletableFuture that completes when the save operation is done
     */
    CompletableFuture<Void> save(@NotNull T item);


    /**
     * Deletes the item with the given identifier asynchronously.
     *
     * @param id the identifier of the item to delete
     * @return a CompletableFuture that completes when the delete operation is done
     */
    CompletableFuture<Void> delete(@NotNull ID id);

    /**
     * Retrieves the item with the given identifier asynchronously.
     *
     * @param id the identifier of the item to retrieve
     * @return a CompletableFuture that completes with the retrieved item
     */
    CompletableFuture<T> get(@NotNull ID id);

}