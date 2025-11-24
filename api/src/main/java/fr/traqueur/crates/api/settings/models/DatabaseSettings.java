package fr.traqueur.crates.api.settings.models;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.traqueur.structura.annotations.Polymorphic;
import fr.traqueur.structura.api.Loadable;

/**
 * Interface representing database settings for the application.
 *
 * <p>This interface extends {@link Loadable}, allowing implementations
 * to load database configuration details such as table prefixes and
 * connection parameters.</p>
 *
 * @see Loadable
 * @see DatabaseConnection
 */
@Polymorphic
public interface DatabaseSettings extends Loadable {

    /**
     * Retrieves the table prefix used in the database.
     *
     * @return the table prefix as a {@code String}
     */
    String tablePrefix();

    /**
     * Creates a new database connection based on the settings.
     *
     * @param debug {@code true} to enable debug mode, {@code false} otherwise
     * @return a new {@link DatabaseConnection} instance
     */
    DatabaseConnection connection(boolean debug);

}
