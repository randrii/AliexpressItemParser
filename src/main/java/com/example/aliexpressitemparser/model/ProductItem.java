package com.example.aliexpressitemparser.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductItem {
  private String link;
  private String imageUrl;
  private String title;
  private String price;
  private String soldCount;
  private String rate;
  private boolean hasFreeShipping;
}
