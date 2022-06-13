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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DefaultLoaderService {
  public static final int ITEMS_IN_ROW = 6;
  @Autowired private WebDriver driver;

  public List<ProductItem> inquireItems(int limit) {
    inquireConnection();

    WebElement root = this.findItemSection();

    List<ProductItem> items = new ArrayList<>(limit);

    for (int i = 0; i < calculateIterationCount(limit); i++) {
      List<WebElement> rawItems = findElementsByClassName(root, "_3t7zg");
      rawItems.stream()
              .skip(ITEMS_IN_ROW * i)
              .limit(calculateLimit(limit, i))
              .map(this::buildProductItem)
              .forEach(items::add);

      scrollPage();
      root = findElementByClassName("_1nker");
    }

    log.info("There are {} items found", items.size());

    driver.close();

    return items;
  }

  private long calculateLimit(int limit, int i) {
    return limit / (ITEMS_IN_ROW * (i+1) ) > 0 ? ITEMS_IN_ROW : limit % ITEMS_IN_ROW;
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

  private int calculateIterationCount(int limit) {
    int count = limit / ITEMS_IN_ROW;
    return limit % ITEMS_IN_ROW > 0 ? ++count : count;
  }
}
