/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.collar;

import com.opengamma.strata.pricer.collar.NormalIborCollarletPeriodPricer;
import com.opengamma.strata.pricer.collar.VolatilityIborCollarLegPricer;
import com.opengamma.strata.product.collar.IborCollarletPeriod;

/**
 * Pricer for cap/floor legs in normal or Bachelier model.
 */
public class NormalIborCollarLegPricer
    extends VolatilityIborCollarLegPricer {

  /**
  * Default implementation.
  */
  public static final NormalIborCollarLegPricer DEFAULT =
      new NormalIborCollarLegPricer(NormalIborCollarletPeriodPricer.DEFAULT);

  /**
   * Creates an instance.
   * 
   * @param periodPricer  the pricer for {@link IborCollarletPeriod}.
   */
  public NormalIborCollarLegPricer(NormalIborCollarletPeriodPricer periodPricer) {
    super(periodPricer);
  }

}
