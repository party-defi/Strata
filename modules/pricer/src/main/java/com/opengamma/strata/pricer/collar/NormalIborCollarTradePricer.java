/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.collar;

import com.opengamma.strata.basics.currency.Payment;
import com.opengamma.strata.pricer.DiscountingPaymentPricer;
import com.opengamma.strata.pricer.collar.VolatilityIborCollarTradePricer;
import com.opengamma.strata.product.collar.ResolvedIborCollar;

/**
 * Pricer for cap/floor trades in normal or Bachelier model.
 */
public class NormalIborCollarTradePricer
    extends VolatilityIborCollarTradePricer {

  /**
   * Default implementation.
   */
  public static final NormalIborCollarTradePricer DEFAULT =
      new NormalIborCollarTradePricer(NormalIborCollarProductPricer.DEFAULT, DiscountingPaymentPricer.DEFAULT);

  /**
   * Creates an instance.
   * 
   * @param productPricer  the pricer for {@link ResolvedIborCollar}
   * @param paymentPricer  the pricer for {@link Payment}
   */
  public NormalIborCollarTradePricer(
      NormalIborCollarProductPricer productPricer,
      DiscountingPaymentPricer paymentPricer) {

    super(productPricer, paymentPricer);
  }

}
