package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class DbDumpDTO {
    private String name;
    private List<TableDTO> tables;
    private List<ForeignKeyDTO> foreignKeys;

    public DbDumpDTO(String name) {
        this.name = name;
        tables = new ArrayList<>();
        foreignKeys = new ArrayList<>();
    }

    @JsonIgnore
    public String getName() {
        return name;
    }

    public void addTable(TableDTO table) {
        tables.add(table);
    }

    public void addForeignKey(ForeignKeyDTO foreignKey) {
        foreignKeys.add(foreignKey);
    }
}
