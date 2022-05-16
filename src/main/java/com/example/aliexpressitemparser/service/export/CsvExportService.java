package com.example.aliexpressitemparser.service.export;

import com.example.aliexpressitemparser.model.ProductItem;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Slf4j
public class CsvExportService {
  @Value("${export.location}")
  private String location;

  @Autowired private CsvMapper csvMapper;

  public void export(List<ProductItem> items) {
    csvMapper.disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
    CsvSchema csvSchema = csvMapper.schemaFor(ProductItem.class).withHeader();
    ObjectWriter writer = csvMapper.writer(csvSchema.withLineSeparator("\n"));

    try {
      writer.writeValue(Files.newBufferedWriter(resolveFileLocation("items")), items);
    } catch (IOException e) {
      log.error("Unable to export. Reason: {}", e.getMessage());
    }
  }

  private Path resolveFileLocation(String filename) {
    return Paths.get(location + "/" + filename + ".csv");
  }
}
