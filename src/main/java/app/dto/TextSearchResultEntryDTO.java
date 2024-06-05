package app.dto;

public class TextSearchResultEntryDTO {
    private String tableName;
    private String columnName;
    private String rowIdValue;
    private String cellValue;

    public TextSearchResultEntryDTO(String tableName, String columnName, String rowIdValue, String cellValue) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.rowIdValue = rowIdValue;
        this.cellValue = cellValue;
    }

    @Override
    public String toString() {
        return tableName + "." + columnName + " -> " + rowIdValue + " : " + cellValue;
    }
}
