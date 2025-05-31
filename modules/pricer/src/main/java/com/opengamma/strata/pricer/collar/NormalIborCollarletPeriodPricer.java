/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.collar;

import com.opengamma.strata.collect.ArgChecker;

/**
 * Pricer for caplet/floorlet in a normal or Bachelier model.
 * <p>
 * The value of the caplet/floorlet after expiry is a fixed payoff amount. The value is zero if valuation date is 
 * after payment date of the caplet/floorlet.
 */
public class NormalIborCollarletPeriodPricer
    extends VolatilityIborCollarletPeriodPricer {

  /**
   * Default implementation.
   */
  public static final NormalIborCollarletPeriodPricer DEFAULT = new NormalIborCollarletPeriodPricer();

  @Override
  protected void validate(IborCollarletVolatilities volatilities) {
    ArgChecker.isTrue(volatilities instanceof NormalIborCollarletVolatilities, "volatilities must be normal volatilities");
  }

}
