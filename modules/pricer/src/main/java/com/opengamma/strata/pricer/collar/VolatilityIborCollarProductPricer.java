/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.collar;

import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.currency.MultiCurrencyAmount;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.market.sensitivity.PointSensitivityBuilder;
import com.opengamma.strata.pricer.collar.IborCollarletPeriodAmounts;
import com.opengamma.strata.pricer.collar.IborCollarletPeriodCurrencyAmounts;
import com.opengamma.strata.pricer.collar.IborCollarletVolatilities;
import com.opengamma.strata.pricer.collar.VolatilityIborCollarLegPricer;
import com.opengamma.strata.pricer.rate.RatesProvider;
import com.opengamma.strata.pricer.swap.DiscountingSwapLegPricer;
import com.opengamma.strata.product.collar.IborCollarLeg;
import com.opengamma.strata.product.collar.ResolvedIborCollar;
import com.opengamma.strata.product.swap.SwapLeg;

/**
 * Pricer for cap/floor products based on volatilities.
 * <p>
 * This function provides the ability to price {@link ResolvedIborCollar}.
 * <p>
 * The pricing methodologies are defined in individual implementations of the
 * volatilities, {@link IborCollarletVolatilities}.
 */
public class VolatilityIborCollarProductPricer {

  /**
   * Default implementation.
   */
  public static final VolatilityIborCollarProductPricer DEFAULT =
      new VolatilityIborCollarProductPricer(VolatilityIborCollarLegPricer.DEFAULT, DiscountingSwapLegPricer.DEFAULT);
  /**
   * The pricer for {@link IborCollarLeg}.
   */
  private final VolatilityIborCollarLegPricer collarLegPricer;
  /**
   * The pricer for {@link SwapLeg}.
   */
  private final DiscountingSwapLegPricer payLegPricer;

  /**
   * Creates an instance.
   *
   * @param collarLegPricer  the pricer for {@link IborCollarLeg}
   * @param payLegPricer  the pricer for {@link SwapLeg}
   */
  public VolatilityIborCollarProductPricer(
      VolatilityIborCollarLegPricer collarLegPricer,
      DiscountingSwapLegPricer payLegPricer) {

    this.collarLegPricer = ArgChecker.notNull(collarLegPricer, "collarLegPricer");
    this.payLegPricer = ArgChecker.notNull(payLegPricer, "payLegPricer");
  }

