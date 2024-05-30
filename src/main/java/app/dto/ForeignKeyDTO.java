package app.dto;

public class ForeignKeyDTO {
    private String name;
    private String sourceTable;
    private String sourceColumn;
    private String targetTable;
    private String targetColumn;

    public ForeignKeyDTO(String name, String sourceTable, String sourceColumn, String targetTable, String targetColumn) {
        this.name = name;
        this.sourceTable = sourceTable;
        this.sourceColumn = sourceColumn;
        this.targetTable = targetTable;
        this.targetColumn = targetColumn;
    }
}
