/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.collar;

import com.opengamma.strata.pricer.collar.VolatilityIborCollarProductPricer;
import com.opengamma.strata.pricer.swap.DiscountingSwapLegPricer;
import com.opengamma.strata.product.collar.IborCollarLeg;
import com.opengamma.strata.product.swap.SwapLeg;

/**
 * Pricer for cap/floor products in normal or Bachelier model.
 */
public class NormalIborCollarProductPricer
    extends VolatilityIborCollarProductPricer {

  /**
   * Default implementation.
   */
  public static final NormalIborCollarProductPricer DEFAULT =
      new NormalIborCollarProductPricer(NormalIborCollarLegPricer.DEFAULT, DiscountingSwapLegPricer.DEFAULT);

  /**
   * Creates an instance.
   * 
   * @param collarLegPricer  the pricer for {@link IborCollarLeg}
   * @param payLegPricer  the pricer for {@link SwapLeg}
   */
  public NormalIborCollarProductPricer(
      NormalIborCollarLegPricer collarLegPricer,
      DiscountingSwapLegPricer payLegPricer) {

    super(collarLegPricer, payLegPricer);
  }

}
