package fr.traqueur.crates.storage.migrations;

import fr.maxlego08.sarah.database.Migration;
import fr.traqueur.crates.api.storage.Tables;
import fr.traqueur.crates.storage.dto.CrateOpeningDTO;

public class CrateOpeningsTableMigration extends Migration {
    @Override
    public void up() {
        this.createOrAlter("%prefix%" + Tables.CRATE_OPENINGS_TABLE, CrateOpeningDTO.class);
    }
}