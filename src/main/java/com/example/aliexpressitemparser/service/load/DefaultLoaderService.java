package com.example.aliexpressitemparser.service.load;

import com.example.aliexpressitemparser.model.ProductItem;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DefaultLoaderService {
  @Autowired private WebDriver driver;

  public List<ProductItem> inquireItems() {
    inquireConnection();

    WebElement root = this.findItemSection();
    List<WebElement> rawItems = findElementsByClassName(root, "_3t7zg");
    log.info("There are {} items found", rawItems.size());

    List<ProductItem> productItems = rawItems.stream().map(this::buildProductItem).collect(Collectors.toList());

    driver.close();

    return productItems;
  }

  public void inquireConnection() {
    PageFactory.initElements(driver, this);
    driver.manage().window().maximize();
    driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    driver.get("https://www.aliexpress.com");
    String title = driver.getTitle();
    log.info("Connected to {}", title);
  }

  public ProductItem buildProductItem(WebElement element) {
    String link = element != null ? element.getAttribute("href") : null;

    WebElement imageElement = findElementByClassName(element, "_1RtJV");
    String imageUrl = imageElement != null ? imageElement.getAttribute("src") : null;

    WebElement titleElement = findElementByClassName(element, ("_18_85"));
    String title = titleElement != null ? titleElement.getText() : null;

    WebElement priceElement = findElementByClassName(element, ("mGXnE"));
    String price = priceElement != null ? priceElement.getText() : null;

    WebElement soldElement = findElementByClassName(element, ("_1kNf9"));
    String soldCount =
        soldElement != null ? soldElement.getText().replaceAll("[a-zA-Z]+", "").trim() : null;

    WebElement rateElement = findElementByClassName(element, ("eXPaM"));
    String rate = rateElement != null ? rateElement.getText() : null;

    boolean hasFreeShipping = findElementByClassName(element, "_2jcMA") != null;

    return ProductItem.builder()
        .title(title)
        .link(link)
        .imageUrl(imageUrl)
        .price(price)
        .soldCount(soldCount)
        .rate(rate)
        .hasFreeShipping(hasFreeShipping)
        .build();
  }

  public WebElement findElementByClassName(String className) {
    try {
      return driver.findElement(By.className(className));
    } catch (Exception e) {
      return null;
    }
  }

  public WebElement findElementByClassName(WebElement element, String className) {
    try {
      return element.findElement(By.className(className));
    } catch (Exception e) {
      return null;
    }
  }

  public List<WebElement> findElementsByClassName(WebElement element, String className) {
    try {
      return element.findElements(By.className(className));
    } catch (Exception e) {
      return Collections.emptyList();
    }
  }

  public void scrollPage() {
    driver.findElement(By.cssSelector("body")).sendKeys(Keys.PAGE_DOWN);
  }

  private WebElement findItemSection() {
    WebElement root = findElementByClassName("_1nker");
    if (root == null) {
      scrollPage();
      return findItemSection();
    } else {
      return root;
    }
  }
}
