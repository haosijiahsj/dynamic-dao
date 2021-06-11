package com.husj.dynamicdao.reflect.definition;

import com.husj.dynamicdao.utils.CollectionUtils;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TableDefinition
 *
 * @author shengjun.hu
 * @date 2021/6/8
 */
@Data
public class TableDefinition {

    private String className;
    private Class<?> clazz;
    private String tableName;
    private List<ColumnDefinition> columnDefinitions;

    public ColumnDefinition getIdColumnDefinition() {
        if (CollectionUtils.isEmpty(columnDefinitions)) {
            return null;
        }

        for (ColumnDefinition columnDefinition : columnDefinitions) {
            if (columnDefinition.isPrimaryKey()) {
                return columnDefinition;
            }
        }
        return null;
    }

    public List<String> getColumnNames() {
        if (CollectionUtils.isEmpty(columnDefinitions)) {
            return Collections.emptyList();
        }

        return columnDefinitions.stream()
                .map(ColumnDefinition::getColumnName)
                .collect(Collectors.toList());
    }

}
