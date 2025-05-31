/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.collar;

import com.opengamma.strata.market.ValueType;
import com.opengamma.strata.market.param.ParameterPerturbation;

/**
 * Volatility for Ibor caplet/floorlet in the normal or Bachelier model.
 */
public interface NormalIborCollarletVolatilities
    extends IborCollarletVolatilities {

  @Override
  public default ValueType getVolatilityType() {
    return ValueType.NORMAL_VOLATILITY;
  }

  @Override
  public abstract NormalIborCollarletVolatilities withParameter(int parameterIndex, double newValue);

  @Override
  public abstract NormalIborCollarletVolatilities withPerturbation(ParameterPerturbation perturbation);

}
