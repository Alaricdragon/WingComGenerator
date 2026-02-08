package main.processers;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ShipCSV_AnotationStratagy<T> extends HeaderColumnNameTranslateMappingStrategy<T> {
    private final Map<String, String> columnMap = new HashMap<>();
    public ShipCSV_AnotationStratagy(Class<T> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            CsvBindByName annotation = field.getAnnotation(CsvBindByName.class);
            if (annotation != null) {
                //this creates a new colom map. this is not enouth on its own. (as we need the colom 'data' and varuble 'data' to be diffrent, so the rest of the code works.)
                //columnMap.put(field.getName().toUpperCase(), annotation.column());
                if (!annotation.column().isBlank()) columnMap.put(field.getName().toUpperCase(), annotation.column());
                else columnMap.put(field.getName().toUpperCase(), field.getName());
            }
        }
        setType(clazz);
    }

    //this is for the varubles colom names. why this is forced into caps by default is past my understanding
    @Override
    public String getColumnName(int col) {
        String name = headerIndex.getByPosition(col);
        return name;
    }
    //this is for the headers names. basicly the varuble we built before.
    public String getHeaderName(int col){
        String name = headerIndex.getByPosition(col);
        return columnMap.getOrDefault(name, "UNDEFINED");
    }
    //finaly, generating the header names manuly. it fucking works. finaly.
    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        String[] result = super.generateHeader(bean);
        for (int i = 0; i < result.length; i++) {
            result[i] = getHeaderName(i);
            //System.out.println(result[i]);
        }
        return result;
    }
}
