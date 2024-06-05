package app.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class JDBCUtils {

    public static void dumpResultSet(ResultSet resultSet) throws SQLException {
        // Metadata
        ResultSetMetaData rsMetadata = resultSet.getMetaData();
        int columnCount = rsMetadata.getColumnCount();

        // Column names
        for (int i = 1; i <= columnCount; i++) {
            System.out.print("| " + rsMetadata.getColumnName(i) + " |");
        }
        System.out.println();

        // Data Rows
        while (resultSet.next()) {
            // Data Cells
            for (int i = 1; i <= columnCount; i++) {
                System.out.print("| " + resultSet.getString(i) + " |");
            }
            System.out.println();
        }
    }

    public static String getIdValue(ResultSet resultSet, String idColumnName) {
        try {
            int idColumnIndex = resultSet.findColumn(idColumnName);
            return resultSet.getString(idColumnIndex);
        } catch (SQLException e) {
            return null;
        }
    }
}
