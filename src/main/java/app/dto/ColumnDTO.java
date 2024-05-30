package app.dto;

public class ColumnDTO {
    private String name;
    private String type;
    private int size;
    private boolean nullable;

    public ColumnDTO(String name, String type, int size, boolean nullable) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.nullable = nullable;
    }
}
