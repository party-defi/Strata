/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.collar;

import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.currency.MultiCurrencyAmount;
import com.opengamma.strata.basics.currency.Payment;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.market.sensitivity.PointSensitivities;
import com.opengamma.strata.market.sensitivity.PointSensitivityBuilder;
import com.opengamma.strata.pricer.DiscountingPaymentPricer;
import com.opengamma.strata.pricer.rate.RatesProvider;
import com.opengamma.strata.product.collar.IborCollarTrade;
import com.opengamma.strata.product.collar.ResolvedIborCollar;
import com.opengamma.strata.product.collar.ResolvedIborCollarTrade;

/**
 * Pricer for cap/floor trades based on volatilities.
 * <p>
 * This function provides the ability to price {@link IborCollarTrade}.
 * The pricing methodologies are defined in individual implementations of the
 * volatilities, {@link IborCollarletVolatilities}.
 * <p>
 * Greeks of the underlying product are computed in the product pricer, {@link VolatilityIborCollarProductPricer}.
 */
public class VolatilityIborCollarTradePricer {

  /**
   * Default implementation.
   */
  public static final VolatilityIborCollarTradePricer DEFAULT =
      new VolatilityIborCollarTradePricer(VolatilityIborCollarProductPricer.DEFAULT, DiscountingPaymentPricer.DEFAULT);
  /**
   * Pricer for {@link ResolvedIborCollar}.
   */
  private final VolatilityIborCollarProductPricer productPricer;
  /**
   * Pricer for {@link Payment}.
   */
  private final DiscountingPaymentPricer paymentPricer;

  /**
   * Creates an instance.
   * 
   * @param productPricer  the pricer for {@link ResolvedIborCollar}
   * @param paymentPricer  the pricer for {@link Payment}
   */
  public VolatilityIborCollarTradePricer(
      VolatilityIborCollarProductPricer productPricer,
      DiscountingPaymentPricer paymentPricer) {

    this.productPricer = ArgChecker.notNull(productPricer, "productPricer");
    this.paymentPricer = ArgChecker.notNull(paymentPricer, "paymentPricer");
  }

  /**
   * Gets the payment pricer.
   * 
   * @return the payment pricer
   */
  protected DiscountingPaymentPricer getPaymentPricer() {
    return paymentPricer;
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value of the Ibor cap/floor trade.
   * <p>
   * The present value of the trade is the value on the valuation date.
   * <p>
   * The cap/floor leg and pay leg are typically in the same currency, thus the
   * present value gamma is expressed as a single currency amount in most cases.
   * 
   * @param trade  the Ibor cap/floor trade
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the present value
   */
  public MultiCurrencyAmount presentValue(
      ResolvedIborCollarTrade trade,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    MultiCurrencyAmount pvProduct = productPricer.presentValue(trade.getProduct(), ratesProvider, volatilities);
    if (!trade.getPremium().isPresent()) {
      return pvProduct;
    }
    CurrencyAmount pvPremium = paymentPricer.presentValue(trade.getPremium().get(), ratesProvider);
    return pvProduct.plus(pvPremium);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value for each caplet/floorlet of the Ibor cap/floor trade.
   * <p>
   * The present value of each caplet/floorlet is the value on the valuation date.
   * The result is returned using the payment currency of the leg.
   * <p>
   * The present value will not be calculated for the trade premium or for the pay leg
   * if the cap/floor product has one.
   *
   * @param trade  the Ibor cap/floor leg
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the present values
   */
  public IborCollarletPeriodCurrencyAmounts presentValueCollarletPeriods(
      ResolvedIborCollarTrade trade,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    return productPricer.presentValueCollarletPeriods(trade.getProduct(), ratesProvider, volatilities);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value rates sensitivity of the Ibor cap/floor trade.
   * <p>
   * The present value rates sensitivity of the trade is the sensitivity
   * of the present value to the underlying curves.
   * 
   * @param trade  the Ibor cap/floor trade
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the present value sensitivity
   */
  public PointSensitivities presentValueSensitivityRates(
      ResolvedIborCollarTrade trade,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    PointSensitivityBuilder pvSensiProduct =
        productPricer.presentValueSensitivityRates(trade.getProduct(), ratesProvider, volatilities);
    if (!trade.getPremium().isPresent()) {
      return pvSensiProduct.build();
    }
    PointSensitivityBuilder pvSensiPremium =
        paymentPricer.presentValueSensitivity(trade.getPremium().get(), ratesProvider);
    return pvSensiProduct.combinedWith(pvSensiPremium).build();
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the present value volatility sensitivity of the Ibor cap/floor product.
   * <p>
   * The present value volatility sensitivity of the product is the sensitivity
   * of the present value to the volatility values.
   * 
   * @param trade  the Ibor cap/floor trade
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the present value sensitivity
   */
  public PointSensitivityBuilder presentValueSensitivityModelParamsVolatility(
      ResolvedIborCollarTrade trade,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    return productPricer.presentValueSensitivityModelParamsVolatility(trade.getProduct(), ratesProvider, volatilities);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the currency exposure of the Ibor cap/floor trade.
   * 
   * @param trade  the Ibor cap/floor trade
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the currency exposure
   */
  public MultiCurrencyAmount currencyExposure(
      ResolvedIborCollarTrade trade,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    MultiCurrencyAmount ceProduct = productPricer.currencyExposure(trade.getProduct(), ratesProvider, volatilities);
    if (!trade.getPremium().isPresent()) {
      return ceProduct;
    }
    CurrencyAmount pvPremium = paymentPricer.presentValue(trade.getPremium().get(), ratesProvider);
    return ceProduct.plus(pvPremium);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the current cash of the Ibor cap/floor trade.
   * 
   * @param trade  the Ibor cap/floor trade
   * @param ratesProvider  the rates provider
   * @param volatilities  the volatilities
   * @return the current cash
   */
  public MultiCurrencyAmount currentCash(
      ResolvedIborCollarTrade trade,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    MultiCurrencyAmount ccProduct = productPricer.currentCash(trade.getProduct(), ratesProvider, volatilities);
    if (!trade.getPremium().isPresent()) {
      return ccProduct;
    }
    Payment premium = trade.getPremium().get();
    if (premium.getDate().equals(ratesProvider.getValuationDate())) {
      ccProduct = ccProduct.plus(premium.getCurrency(), premium.getAmount());
    }
    return ccProduct;
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the forward rates for each caplet/floorlet of the Ibor cap/floor trade.
   *
   * @param trade  the Ibor cap/floor trade
   * @param ratesProvider  the rates provider
   * @return the forward rates
   */
  public IborCollarletPeriodAmounts forwardRates(
      ResolvedIborCollarTrade trade,
      RatesProvider ratesProvider) {

    return productPricer.forwardRates(trade.getProduct(), ratesProvider);
  }

  //-------------------------------------------------------------------------
  /**
   * Calculates the implied volatilities for each caplet/floorlet of the Ibor cap/floor trade.
   *
   * @param trade  the Ibor cap/floor trade
   * @param ratesProvider  the rates provider
   * @param volatilities the volatilities
   * @return the implied volatilities
   */
  public IborCollarletPeriodAmounts impliedVolatilities(
      ResolvedIborCollarTrade trade,
      RatesProvider ratesProvider,
      IborCollarletVolatilities volatilities) {

    return productPricer.impliedVolatilities(trade.getProduct(), ratesProvider, volatilities);
  }

}
