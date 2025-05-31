package com.opengamma.strata.loader.json;

import java.io.IOException;

//import com.fasterxml.jackson.databind.ObjectMapper;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.product.Product;
import com.opengamma.strata.product.Trade;
import com.opengamma.strata.product.TradeInfo;

/**
 * Generic trade loader that parses a Trade from JSON.
 * <p>
 * This class uses {@code jackson-jr-objects} to parse the JSON file.
 * <p>
 * The JSON must represent a {@link Trade}.
 */
public class JsonTradeLoader {

//  /**
//   * Parses a JSON file to create a trade.
//   *
//   * @param json  the JSON file content
//   * @return the parsed trade
//   * @throws IllegalArgumentException if the JSON is invalid
//   */
//  public static Trade parseTrade(String json) {
//    ArgChecker.notNull(json, "fileContent");
//    ObjectMapper mapper = new ObjectMapper();
//    try {
//      return mapper.readValue(json, Trade.class);
//    } catch (IOException ex) {
//      throw new IllegalArgumentException("Unable to parse JSON", ex);
//    }
//  }

//-------------------------------------------------------------------------
//  /**
//   * Parses a JSON file to create a trade.
//   * <p>
//   * This method allows additional information, such as trade date, to be parsed from the JSON.
//   *
//   * @param json  the JSON file content
//   * @return the parsed trade
//   * @throws IllegalArgumentException if the JSON is invalid
//   */
//  public static Trade parseTradeWithTradeInfo(String json) {
//    ArgChecker.notNull(json, "json");
//    try {
//      JsonTradeStructure structure = JSON.std.beanFrom(JsonTradeStructure.class, json);
//      TradeInfo tradeInfo = structure.getTradeInfo();
//      Product product = structure.getProduct();
//      return Trade.of(tradeInfo, product);
//    } catch (IOException ex) {
//      throw new IllegalArgumentException("Unable to parse JSON", ex);
//    }
//  }

  //-------------------------------------------------------------------------
  // structure used when parsing trade info
  private static class JsonTradeStructure {
    private TradeInfo tradeInfo;
    private Product product;

    public JsonTradeStructure() {
    }

    public TradeInfo getTradeInfo() {
      return tradeInfo;
    }

    public void setTradeInfo(TradeInfo tradeInfo) {
      this.tradeInfo = tradeInfo;
    }

    public Product getProduct() {
      return product;
    }

    public void setProduct(Product product) {
      this.product = product;
    }
  }
}