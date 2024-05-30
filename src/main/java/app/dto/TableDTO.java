package app.dto;

import java.util.ArrayList;
import java.util.List;

public class TableDTO {
    private String name;
    private List<ColumnDTO> columns;

    public TableDTO(String name) {
        this.name = name;
        columns = new ArrayList<>();
    }

    public void addColumn(ColumnDTO column) {
        columns.add(column);
    }
}
