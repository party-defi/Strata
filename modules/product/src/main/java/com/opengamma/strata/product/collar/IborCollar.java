/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product.collar;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.ImmutableValidator;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.joda.beans.impl.direct.DirectPrivateBeanBuilder;

import com.google.common.collect.ImmutableSet;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.Resolvable;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.index.Index;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.product.Product;
import com.opengamma.strata.product.swap.SwapLeg;

/**
 * An Ibor cap/floor product.
 * <p>
 * The Ibor cap/floor product consists of two legs, a cap/floor leg and a pay leg.
 * The cap/floor leg involves a set of call/put options on successive Ibor index rates,
 * known as Ibor caplets/floorlets.
 * The pay leg is any swap leg from a standard interest rate swap. The pay leg is absent for typical
 * Ibor cap/floor products, with the premium paid upfront instead, as defined in {@link IborCollarTrade}.
 */
@BeanDefinition(builderScope = "private")
public final class IborCollar
    implements Product, Resolvable<ResolvedIborCollar>, ImmutableBean, Serializable {

  /**
   * The Ibor cap leg of the product.
   * <p>
   * This is associated with periodic payments based on Ibor rate.
   * The payments are Ibor caplets or Ibor floorlets.
   */
  @PropertyDefinition(validate = "notNull")
  private final IborCollarLeg collarLeg;

  /**
   * The optional pay leg of the product.
   * <p>
   * These periodic payments are not made for typical cap/floor products.
   * Instead, the premium is paid upfront.
   */
  @PropertyDefinition(get = "optional")
  private final SwapLeg payLeg;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance from a cap/floor leg with no pay leg.
   * <p>
   * The pay leg is absent in the resulting cap/floor.
   * 
   * @param collarLeg  the cap/floor leg
   * @return the cap/floor
   */
  public static IborCollar of(IborCollarLeg collarLeg) {
    return new IborCollar(collarLeg,  null);
  }

  /**
   * Obtains an instance from a cap/floor leg and a pay leg.
   * 
   * @param collarLeg  the cap/floor leg
   * @param payLeg  the pay leg
   * @return the cap/floor
   */
  public static IborCollar of(IborCollarLeg collarLeg, SwapLeg payLeg) {
    return new IborCollar(collarLeg, payLeg);
  }

  //-------------------------------------------------------------------------
  @ImmutableValidator
  private void validate() {
    if (payLeg != null) {
      ArgChecker.isFalse(
          payLeg.getPayReceive().equals(collarLeg.getPayReceive()),
          "Legs must have different Pay/Receive flag, but both were {}", payLeg.getPayReceive());
    }
  }

  //-------------------------------------------------------------------------
  @Override
  public ImmutableSet<Currency> allPaymentCurrencies() {
    if (payLeg == null) {
      return ImmutableSet.of(collarLeg.getCurrency());
    } else {
      return ImmutableSet.of(collarLeg.getCurrency(), payLeg.getCurrency());
    }
  }

  @Override
  public ImmutableSet<Currency> allCurrencies() {
    if (payLeg == null) {
      return ImmutableSet.of(collarLeg.getCurrency());
    } else {
      ImmutableSet.Builder<Currency> builder = ImmutableSet.builder();
      builder.add(collarLeg.getCurrency());
      builder.addAll(payLeg.allCurrencies());
      return builder.build();
    }
  }

  /**
   * Returns the set of indices referred to by the cap/floor.
   * <p>
   * A cap/floor will typically refer to one index, such as 'GBP-LIBOR-3M'.
   * Calling this method will return the complete list of indices.
   * 
   * @return the set of indices referred to by this cap/floor
   */
  public ImmutableSet<Index> allIndices() {
    ImmutableSet.Builder<Index> builder = ImmutableSet.builder();
    builder.add(collarLeg.getCalculation().getIndex());
    if (payLeg != null) {
      payLeg.collectIndices(builder);
    }
    return builder.build();
  }

  //-------------------------------------------------------------------------
  @Override
  public ResolvedIborCollar resolve(ReferenceData refData) {
    if (payLeg == null) {
      return ResolvedIborCollar.of(collarLeg.resolve(refData));
    }
    return ResolvedIborCollar.of(collarLeg.resolve(refData), payLeg.resolve(refData));
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code IborCapFloor}.
   * @return the meta-bean, not null
   */
  public static Meta meta() {
    return Meta.INSTANCE;
  }

  static {
    MetaBean.register(Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  private IborCollar(
      IborCollarLeg collarLeg,
      SwapLeg payLeg) {
    JodaBeanUtils.notNull(collarLeg, "collarLeg");

    this.collarLeg = collarLeg;
    this.payLeg = payLeg;
    validate();
  }

  @Override
  public Meta metaBean() {
    return Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the Ibor cap leg of the product.
   * <p>
   * This is associated with periodic payments based on Ibor rate.
   * The payments are Ibor caplets.
   * @return the value of the property, not null
   */
  public IborCollarLeg getCollarLeg() {
    return collarLeg;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the optional pay leg of the product.
   * <p>
   * These periodic payments are not made for typical cap/floor products.
   * Instead, the premium is paid upfront.
   * @return the optional value of the property, not null
   */
  public Optional<SwapLeg> getPayLeg() {
    return Optional.ofNullable(payLeg);
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      IborCollar other = (IborCollar) obj;
      return JodaBeanUtils.equal(collarLeg, other.collarLeg) &&
          JodaBeanUtils.equal(payLeg, other.payLeg);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(collarLeg);
    hash = hash * 31 + JodaBeanUtils.hashCode(payLeg);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("IborCapFloor{");
    buf.append("collarLeg").append('=').append(JodaBeanUtils.toString(collarLeg)).append(',').append(' ');
    buf.append("payLeg").append('=').append(JodaBeanUtils.toString(payLeg));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code IborCapFloor}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code collarLeg} property.
     */
    private final MetaProperty<IborCollarLeg> collarLeg = DirectMetaProperty.ofImmutable(
        this, "collarLeg", IborCollar.class, IborCollarLeg.class);

    /**
     * The meta-property for the {@code payLeg} property.
     */
    private final MetaProperty<SwapLeg> payLeg = DirectMetaProperty.ofImmutable(
        this, "payLeg", IborCollar.class, SwapLeg.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "collarLeg",
        "payLeg");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2124672084:  // collarLeg
          return collarLeg;
        case -995239866:  // payLeg
          return payLeg;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends IborCollar> builder() {
      return new Builder();
    }

    @Override
    public Class<? extends IborCollar> beanType() {
      return IborCollar.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code collarLeg} property.
     * @return the meta-property, not null
     */
    public MetaProperty<IborCollarLeg> collarLeg() {
      return collarLeg;
    }

    /**
     * The meta-property for the {@code payLeg} property.
     * @return the meta-property, not null
     */
    public MetaProperty<SwapLeg> payLeg() {
      return payLeg;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 2124672084:  // collarLeg
          return ((IborCollar) bean).getCollarLeg();
        case -995239866:  // payLeg
          return ((IborCollar) bean).payLeg;
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code IborCapFloor}.
   */
  private static final class Builder extends DirectPrivateBeanBuilder<IborCollar> {

    private IborCollarLeg collarLeg;
    private SwapLeg payLeg;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 2124672084:  // collarLeg
          return collarLeg;
        case -995239866:  // payLeg
          return payLeg;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 2124672084:  // collarLeg
          this.collarLeg = (IborCollarLeg) newValue;
          break;
        case -995239866:  // payLeg
          this.payLeg = (SwapLeg) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public IborCollar build() {
      return new IborCollar(
          collarLeg,
          payLeg);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("IborCapFloor.Builder{");
      buf.append("collarLeg").append('=').append(JodaBeanUtils.toString(collarLeg)).append(',').append(' ');
      buf.append("payLeg").append('=').append(JodaBeanUtils.toString(payLeg));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
