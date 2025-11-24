package fr.traqueur.crates.storage.migrations;

import fr.maxlego08.sarah.database.Migration;
import fr.traqueur.crates.api.storage.Tables;

/**
 * Migration to add performance indexes on foreign key columns.
 * These indexes significantly improve query performance for:
 * - User lookups by UUID in user_keys and crate_openings tables
 * - Crate filtering in crate_openings table
 */
public class IndexesMigration extends Migration {
    @Override
    public void up() {
        this.index("%prefix%" + Tables.CRATE_OPENINGS_TABLE, "player_uuid");
        this.index("%prefix%" + Tables.CRATE_OPENINGS_TABLE, "crate_id");
    }
}