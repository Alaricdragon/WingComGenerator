package main.threads;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import main.Seeker;
import main.beans.Bean_Ship;
import main.processers.ShipCSV_AnotationStratagy;

import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Create_HullCSV implements Runnable{
    @Override
    public void run() {
        ArrayList<Bean_Ship> a = Seeker.shipsToPrintToCSV.getListWithLock();
        try (FileWriter writer = new FileWriter("./data/hulls/ship_data.csv")) {
            //CSVWriter writer2 = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
            StatefulBeanToCsvBuilder<Bean_Ship> builder = new StatefulBeanToCsvBuilder<>(writer);
            StatefulBeanToCsv<Bean_Ship> csvWriter = builder
                    .withMappingStrategy(new ShipCSV_AnotationStratagy<>(Bean_Ship.class))
                    .withApplyQuotesToAll(false)
                    .build();

            csvWriter.write(a);
        } catch (Exception e) {
            System.out.println("ERROR: failed for a reason of: "+e);
            throw new RuntimeException(e);
        }
        Seeker.shipsToPrintToCSV.unlock();
    }
}
