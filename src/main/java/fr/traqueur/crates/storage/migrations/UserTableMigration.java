package fr.traqueur.crates.storage.migrations;

import fr.maxlego08.sarah.database.Migration;
import fr.traqueur.crates.api.storage.Tables;
import fr.traqueur.crates.storage.dto.UserDTO;

public class UserTableMigration extends Migration {
    @Override
    public void up() {
        this.createOrAlter("%prefix%"+ Tables.USERS_TABLE, UserDTO.class);
    }
}
