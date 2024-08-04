/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product.collar;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.beans.Bean;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.gen.BeanDefinition;
import org.joda.beans.gen.ImmutableConstructor;
import org.joda.beans.gen.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ImmutableList;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.currency.Currency;
import com.opengamma.strata.basics.index.IborIndex;
import com.opengamma.strata.collect.ArgChecker;
import com.opengamma.strata.product.common.PayReceive;

/**
 * An Ibor cap/floor leg of an Ibor cap/floor product, resolved for pricing.
 * <p>
 * This is the resolved form of {@link IborCollarLeg} and is an input to the pricers.
 * Applications will typically create a {@code ResolvedCmLegs} from a {@code IborCollarLeg}
 * using {@link IborCollarLeg#resolve(ReferenceData)}.
 * <p>
 * This defines a single leg for an Ibor cap/floor product and is formed from a number of periods.
 * Each period may be a caplet or floorlet.
 * The cap/floor instruments are defined as a set of call/put options on successive Ibor index rates.
 * <p>
 * A {@code ResolvedIborCollarLeg} is bound to data that changes over time, such as holiday calendars.
 * If the data changes, such as the addition of a new holiday, the resolved form will not be updated.
 * Care must be taken when placing the resolved form in a cache or persistence layer.
 */
@BeanDefinition
public final class ResolvedIborCollarLeg
    implements ImmutableBean, Serializable {

  /**
   * Whether the leg is pay or receive.
   * <p>
   * A value of 'Pay' implies that the resulting amount is paid to the counterparty.
   * A value of 'Receive' implies that the resulting amount is received from the counterparty.
   * <p>
   * The value of this flag should match the signs of the payment period notionals.
   */
  @PropertyDefinition(validate = "notNull")
  private final PayReceive payReceive;
  /**
   * The periodic payments based on the successive observed values of an Ibor index.
   * <p>
   * Each payment period represents part of the life-time of the leg.
   * In most cases, the periods do not overlap. However, since each payment period
   * is essentially independent the data model allows overlapping periods.
   */
  @PropertyDefinition(validate = "notEmpty")
  private final ImmutableList<IborCollarletPeriod> collarletPeriods;

  //-------------------------------------------------------------------------
  @ImmutableConstructor
  private ResolvedIborCollarLeg(
      PayReceive payReceive,
      List<IborCollarletPeriod> collarletPeriods) {

    this.payReceive = ArgChecker.notNull(payReceive, "payReceive");
    this.collarletPeriods = ImmutableList.copyOf(collarletPeriods);
    Set<Currency> currencies =
        this.collarletPeriods.stream().map(IborCollarletPeriod::getCurrency).collect(Collectors.toSet());
    ArgChecker.isTrue(currencies.size() == 1, "Leg must have a single currency, found: " + currencies);
    Set<IborIndex> iborIndices =
        this.collarletPeriods.stream().map(IborCollarletPeriod::getIndex).collect(Collectors.toSet());
    ArgChecker.isTrue(iborIndices.size() == 1, "Leg must have a single Ibor index: " + iborIndices);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the accrual start date of the leg.
   * <p>
   * This is the first accrual date in the leg, often known as the effective date.
   * This date has typically been adjusted to be a valid business day.
   * 
   * @return the start date of the leg
   */
  public LocalDate getStartDate() {
    return collarletPeriods.get(0).getStartDate();
  }

  /**
   * Gets the accrual end date of the leg.
   * <p>
   * This is the last accrual date in the leg, often known as the termination date.
   * This date has typically been adjusted to be a valid business day.
   * 
   * @return the end date of the leg
   */
  public LocalDate getEndDate() {
    return collarletPeriods.get(collarletPeriods.size() - 1).getEndDate();
  }

  /**
   * Gets the fixing date time of the final caplet/floorlet period.
   * 
   * @return the fixing date time
   */
  public ZonedDateTime getFinalFixingDateTime() {
    return collarletPeriods.get(collarletPeriods.size() - 1).getFixingDateTime();
  }

  /**
   * Gets the final caplet/floorlet period.
   * 
   * @return the final period
   */
  public IborCollarletPeriod getFinalPeriod() {
    return collarletPeriods.get(collarletPeriods.size() - 1);
  }

  /**
   * Gets the currency of the leg.
   * <p>
   * All periods in the leg will have this currency.
   * 
   * @return the currency
   */
  public Currency getCurrency() {
    return collarletPeriods.get(0).getCurrency();
  }

  /**
   * Gets the Ibor index of the leg.
   * <p>
   * All periods in the leg will have this index.
   * 
   * @return the index
   */
  public IborIndex getIndex() {
    return collarletPeriods.get(0).getIndex();
  }

  //------------------------- AUTOGENERATED START -------------------------
  /**
   * The meta-bean for {@code ResolvedIborCollarLeg}.
   * @return the meta-bean, not null
   */
  public static ResolvedIborCollarLeg.Meta meta() {
    return ResolvedIborCollarLeg.Meta.INSTANCE;
  }

  static {
    MetaBean.register(ResolvedIborCollarLeg.Meta.INSTANCE);
  }

  /**
   * The serialization version id.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static ResolvedIborCollarLeg.Builder builder() {
    return new ResolvedIborCollarLeg.Builder();
  }

  @Override
  public ResolvedIborCollarLeg.Meta metaBean() {
    return ResolvedIborCollarLeg.Meta.INSTANCE;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets whether the leg is pay or receive.
   * <p>
   * A value of 'Pay' implies that the resulting amount is paid to the counterparty.
   * A value of 'Receive' implies that the resulting amount is received from the counterparty.
   * <p>
   * The value of this flag should match the signs of the payment period notionals.
   * @return the value of the property, not null
   */
  public PayReceive getPayReceive() {
    return payReceive;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the periodic payments based on the successive observed values of an Ibor index.
   * <p>
   * Each payment period represents part of the life-time of the leg.
   * In most cases, the periods do not overlap. However, since each payment period
   * is essentially independent the data model allows overlapping periods.
   * @return the value of the property, not empty
   */
  public ImmutableList<IborCollarletPeriod> getCapletFloorletPeriods() {
    return collarletPeriods;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ResolvedIborCollarLeg other = (ResolvedIborCollarLeg) obj;
      return JodaBeanUtils.equal(payReceive, other.payReceive) &&
          JodaBeanUtils.equal(collarletPeriods, other.collarletPeriods);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(payReceive);
    hash = hash * 31 + JodaBeanUtils.hashCode(collarletPeriods);
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("ResolvedIborCollarLeg{");
    buf.append("payReceive").append('=').append(JodaBeanUtils.toString(payReceive)).append(',').append(' ');
    buf.append("collarletPeriods").append('=').append(JodaBeanUtils.toString(collarletPeriods));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ResolvedIborCollarLeg}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code payReceive} property.
     */
    private final MetaProperty<PayReceive> payReceive = DirectMetaProperty.ofImmutable(
        this, "payReceive", ResolvedIborCollarLeg.class, PayReceive.class);
    /**
     * The meta-property for the {@code collarletPeriods} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableList<IborCollarletPeriod>> collarletPeriods = DirectMetaProperty.ofImmutable(
        this, "collarletPeriods", ResolvedIborCollarLeg.class, (Class) ImmutableList.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "payReceive",
        "collarletPeriods");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -885469925:  // payReceive
          return payReceive;
        case 1504863482:  // collarletPeriods
          return collarletPeriods;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public ResolvedIborCollarLeg.Builder builder() {
      return new ResolvedIborCollarLeg.Builder();
    }

    @Override
    public Class<? extends ResolvedIborCollarLeg> beanType() {
      return ResolvedIborCollarLeg.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code payReceive} property.
     * @return the meta-property, not null
     */
    public MetaProperty<PayReceive> payReceive() {
      return payReceive;
    }

    /**
     * The meta-property for the {@code collarletPeriods} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableList<IborCollarletPeriod>> collarletPeriods() {
      return collarletPeriods;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -885469925:  // payReceive
          return ((ResolvedIborCollarLeg) bean).getPayReceive();
        case 1504863482:  // collarletPeriods
          return ((ResolvedIborCollarLeg) bean).getCapletFloorletPeriods();
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
   * The bean-builder for {@code ResolvedIborCollarLeg}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<ResolvedIborCollarLeg> {

    private PayReceive payReceive;
    private List<IborCollarletPeriod> collarletPeriods = ImmutableList.of();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(ResolvedIborCollarLeg beanToCopy) {
      this.payReceive = beanToCopy.getPayReceive();
      this.collarletPeriods = beanToCopy.getCapletFloorletPeriods();
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case -885469925:  // payReceive
          return payReceive;
        case 1504863482:  // collarletPeriods
          return collarletPeriods;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -885469925:  // payReceive
          this.payReceive = (PayReceive) newValue;
          break;
        case 1504863482:  // collarletPeriods
          this.collarletPeriods = (List<IborCollarletPeriod>) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public ResolvedIborCollarLeg build() {
      return new ResolvedIborCollarLeg(
          payReceive,
          collarletPeriods);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets whether the leg is pay or receive.
     * <p>
     * A value of 'Pay' implies that the resulting amount is paid to the counterparty.
     * A value of 'Receive' implies that the resulting amount is received from the counterparty.
     * <p>
     * The value of this flag should match the signs of the payment period notionals.
     * @param payReceive  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder payReceive(PayReceive payReceive) {
      JodaBeanUtils.notNull(payReceive, "payReceive");
      this.payReceive = payReceive;
      return this;
    }

    /**
     * Sets the periodic payments based on the successive observed values of an Ibor index.
     * <p>
     * Each payment period represents part of the life-time of the leg.
     * In most cases, the periods do not overlap. However, since each payment period
     * is essentially independent the data model allows overlapping periods.
     * @param collarletPeriods  the new value, not empty
     * @return this, for chaining, not null
     */
    public Builder collarletPeriods(List<IborCollarletPeriod> collarletPeriods) {
      JodaBeanUtils.notEmpty(collarletPeriods, "collarletPeriods");
      this.collarletPeriods = collarletPeriods;
      return this;
    }

    /**
     * Sets the {@code collarletPeriods} property in the builder
     * from an array of objects.
     * @param collarletPeriods  the new value, not empty
     * @return this, for chaining, not null
     */
    public Builder collarletPeriods(IborCollarletPeriod... collarletPeriods) {
      return collarletPeriods(ImmutableList.copyOf(collarletPeriods));
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("ResolvedIborCollarLeg.Builder{");
      buf.append("payReceive").append('=').append(JodaBeanUtils.toString(payReceive)).append(',').append(' ');
      buf.append("collarletPeriods").append('=').append(JodaBeanUtils.toString(collarletPeriods));
      buf.append('}');
      return buf.toString();
    }

  }

  //-------------------------- AUTOGENERATED END --------------------------
}
