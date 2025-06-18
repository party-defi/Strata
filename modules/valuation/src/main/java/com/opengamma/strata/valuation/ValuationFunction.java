/*
 * Copyright (C) 2023 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.valuation;

/**
 * Interface for performing valuation functions using JSON representations.
 * <p>
 * This interface provides a top-level valuation function that takes JSON representations
 * of Strata objects and returns a JSON representation of the results.
 */
public interface ValuationFunction {

  /**
   * Performs valuation on a trade using the specified market data, reference data, and control parameters.
   * 
   * @param tradeJson  the JSON representation of a {@code strata.product.Trade} object
   * @param marketDataJson  the JSON representation of a {@code strata.data.MarketData} object
   * @param referenceDataJson  the JSON representation of a {@code strata.basics.ReferenceData} object
   * @param controlJson  the JSON representation of a Control object containing valuation datetime,
   *   list of {@code strata.calc.Column} that defines the measures to calculate,
   *   and a {@code strata.calc.CalculationRules}
   * @return the JSON representation of a {@code strata.calc.Results} object
   */
  String valuate(String tradeJson, String marketDataJson, String referenceDataJson, String controlJson);

}