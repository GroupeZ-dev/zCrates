package fr.traqueur.crates.api.settings.models;

import fr.maxlego08.sarah.DatabaseConnection;
import fr.traqueur.structura.annotations.Polymorphic;
import fr.traqueur.structura.api.Loadable;

@Polymorphic
public interface DatabaseSettings extends Loadable {

    String tablePrefix();

    DatabaseConnection connection(boolean debug);

}
