package app;

import app.dto.TextSearchResultEntryDTO;
import app.util.JDBCUtils;
import app.util.JSONUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class PostgresDatabaseSearchApp {
    private static final long DATA_FETCH_ROWS_BATCH_SIZE = 100000;

    public static void main(String[] args) {
        if (args.length < 6) {
            printUsage();
            System.exit(-1);
        }

        String url = args[0];
        String user = args[1];
        String password = args[2];

        String searchType = args[3];
        String searchCondition = args[4];
        String searchText = args[5];

        List<TextSearchResultEntryDTO> searchResult = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // Tables Loop
            ResultSet tableRS = connection.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
            while (tableRS.next()) {
                String tableName = tableRS.getString("TABLE_NAME");
                System.out.print("Searching in Table: " + tableName + " ");

                long tableStartTime = System.currentTimeMillis();
                long rowTotalCounter = 0;
                long rowBatchCounter = 0;
                long dataBatchLimit = DATA_FETCH_ROWS_BATCH_SIZE;
                // Loop over all Batches to load all data for a Table
                do {
                    rowBatchCounter = 0;

                    long dataBatchOffset = rowTotalCounter;
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT * FROM " + tableName + " LIMIT ? OFFSET ?");
                    statement.setLong(1, dataBatchLimit);
                    statement.setLong(2, dataBatchOffset);

                    ResultSet dataRS = statement.executeQuery();
                    ResultSetMetaData dataRSMetaData = dataRS.getMetaData();
                    int columnAmount = dataRSMetaData.getColumnCount();

                    // Loop over Batches for Table Data
                    while (dataRS.next()) {
                        rowTotalCounter++;
                        if (rowTotalCounter % 1000000 == 0) {
                            System.out.print(".");
                        }

                        // Loop over Record Columns
                        String id = JDBCUtils.getIdValue(dataRS, "id");
                        for (int i = 1; i <= columnAmount; i++) {
                            String databaseCellValue = dataRS.getString(i);
                            if (databaseCellValue != null && databaseCellValue.contains(searchText)) {
                                TextSearchResultEntryDTO searchResultEntry = new TextSearchResultEntryDTO(
                                        tableName, dataRSMetaData.getColumnName(i), id, dataRS.getString(i)
                                );
                                searchResult.add(searchResultEntry);

                                System.out.println();
                                System.out.println("\tFound result: " + searchResultEntry);
                                break;
                            }
                        }

                        rowBatchCounter++;
                    }
                } while (rowBatchCounter == DATA_FETCH_ROWS_BATCH_SIZE);
                long tableEndTime = System.currentTimeMillis();
                System.out.println(" - rows: " + rowTotalCounter +
                        ", took: " + ((tableEndTime - tableStartTime) / 1000) + "s");
            }

            System.out.println("Saving search-results...");
            JSONUtils.saveToDisk(searchResult, "search-results.json");

            System.out.println("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println(PostgresDatabaseSearchApp.class.getSimpleName()
                + " <connection-string> <user> <password> <type> <condition> <search-text>");
        System.out.println("<type> - for example: String, Number");
        System.out.println("<condition> - for example: Contains, StartsWith, EndsWith, Equals, >, <, >=, <=");
    }
}