  /**
   * Gets the pay leg pricer.
   * 
   * @return the pay leg pricer
   */
  protected DiscountingSwapLegPricer getPayLegPricer() {
    return payLegPricer;
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value of the Ibor cap/floor product.
   * <p>
   * The present value of the product is the value on the valuation date.
   * <p>
   * The cap/floor leg and pay leg are typically in the same currency, thus the
   * present value gamma is expressed as a single currency amount in most cases.
   * 
   * @param collar  the Ibor cap/floor product
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the present value
   */
  public MultiCurrencyAmount presentValue(
      ResolvedIborCollar collar,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    CurrencyAmount pvCollarLeg =
        collarLegPricer.presentValue(collar.getCollarLeg(), ratesProvider, volatilities);
    if (!collar.getPayLeg().isPresent()) {
      return MultiCurrencyAmount.of(pvCollarLeg);
    }
    CurrencyAmount pvPayLeg = payLegPricer.presentValue(collar.getPayLeg().get(), ratesProvider);
    return MultiCurrencyAmount.of(pvCollarLeg).plus(pvPayLeg);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value for each caplet/floorlet of the Ibor cap/floor product.
   * <p>
   * The present value of each collarlet is the value on the valuation date.
   * The result is returned using the payment currency of the leg.
   * <p>
   * The present value will not be calculated for the pay leg if the product has one.
   *
   * @param collar  the Ibor collar product
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the present values
   */
  public IborCollarletPeriodCurrencyAmounts presentValueCollarletPeriods(
      ResolvedIborCollar collar,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    return collarLegPricer.presentValueCollarletPeriods(collar.getCollarLeg(), ratesProvider, volatilities);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value delta of the Ibor cap/floor product.
   * <p>
   * The present value of the product is the sensitivity value on the valuation date.
   * <p>
   * The cap/floor leg and pay leg are typically in the same currency, thus the
   * present value gamma is expressed as a single currency amount in most cases.
   * 
   * @param collar  the Ibor cap/floor product
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the present value delta
   */
  public MultiCurrencyAmount presentValueDelta(
      ResolvedIborCollar collar,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    CurrencyAmount pvCollarLeg =
        collarLegPricer.presentValueDelta(collar.getCollarLeg(), ratesProvider, volatilities);
    return MultiCurrencyAmount.of(pvCollarLeg);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value gamma of the Ibor cap/floor product.
   * <p>
   * The present value of the product is the sensitivity value on the valuation date.
   * <p>
   * The cap/floor leg and pay leg are typically in the same currency, thus the
   * present value gamma is expressed as a single currency amount in most cases.
   * 
   * @param collar  the Ibor cap/floor product
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the present value gamma
   */
  public MultiCurrencyAmount presentValueGamma(
      ResolvedIborCollar collar,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    CurrencyAmount pvCollarLeg =
        collarLegPricer.presentValueGamma(collar.getCollarLeg(), ratesProvider, volatilities);
    return MultiCurrencyAmount.of(pvCollarLeg);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value theta of the Ibor cap/floor product.
   * <p>
   * The present value of the product is the sensitivity value on the valuation date.
   * <p>
   * The cap/floor leg and pay leg are typically in the same currency, thus the
   * present value gamma is expressed as a single currency amount in most cases.
   * 
   * @param collar  the Ibor cap/floor product
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the present value theta
   */
  public MultiCurrencyAmount presentValueTheta(
      ResolvedIborCollar collar,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    CurrencyAmount pvCollarLeg =
        collarLegPricer.presentValueTheta(collar.getCollarLeg(), ratesProvider, volatilities);
    return MultiCurrencyAmount.of(pvCollarLeg);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value rates sensitivity of the Ibor cap/floor product.
   * <p>
   * The present value rates sensitivity of the product is the sensitivity
   * of the present value to the underlying curves.
   * 
   * @param collar  the Ibor cap/floor product
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the present value sensitivity
   */
  public PointSensitivityBuilder presentValueSensitivityRates(
      ResolvedIborCollar collar,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    PointSensitivityBuilder pvSensiCollarLeg =
        collarLegPricer.presentValueSensitivityRates(collar.getCollarLeg(), ratesProvider, volatilities);
    if (!collar.getPayLeg().isPresent()) {
      return pvSensiCollarLeg;
    }
    PointSensitivityBuilder pvSensiPayLeg =
        payLegPricer.presentValueSensitivity(collar.getPayLeg().get(), ratesProvider);
    return pvSensiCollarLeg.combinedWith(pvSensiPayLeg);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value volatility sensitivity of the Ibor cap/floor product.
   * <p>
   * The present value volatility sensitivity of the product is the sensitivity
   * of the present value to the volatility values.
   * 
   * @param collar  the Ibor cap/floor product
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the present value sensitivity
   */
  public PointSensitivityBuilder presentValueSensitivityModelParamsVolatility(
      ResolvedIborCollar collar,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    return collarLegPricer.presentValueSensitivityModelParamsVolatility(
        collar.getCollarLeg(), ratesProvider, volatilities);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the currency exposure of the Ibor cap/floor product.
   * 
   * @param collar  the Ibor cap/floor product
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the currency exposure
   */
  public MultiCurrencyAmount currencyExposure(
      ResolvedIborCollar collar,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    CurrencyAmount ceCollarLeg =
        collarLegPricer.presentValue(collar.getCollarLeg(), ratesProvider, volatilities);
    if (!collar.getPayLeg().isPresent()) {
      return MultiCurrencyAmount.of(ceCollarLeg);
    }
    MultiCurrencyAmount cePayLeg = payLegPricer.currencyExposure(collar.getPayLeg().get(), ratesProvider);
    return cePayLeg.plus(ceCollarLeg);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the current cash of the Ibor cap/floor product.
   * 
   * @param collar  the Ibor cap/floor product
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the current cash
   */
  public MultiCurrencyAmount currentCash(
      ResolvedIborCollar collar,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    CurrencyAmount ccCollarLeg =
        collarLegPricer.currentCash(collar.getCollarLeg(), ratesProvider, volatilities);
    if (!collar.getPayLeg().isPresent()) {
      return MultiCurrencyAmount.of(ccCollarLeg);
    }
    CurrencyAmount ccPayLeg = payLegPricer.currentCash(collar.getPayLeg().get(), ratesProvider);
    return MultiCurrencyAmount.of(ccPayLeg).plus(ccCollarLeg);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the forward rates for each caplet/floorlet of the Ibor cap/floor.
   *
   * @param collar  the Ibor cap/floor
   * @param ratesProvider  the rates provider
   * @return the forward rates
   */
  public IborCollarletPeriodAmounts forwardRates(
      ResolvedIborCollar collar,
      RatesProvider ratesProvider) {

    return collarLegPricer.forwardRates(collar.getCollarLeg(), ratesProvider);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the implied volatilities for each caplet/floorlet of the Ibor cap/floor.
   *
   * @param collar  the Ibor cap/floor
   * @param ratesProvider  the rates provider
   * @param volatilities the volatilities
   * @return the implied volatilities
   */
  public IborCollarletPeriodAmounts impliedVolatilities(
      ResolvedIborCollar collar,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    return collarLegPricer.impliedVolatilities(collar.getCollarLeg(), ratesProvider, volatilities);
  }

}
