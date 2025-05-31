/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.collar;

import com.google.common.collect.ImmutableMap;
import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.collect.MapStream;
import com.opengamma.strata.market.sensitivity.PointSensitivityBuilder;
import com.opengamma.strata.pricer.collar.IborCollarletPeriodAmounts;
import com.opengamma.strata.pricer.collar.IborCollarletPeriodCurrencyAmounts;
import com.opengamma.strata.pricer.collar.IborCollarletVolatilities;
import com.opengamma.strata.pricer.collar.VolatilityIborCollarletPeriodPricer;
import com.opengamma.strata.pricer.rate.RatesProvider;
import com.opengamma.strata.product.collar.IborCollarLeg;
import com.opengamma.strata.product.collar.IborCollarletPeriod;
import com.opengamma.strata.product.collar.ResolvedIborCollarLeg;

import java.util.Map;

/**
 * Pricer for cap/floor legs based on volatilities.
 * <p>
 * This function provides the ability to price {@link ResolvedIborCollarLeg}.
 * One must apply {@code expand()} in order to price {@link IborCollarLeg}.
 * <p>
 * The pricing methodologies are defined in individual implementations of the
 * volatilities, {@link IborCollarletVolatilities}.
 */
public class VolatilityIborCollarLegPricer {

  /**
   * Default implementation.
   */
  public static final VolatilityIborCollarLegPricer DEFAULT =
      new VolatilityIborCollarLegPricer(VolatilityIborCollarletPeriodPricer.DEFAULT);

  /**
   * Pricer for {@link IborCollarletPeriod}.
   */
  private final VolatilityIborCollarletPeriodPricer periodPricer;

  /**
   * Creates an instance.
   *
   * @param periodPricer  the pricer for {@link IborCollarletPeriod}.
   */
  public VolatilityIborCollarLegPricer(VolatilityIborCollarletPeriodPricer periodPricer) {
    this.periodPricer = ArgChecker.notNull(periodPricer, "periodPricer");
  }

