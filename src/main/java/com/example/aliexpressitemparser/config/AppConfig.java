package com.example.aliexpressitemparser.config;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
  @Bean
  public WebDriver getWebDriver() {
    WebDriverManager.chromedriver().setup();

    ChromeOptions options = new ChromeOptions();
    options.setHeadless(true);

    return new ChromeDriver();
  }

  @Bean
  public CsvMapper csvMapper() {
    return new CsvMapper();
  }
}
