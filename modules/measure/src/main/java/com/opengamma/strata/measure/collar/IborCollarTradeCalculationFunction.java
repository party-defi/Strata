/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.measure.collar;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.index.Index;
import com.opengamma.strata.calc.Measure;
import com.opengamma.strata.calc.runner.CalculationFunction;
import com.opengamma.strata.calc.runner.CalculationParameters;
import com.opengamma.strata.calc.runner.FunctionRequirements;
import com.opengamma.strata.collect.result.FailureReason;
import com.opengamma.strata.collect.result.Result;
import com.opengamma.strata.data.scenario.ScenarioMarketData;
import com.opengamma.strata.measure.Measures;
import com.opengamma.strata.measure.rate.RatesMarketDataLookup;
import com.opengamma.strata.measure.rate.RatesScenarioMarketData;
import com.opengamma.strata.product.collar.IborCollar;
import com.opengamma.strata.product.collar.IborCollarTrade;
import com.opengamma.strata.product.collar.ResolvedIborCollarTrade;

/**
 * Perform calculations on a single {@code IborCollarTrade} for each of a set of scenarios.
 * <p>
 * This uses Black, Normal or SABR cap/floor volatilities,
 * which must be specified using {@link IborCollarMarketDataLookup}.
 * An instance of {@link RatesMarketDataLookup} must also be specified.
 * <p>
 * The supported built-in measures are:
 * <ul>
 *   <li>{@linkplain Measures#PRESENT_VALUE Present value}
 *   <li>{@linkplain Measures#PV01_CALIBRATED_SUM PV01 calibrated sum on rate curves}
 *   <li>{@linkplain Measures#PV01_CALIBRATED_BUCKETED PV01 calibrated bucketed on rate curves}
 *   <li>{@linkplain Measures#PV01_MARKET_QUOTE_SUM PV01 market quote sum on rate curves}
 *   <li>{@linkplain Measures#PV01_MARKET_QUOTE_BUCKETED PV01 market quote bucketed on rate curves}
 *   <li>{@linkplain Measures#CURRENCY_EXPOSURE Currency exposure}
 *   <li>{@linkplain Measures#CURRENT_CASH Current cash}
 *   <li>{@linkplain Measures#CURRENT_CASH Current cash}
 *   <li>{@linkplain Measures#RESOLVED_TARGET Resolved trade}
 * </ul>
 * <p>
 * The "natural" currency is determined from the cap/floor leg.
 */
public class IborCollarTradeCalculationFunction
    implements CalculationFunction<IborCollarTrade> {

  /**
   * The calculations by measure.
   */
  private static final ImmutableMap<Measure, SingleMeasureCalculation> CALCULATORS =
      ImmutableMap.<Measure, SingleMeasureCalculation>builder()
          .put(Measures.PRESENT_VALUE, IborCollarMeasureCalculations.DEFAULT::presentValue)
          .put(Measures.PV01_CALIBRATED_SUM, IborCollarMeasureCalculations.DEFAULT::pv01RatesCalibratedSum)
          .put(Measures.PV01_CALIBRATED_BUCKETED, IborCollarMeasureCalculations.DEFAULT::pv01RatesCalibratedBucketed)
          .put(Measures.PV01_MARKET_QUOTE_SUM, IborCollarMeasureCalculations.DEFAULT::pv01RatesMarketQuoteSum)
          .put(Measures.PV01_MARKET_QUOTE_BUCKETED, IborCollarMeasureCalculations.DEFAULT::pv01RatesMarketQuoteBucketed)
          .put(Measures.CURRENCY_EXPOSURE, IborCollarMeasureCalculations.DEFAULT::currencyExposure)
          .put(Measures.CURRENT_CASH, IborCollarMeasureCalculations.DEFAULT::currentCash)
          .put(Measures.RESOLVED_TARGET, (rt, smd, m) -> rt)
          .build();

  private static final ImmutableSet<Measure> MEASURES = CALCULATORS.keySet();

  /**
   * Creates an instance.
   */
  public IborCollarTradeCalculationFunction() {
  }

  //-------------------------------------------------------------------------
  @Override
  public Class<IborCollarTrade> targetType() {
    return IborCollarTrade.class;
  }

  @Override
  public Set<Measure> supportedMeasures() {
    return MEASURES;
  }

  @Override
  public Optional<String> identifier(IborCollarTrade target) {
    return target.getInfo().getId().map(id -> id.toString());
  }

  @Override
  public Currency naturalCurrency(IborCollarTrade trade, ReferenceData refData) {
    return trade.getProduct().getCollarLeg().getCurrency();
  }

  //-------------------------------------------------------------------------
  @Override
  public FunctionRequirements requirements(
      IborCollarTrade trade,
      Set<Measure> measures,
      CalculationParameters parameters,
      ReferenceData refData) {

    // extract data from product
    IborCollar product = trade.getProduct();
    Set<Currency> currencies = product.allPaymentCurrencies();
    Set<Index> indices = product.allIndices();

    // use lookup to build requirements
    RatesMarketDataLookup ratesLookup = parameters.getParameter(RatesMarketDataLookup.class);
    FunctionRequirements ratesReqs = ratesLookup.requirements(currencies, indices);
    IborCollarMarketDataLookup capFloorLookup = parameters.getParameter(IborCollarMarketDataLookup.class);
    FunctionRequirements capFloorReqs = capFloorLookup.requirements(product.getCollarLeg().getIndex());
    return ratesReqs.combinedWith(capFloorReqs);
  }

  //-------------------------------------------------------------------------
  @Override
  public Map<Measure, Result<?>> calculate(
      IborCollarTrade trade,
      Set<Measure> measures,
      CalculationParameters parameters,
      ScenarioMarketData scenarioMarketData,
      ReferenceData refData) {

    // expand the trade once for all measures and all scenarios
    ResolvedIborCollarTrade resolved = trade.resolve(refData);
    RatesMarketDataLookup ratesLookup = parameters.getParameter(RatesMarketDataLookup.class);
    RatesScenarioMarketData ratesMarketData = ratesLookup.marketDataView(scenarioMarketData);
    IborCollarMarketDataLookup capFloorLookup = parameters.getParameter(IborCollarMarketDataLookup.class);
    IborCollarScenarioMarketData capFloorMarketData = capFloorLookup.marketDataView(scenarioMarketData);

    // loop around measures, calculating all scenarios for one measure
    Map<Measure, Result<?>> results = new HashMap<>();
    for (Measure measure : measures) {
      results.put(measure, calculate(measure, resolved, ratesMarketData, capFloorMarketData));
    }
    return results;
  }

  // calculate one measure
  private Result<?> calculate(
      Measure measure,
      ResolvedIborCollarTrade trade,
      RatesScenarioMarketData ratesMarketData,
      IborCollarScenarioMarketData capFloorMarketData) {

    SingleMeasureCalculation calculator = CALCULATORS.get(measure);
    if (calculator == null) {
      return Result.failure(FailureReason.UNSUPPORTED, "Unsupported measure for IborCollarTrade: {}", measure);
    }
    return Result.of(() -> calculator.calculate(trade, ratesMarketData, capFloorMarketData));
  }

  //-------------------------------------------------------------------------
  @FunctionalInterface
  interface SingleMeasureCalculation {
    public abstract Object calculate(
        ResolvedIborCollarTrade trade,
        RatesScenarioMarketData ratesMarketData,
        IborCollarScenarioMarketData capFloorMarketData);
  }

}
