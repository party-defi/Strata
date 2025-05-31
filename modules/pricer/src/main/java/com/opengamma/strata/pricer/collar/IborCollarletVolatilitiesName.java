/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.collar;

import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.data.MarketDataName;
import com.opengamma.strata.pricer.collar.IborCollarletVolatilities;
import org.joda.convert.FromString;

import java.io.Serializable;

/**
 * The name of a set of Ibor cap/floor volatilities.
 */
public final class IborCollarletVolatilitiesName
    extends MarketDataName<IborCollarletVolatilities>
    implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1L;

  /**
   * The name.
   */
  private final String name;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance from the specified name.
   * <p>
   * Names may contain any character, but must not be empty.
   *
   * @param name  the name
   * @return the name instance
   */
  @FromString
  public static IborCollarletVolatilitiesName of(String name) {
    return new IborCollarletVolatilitiesName(name);
  }

  /**
   * Creates an instance.
   *
   * @param name  the name
   */
  private IborCollarletVolatilitiesName(String name) {
    this.name = ArgChecker.notEmpty(name, "name");
  }

  //-------------------------------------------------------------------------
  @Override
  public Class<IborCollarletVolatilities> getMarketDataType() {
    return IborCollarletVolatilities.class;
  }

  @Override
  public String getName() {
    return name;
  }

}
