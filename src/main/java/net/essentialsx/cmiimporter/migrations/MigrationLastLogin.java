package net.essentialsx.cmiimporter.migrations;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.essentialsx.cmiimporter.CMIImporter;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MigrationLastLogin extends Migration {

    public MigrationLastLogin(CMIImporter importer, Essentials essentials, boolean requiresUsers) {
        super(importer, essentials, requiresUsers);
    }

    @Override
    public void run() {
        try {
            List<DbRow> results = DB.getResults("SELECT player_uuid, LastLoginTime FROM " + table("users") + " WHERE player_uuid IS NOT NULL AND LastLoginTime IS NOT NULL");
            for (DbRow row : results) {
                UUID uuid = UUID.fromString(row.getString("player_uuid"));
                User user = essentials.getUser(uuid);
                if (user == null) {
                    logWarning(String.format("Unable to migrate last login data for UUID %s!", uuid));
                    continue;
                }
                try {
                    long lastLoginTime = row.getLong("LastLoginTime");
                    user.setLastLogin(lastLoginTime);
                } catch (Exception ex) {
                    logWarning(String.format("Unable to migrate last login data for %s!", user.getName()));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
