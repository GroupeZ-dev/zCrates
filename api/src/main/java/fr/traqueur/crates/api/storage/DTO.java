package fr.traqueur.crates.api.storage;

/**
 * Generic Data Transfer Object (DTO) interface for converting to model objects.
 *
 * @param <T> the type of the model object
 */
public interface DTO<T> {

    /**
     * Converts this DTO to its corresponding model object.
     *
     * @return the model object of type T
     */
    T toModel();

}
