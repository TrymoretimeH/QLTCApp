package com.example.qltc.migrations;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

public class QLTCMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        if (oldVersion == 0) {
            schema.get("Transaction")
                    .addField("tempAmount", int.class);

            schema.get("Transaction")
                    .transform(obj -> obj.set("tempAmount", (int) obj.getDouble("amount")));

            schema.get("Transaction")
                    .removeField("amount");

            schema.get("Transaction")
                    .renameField("tempAmount", "amount");
            oldVersion++;
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        // Your specific comparison logic if needed
        return true;
    }

    @Override
    public int hashCode() {
        // Your specific hash code generation logic if needed
        return getClass().hashCode();
    }
}
