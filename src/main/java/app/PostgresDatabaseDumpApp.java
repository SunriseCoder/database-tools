package app;

import app.dto.ColumnDTO;
import app.dto.DbDumpDTO;
import app.dto.ForeignKeyDTO;
import app.dto.TableDTO;
import app.util.JSONUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class PostgresDatabaseDumpApp {

    public static void main(String[] args) {
        if (args.length < 4) {
            printUsage();
            System.exit(-1);
        }

        String url = args[0];
        String user = args[1];
        String password = args[2];

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Path outputFolder = Paths.get("data");
            Files.createDirectories(outputFolder);

            String dbDumpName = args[3];
            Path outputFile = outputFolder.resolve(dbDumpName + "-v000000.json");

            DbDumpDTO dbDump = new DbDumpDTO(dbDumpName);

            Map<String, TableDTO> tableMap = new HashMap<>();
            Map<String, ForeignKeyDTO> foreignKeyMap = new HashMap<>();

            // Columns Loop
            ResultSet columnsRS = connection.getMetaData().getColumns(null, "public", null, null);
            while (columnsRS.next()) {
                // Table
                String tableName = columnsRS.getString("TABLE_NAME");
                System.out.println("Processing table: " + tableName);
                TableDTO table = tableMap.get(tableName);
                if (table == null) {
                    table = new TableDTO(tableName);
                    dbDump.addTable(table);
                    tableMap.put(tableName, table);
                }

                // Column
                ColumnDTO column = new ColumnDTO(
                        columnsRS.getString("COLUMN_NAME"),
                        columnsRS.getString("TYPE_NAME"),
                        columnsRS.getInt("COLUMN_SIZE"),
                        columnsRS.getString("IS_NULLABLE").equals("YES"));
                table.addColumn(column);
            }

            // Foreign Keys Loop
            ResultSet foreignKeyResultSet = connection.getMetaData().getImportedKeys(null, null, null);
            while (foreignKeyResultSet.next()) {
                String foreignKeyName = foreignKeyResultSet.getString("fk_name");
                System.out.println("Processing Foreign Key: " + foreignKeyName);
                ForeignKeyDTO foreignKey = foreignKeyMap.get(foreignKeyName);
                if (foreignKey == null) {
                    foreignKey = new ForeignKeyDTO(foreignKeyName,
                            foreignKeyResultSet.getString("fktable_name"),
                            foreignKeyResultSet.getString("fkcolumn_name"),
                            foreignKeyResultSet.getString("pktable_name"),
                            foreignKeyResultSet.getString("pkcolumn_name"));

                    foreignKeyMap.put(foreignKeyName, foreignKey);
                    dbDump.addForeignKey(foreignKey);
                } else {
                    throw new UnsupportedOperationException("Foreign Key found again, probably a composite key," +
                            " which is not supported yet: " + foreignKeyName);
                }
            }

            JSONUtils.saveToDisk(dbDump, outputFile.toString());

            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println(PostgresDatabaseDumpApp.class.getSimpleName()
                + " <connection-string> <user> <password> <dump-name>");
    }
}