  //-------------------------------------------------------------------------
  /**
   * Obtains the underlying period pricer. 
   * 
   * @return the period pricer
   */
  public VolatilityIborCollarletPeriodPricer getPeriodPricer() {
    return periodPricer;
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value of the Ibor cap/floor leg.
   * <p>
   * The present value of the leg is the value on the valuation date.
   * The result is returned using the payment currency of the leg.
   * 
   * @param collarLeg  the Ibor cap/floor leg
   * @param ratesProvider  the rates provider 
   * @param volatilities  the volatilities
   * @return the present value
   */
  public CurrencyAmount presentValue(
      ResolvedIborCollarLeg collarLeg,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    validate(ratesProvider, volatilities);
    return collarLeg.getCollarletPeriods()
        .stream()
        .map(period -> periodPricer.presentValue(period, ratesProvider, volatilities))
        .reduce((c1, c2) -> c1.plus(c2))
        .get();
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value for each caplet/floorlet of the Ibor cap/floor leg.
   * <p>
   * The present value of each caplet/floorlet is the value on the valuation date.
   * The result is returned using the payment currency of the leg.
   *
   * @param collarLeg  the Ibor cap/floor leg
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the present values
   */
  public IborCollarletPeriodCurrencyAmounts presentValueCollarletPeriods(
      ResolvedIborCollarLeg collarLeg,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    validate(ratesProvider, volatilities);
    Map<IborCollarletPeriod, CurrencyAmount> periodPresentValues =
        MapStream.of(collarLeg.getCollarletPeriods())
            .mapValues(period -> periodPricer.presentValue(period, ratesProvider, volatilities))
            .toMap();
    return IborCollarletPeriodCurrencyAmounts.of(periodPresentValues);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value delta of the Ibor cap/floor leg.
   * <p>
   * The present value delta of the leg is the sensitivity value on the valuation date.
   * The result is returned using the payment currency of the leg.
   * 
   * @param collarLeg  the Ibor cap/floor leg
   * @param ratesProvider  the rates provider 
   * @param volatilities  the volatilities
   * @return the present value delta
   */
  public CurrencyAmount presentValueDelta(
      ResolvedIborCollarLeg collarLeg,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    validate(ratesProvider, volatilities);
    return collarLeg.getCollarletPeriods()
        .stream()
        .map(period -> periodPricer.presentValueDelta(period, ratesProvider, volatilities))
        .reduce((c1, c2) -> c1.plus(c2))
        .get();
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value gamma of the Ibor cap/floor leg.
   * <p>
   * The present value gamma of the leg is the sensitivity value on the valuation date.
   * The result is returned using the payment currency of the leg.
   * 
   * @param collarLeg  the Ibor cap/floor leg
   * @param ratesProvider  the rates provider 
   * @param volatilities  the volatilities
   * @return the present value gamma
   */
  public CurrencyAmount presentValueGamma(
      ResolvedIborCollarLeg collarLeg,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    validate(ratesProvider, volatilities);
    return collarLeg.getCollarletPeriods()
        .stream()
        .map(period -> periodPricer.presentValueGamma(period, ratesProvider, volatilities))
        .reduce((c1, c2) -> c1.plus(c2))
        .get();
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value theta of the Ibor cap/floor leg.
   * <p>
   * The present value theta of the leg is the sensitivity value on the valuation date.
   * The result is returned using the payment currency of the leg.
   * 
   * @param collarLeg  the Ibor cap/floor leg
   * @param ratesProvider  the rates provider 
   * @param volatilities  the volatilities
   * @return the present value theta
   */
  public CurrencyAmount presentValueTheta(
      ResolvedIborCollarLeg collarLeg,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    validate(ratesProvider, volatilities);
    return collarLeg.getCollarletPeriods()
        .stream()
        .map(period -> periodPricer.presentValueTheta(period, ratesProvider, volatilities))
        .reduce((c1, c2) -> c1.plus(c2))
        .get();
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value rates sensitivity of the Ibor cap/floor leg.
   * <p>
   * The present value rates sensitivity of the leg is the sensitivity
   * of the present value to the underlying curves.
   * 
   * @param collarLeg  the Ibor cap/floor leg
   * @param ratesProvider  the rates provider 
   * @param volatilities  the volatilities
   * @return the present value curve sensitivity 
   */
  public PointSensitivityBuilder presentValueSensitivityRates(
      ResolvedIborCollarLeg collarLeg,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    validate(ratesProvider, volatilities);
    return collarLeg.getCollarletPeriods()
        .stream()
        .map(period -> periodPricer.presentValueSensitivityRates(period, ratesProvider, volatilities))
        .reduce((p1, p2) -> p1.combinedWith(p2))
        .get();
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value volatility sensitivity of the Ibor cap/floor leg.
   * <p>
   * The present value volatility sensitivity of the leg is the sensitivity
   * of the present value to the volatility values.
   * 
   * @param collarLeg  the Ibor cap/floor leg
   * @param ratesProvider  the rates provider 
   * @param volatilities  the volatilities
   * @return the present value volatility sensitivity
   */
  public PointSensitivityBuilder presentValueSensitivityModelParamsVolatility(
      ResolvedIborCollarLeg collarLeg,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    validate(ratesProvider, volatilities);
    return collarLeg.getCollarletPeriods()
        .stream()
        .map(period -> periodPricer.presentValueSensitivityModelParamsVolatility(period, ratesProvider, volatilities))
        .reduce((c1, c2) -> c1.combinedWith(c2))
        .get();
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the current cash of the Ibor cap/floor leg.
   * 
   * @param collarLeg  the Ibor cap/floor leg
   * @param ratesProvider  the rates provider 
   * @param volatilities  the volatilities
   * @return the current cash
   */
  public CurrencyAmount currentCash(
      ResolvedIborCollarLeg collarLeg,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    validate(ratesProvider, volatilities);
    return collarLeg.getCollarletPeriods()
        .stream()
        .filter(period -> period.getPaymentDate().equals(ratesProvider.getValuationDate()))
        .map(period -> periodPricer.presentValue(period, ratesProvider, volatilities))
        .reduce((c1, c2) -> c1.plus(c2))
        .orElse(CurrencyAmount.zero(collarLeg.getCurrency()));
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the forward rates for each caplet/floorlet of the Ibor cap/floor leg.
   *
   * @param collarLeg  the Ibor cap/floor leg
   * @param ratesProvider  the rates provider
   * @return the forward rates
   */
  public IborCollarletPeriodAmounts forwardRates(
      ResolvedIborCollarLeg collarLeg,
      RatesProvider ratesProvider) {

    Map<IborCollarletPeriod, Double> forwardRates = MapStream.of(collarLeg.getCollarletPeriods())
        .filterKeys(period -> !ratesProvider.getValuationDate().isAfter(period.getFixingDate()))
        .mapValues(period -> periodPricer.forwardRate(period, ratesProvider))
        .toMap();
    return IborCollarletPeriodAmounts.of(forwardRates);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the implied volatilities for each caplet/floorlet of the Ibor cap/floor leg.
   *
   * @param collarLeg  the Ibor cap/floor leg
   * @param ratesProvider  the rates provider
   * @param volatilities the volatilities
   * @return the implied volatilities
   */
  public IborCollarletPeriodAmounts impliedVolatilities(
      ResolvedIborCollarLeg collarLeg,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    validate(ratesProvider, volatilities);
    ImmutableMap<IborCollarletPeriod, Double> impliedVolatilities = MapStream.of(collarLeg.getCollarletPeriods())
        .filterKeys(period -> volatilities.relativeTime(period.getFixingDateTime()) >= 0)
        .mapValues(period -> periodPricer.impliedVolatility(period, ratesProvider, volatilities))
        .toMap();
    return IborCollarletPeriodAmounts.of(impliedVolatilities);
  }

  //-------------------------------------------------------------------------
  protected void validate(RatesProvider ratesProvider, IborCollarletVolatilities volatilities) {
    ArgChecker.isTrue(volatilities.getValuationDate().equals(ratesProvider.getValuationDate()),
        "volatility and rate data must be for the same date");
  }

}
