package fr.traqueur.crates.api.storage.repositories;

import fr.maxlego08.sarah.Column;
import fr.maxlego08.sarah.RequestHelper;
import fr.traqueur.crates.api.storage.Tables;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Base class for SQL repositories.
 * Provides common functionality for repositories that use SQL as a storage source.
 *
 * @param <T>  the type of the entity
 * @param <ID> the type of the entity's identifier
 */
public abstract class SQLRepository<T, D, ID> implements Repository<T, ID> {

    protected final RequestHelper requestHelper;
    protected Class<D> dataClass = getDataClass();

    public CompletableFuture<Boolean> init() {
        return createTable();
    }

    @SuppressWarnings("unchecked")
    private Class<D> getDataClass() {
        // Check generic superclass first (for classes that extend SQLRepository)
        Type genericSuperclass = getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type rawType = parameterizedType.getRawType();
            if (rawType instanceof Class<?> rawClass &&
                    SQLRepository.class.isAssignableFrom(rawClass)) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 1) {
                    return (Class<D>) actualTypeArguments[1];
                }
            }
        }
        throw new IllegalStateException("Could not determine data class for repository: " + getClass().getName());
    }

    /**
     * Constructor for SQLRepository.
     *
     * @param requestHelper the request helper to be used by this repository
     */
    public SQLRepository(RequestHelper requestHelper) {
        this.requestHelper = requestHelper;
    }

    protected String getPrimaryKeyColumn() {
        for (RecordComponent recordComponent : dataClass.getRecordComponents()) {
            if(recordComponent.isAnnotationPresent(Column.class)) {
                Column column = recordComponent.getAnnotation(Column.class);
                if(column.primary()) {
                    return column.value();
                }
            }
        }
        throw new IllegalStateException("No primary key column found in data class: " + dataClass.getName());
    }

    /**
     * Creates the table for the entity in the database.
     * This method should be implemented by subclasses to define the table structure.
     *
     * @return a CompletableFuture that completes when the table is created
     */
    public abstract CompletableFuture<Boolean> createTable();

}