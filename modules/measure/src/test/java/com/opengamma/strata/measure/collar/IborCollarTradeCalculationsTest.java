/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.measure.collar;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.opengamma.strata.basics.currency.MultiCurrencyAmount;
import com.opengamma.strata.data.scenario.MultiCurrencyScenarioArray;
import com.opengamma.strata.data.scenario.ScenarioArray;
import com.opengamma.strata.data.scenario.ScenarioMarketData;
import com.opengamma.strata.market.param.CurrencyParameterSensitivities;
import com.opengamma.strata.market.sensitivity.PointSensitivities;
import com.opengamma.strata.measure.rate.RatesMarketDataLookup;
import com.opengamma.strata.pricer.collar.IborCollarletVolatilities;
import com.opengamma.strata.pricer.collar.VolatilityIborCollarTradePricer;
import com.opengamma.strata.pricer.rate.RatesProvider;
import com.opengamma.strata.product.collar.ResolvedIborCollarTrade;

/**
 * Test {@link IborCollarTradeCalculations}.
 */
public class IborCollarTradeCalculationsTest {

  private static final ResolvedIborCollarTrade RTRADE = IborCollarTradeCalculationFunctionTest.RTRADE;
  private static final RatesMarketDataLookup RATES_LOOKUP = IborCollarTradeCalculationFunctionTest.RATES_LOOKUP;
  private static final IborCollarMarketDataLookup SWAPTION_LOOKUP = IborCollarTradeCalculationFunctionTest.SWAPTION_LOOKUP;
  private static final IborCollarletVolatilities VOLS = IborCollarTradeCalculationFunctionTest.VOLS;

  //-------------------------------------------------------------------------
  @Test
  public void test_presentValue() {
    ScenarioMarketData md = IborCollarTradeCalculationFunctionTest.marketData();
    RatesProvider provider = RATES_LOOKUP.marketDataView(md.scenario(0)).ratesProvider();
    VolatilityIborCollarTradePricer pricer = VolatilityIborCollarTradePricer.DEFAULT;
    MultiCurrencyAmount expectedPv = pricer.presentValue(RTRADE, provider, VOLS);
    MultiCurrencyAmount expectedCurrencyExposure = pricer.currencyExposure(RTRADE, provider, VOLS);
    MultiCurrencyAmount expectedCurrentCash = pricer.currentCash(RTRADE, provider, VOLS);

    assertThat(IborCollarTradeCalculations.DEFAULT.presentValue(RTRADE, RATES_LOOKUP, SWAPTION_LOOKUP, md))
        .isEqualTo(MultiCurrencyScenarioArray.of(ImmutableList.of(expectedPv)));
    assertThat(IborCollarTradeCalculations.DEFAULT.currencyExposure(RTRADE, RATES_LOOKUP, SWAPTION_LOOKUP, md))
        .isEqualTo(MultiCurrencyScenarioArray.of(ImmutableList.of(expectedCurrencyExposure)));
    assertThat(IborCollarTradeCalculations.DEFAULT.currentCash(RTRADE, RATES_LOOKUP, SWAPTION_LOOKUP, md))
        .isEqualTo(MultiCurrencyScenarioArray.of(ImmutableList.of(expectedCurrentCash)));
  }

  @Test
  public void test_pv01() {
    ScenarioMarketData md = IborCollarTradeCalculationFunctionTest.marketData();
    RatesProvider provider = RATES_LOOKUP.marketDataView(md.scenario(0)).ratesProvider();
    VolatilityIborCollarTradePricer pricer = VolatilityIborCollarTradePricer.DEFAULT;
    PointSensitivities pvPointSens = pricer.presentValueSensitivityRates(RTRADE, provider, VOLS);
    CurrencyParameterSensitivities pvParamSens = provider.parameterSensitivity(pvPointSens);
    MultiCurrencyAmount expectedPv01Cal = pvParamSens.total().multipliedBy(1e-4);
    CurrencyParameterSensitivities expectedPv01CalBucketed = pvParamSens.multipliedBy(1e-4);

    assertThat(IborCollarTradeCalculations.DEFAULT.pv01RatesCalibratedSum(RTRADE, RATES_LOOKUP, SWAPTION_LOOKUP, md))
        .isEqualTo(MultiCurrencyScenarioArray.of(ImmutableList.of(expectedPv01Cal)));
    assertThat(IborCollarTradeCalculations.DEFAULT.pv01RatesCalibratedBucketed(RTRADE, RATES_LOOKUP, SWAPTION_LOOKUP, md))
        .isEqualTo(ScenarioArray.of(ImmutableList.of(expectedPv01CalBucketed)));
  }

}
