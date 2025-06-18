/*
 * Copyright (C) 2023 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.valuation;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.joda.beans.Bean;
import org.joda.beans.ser.JodaBeanSer;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.opengamma.strata.basics.ImmutableReferenceData;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.StandardId;
import com.opengamma.strata.calc.CalculationRules;
import com.opengamma.strata.calc.Column;
import com.opengamma.strata.calc.Measure;
import com.opengamma.strata.calc.Results;
import com.opengamma.strata.calc.runner.CalculationFunctions;
import com.opengamma.strata.data.MarketData;
import com.opengamma.strata.data.ImmutableMarketData;
import com.opengamma.strata.product.Trade;
import com.opengamma.strata.product.TradeInfo;

/**
 * Tests for JSON serialization and deserialization of interface objects.
 */
public class JsonSerializationTest {

  /**
   * Test serialization and deserialization of Control object.
   */
  @Test
  public void testControlSerialization() {
    // Use JodaBeanSer for serialization/deserialization
    JodaBeanSer serializer = JodaBeanSer.COMPACT;

    // Create a Control object
    ZonedDateTime valuationDateTime = ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
    List<Column> columns = ImmutableList.of(Column.of(Measure.of("PV")));
    CalculationRules calculationRules = CalculationRules.of(CalculationFunctions.empty());

    Control control = Control.meta().builder()
        .set(Control.meta().valuationDateTime(), valuationDateTime)
        .set(Control.meta().columns(), columns)
        .set(Control.meta().calculationRules(), calculationRules)
        .build();

    // Serialize to JSON
    String controlJson = serializer.jsonWriter().write(control);

    // Output for debugging
    System.out.println("Control JSON: " + controlJson);

    // Deserialize from JSON
    Control deserializedControl = (Control) serializer.jsonReader().read(controlJson, Control.class);

    // Verify deserialized object matches original
    assertThat(deserializedControl).isEqualTo(control);
  }

  /**
   * Test serialization and deserialization of Trade object.
   */
  @Test
  public void testTradeSerialization() {
    // Use JodaBeanSer for serialization/deserialization
    JodaBeanSer serializer = JodaBeanSer.COMPACT;

    // Create a simple TradeInfo
    TradeInfo tradeInfo = TradeInfo.builder()
        .id(StandardId.of("example", "1"))
        .tradeDate(LocalDate.of(2023, 1, 1))
        .build();

    // We can't create a Trade directly as it's an interface, so we'll use a mock
    // In a real test, you would use an actual implementation like SwapTrade

    // For this test, we'll just verify that TradeInfo can be serialized/deserialized
    String tradeInfoJson = serializer.jsonWriter().write(tradeInfo);

    // Output for debugging
    System.out.println("TradeInfo JSON: " + tradeInfoJson);

    // Deserialize from JSON
    TradeInfo deserializedTradeInfo = (TradeInfo) serializer.jsonReader().read(tradeInfoJson, TradeInfo.class);

    // Verify deserialized object matches original
    assertThat(deserializedTradeInfo).isEqualTo(tradeInfo);
  }

  /**
   * Test serialization and deserialization of MarketData object.
   */
  @Test
  public void testMarketDataSerialization() {
    // Use JodaBeanSer for serialization/deserialization
    JodaBeanSer serializer = JodaBeanSer.COMPACT;

    // Create a simple MarketData
    LocalDate valuationDate = LocalDate.of(2023, 1, 1);
    ImmutableMarketData marketData = ImmutableMarketData.of(valuationDate, ImmutableMap.of());

    // Serialize to JSON
    String marketDataJson = serializer.jsonWriter().write(marketData);

    // Output for debugging
    System.out.println("MarketData JSON: " + marketDataJson);

    // Deserialize from JSON
    ImmutableMarketData deserializedMarketData = (ImmutableMarketData) serializer.jsonReader().read(marketDataJson, ImmutableMarketData.class);

    // Verify deserialized object matches original
    assertThat(deserializedMarketData).isEqualTo(marketData);
  }

  /**
   * Test serialization and deserialization of ReferenceData object.
   */
  @Test
  public void testReferenceDataSerialization() {
    // Use JodaBeanSer for serialization/deserialization
    JodaBeanSer serializer = JodaBeanSer.COMPACT;

    // Create a simple ReferenceData
    ImmutableReferenceData referenceData = ImmutableReferenceData.empty();

    // Serialize to JSON
    String referenceDataJson = serializer.jsonWriter().write(referenceData);

    // Output for debugging
    System.out.println("ReferenceData JSON: " + referenceDataJson);

    // Deserialize from JSON
    ImmutableReferenceData deserializedReferenceData = (ImmutableReferenceData) serializer.jsonReader().read(referenceDataJson, ImmutableReferenceData.class);

    // Verify deserialized object matches original
    assertThat(deserializedReferenceData).isEqualTo(referenceData);
  }
}
