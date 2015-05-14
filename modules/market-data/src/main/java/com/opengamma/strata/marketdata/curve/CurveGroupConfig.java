/**
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.marketdata.curve;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ImmutableList;

/**
 * Configuration for calibrating a group of related curves.
 * <p>
 * A curve group contains one or more entries, each of which contains configuration for a curve and a
 * set of market data keys specifying how the curve is used in the group.
 * <p>
 * A curve can be used for multiple purposes and therefore the curve itself contains no information about how
 * it is used.
 * <p>
 * In the simple case a curve is only used for a single purpose. For example, if a curve is used for discounting
 * it will be associated with one key of type {@code DiscountingCurveKey}.
 * <p>
 * A single curve can also be used as both a discounting curve and a forward curve.
 * In that case its key set would contain a {@code DiscountingCurveKey} and a {@code RateIndexCurveKey}.
 * <p>
 * Every curve must be associated with at least once key.
 */
@BeanDefinition(builderScope = "private")
public final class CurveGroupConfig implements ImmutableBean {

  /** The name of the curve group. */
  @PropertyDefinition(validate = "notNull")
  private final String name;

  /** The configuration for building the curves in the group. */
  @PropertyDefinition(validate = "notNull")
  private final ImmutableList<CurveGroupEntry> entries;

  /**
   * Returns a mutable builder for building the configuration for a curve group.
   *
   * @return a mutable builder for building the configuration for a curve group
   */
  public static CurveGroupConfigBuilder builder() {
    return new CurveGroupConfigBuilder();
  }

  /**
   * Package-private constructor used by the builder.
   *
   * @param name  the name of the curve group
   * @param entries  details of the curves in the group
   */
  @ImmutableConstructor
  CurveGroupConfig(String name, List<CurveGroupEntry> entries) {
    JodaBeanUtils.notNull(name, "name");
    JodaBeanUtils.notNull(entries, "entries");
    this.name = name;
    this.entries = ImmutableList.copyOf(entries);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code CurveGroupConfig}.
   * @return the meta-bean, not null
   */
  public static CurveGroupConfig.Meta meta() {
    return CurveGroupConfig.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(CurveGroupConfig.Meta.INSTANCE);
  }

  @Override
  public CurveGroupConfig.Meta metaBean() {
    return CurveGroupConfig.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the name of the curve group.
   * @return the value of the property, not null
   */
  public String getName() {
    return name;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the configuration for building the curves in the group.
   * @return the value of the property, not null
   */
  public ImmutableList<CurveGroupEntry> getEntries() {
    return entries;
  }

  //-----------------------------------------------------------------------
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      CurveGroupConfig other = (CurveGroupConfig) obj;
      return JodaBeanUtils.equal(getName(), other.getName()) &&
          JodaBeanUtils.equal(getEntries(), other.getEntries());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash = hash * 31 + JodaBeanUtils.hashCode(getName());
    hash = hash * 31 + JodaBeanUtils.hashCode(getEntries());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("CurveGroupConfig{");
    buf.append("name").append('=').append(getName()).append(',').append(' ');
    buf.append("entries").append('=').append(JodaBeanUtils.toString(getEntries()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code CurveGroupConfig}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code name} property.
     */
    private final MetaProperty<String> name = DirectMetaProperty.ofImmutable(
        this, "name", CurveGroupConfig.class, String.class);
    /**
     * The meta-property for the {@code entries} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableList<CurveGroupEntry>> entries = DirectMetaProperty.ofImmutable(
        this, "entries", CurveGroupConfig.class, (Class) ImmutableList.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "name",
        "entries");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case -1591573360:  // entries
          return entries;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public BeanBuilder<? extends CurveGroupConfig> builder() {
      return new CurveGroupConfig.Builder();
    }

    @Override
    public Class<? extends CurveGroupConfig> beanType() {
      return CurveGroupConfig.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code name} property.
     * @return the meta-property, not null
     */
    public MetaProperty<String> name() {
      return name;
    }

    /**
     * The meta-property for the {@code entries} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableList<CurveGroupEntry>> entries() {
      return entries;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return ((CurveGroupConfig) bean).getName();
        case -1591573360:  // entries
          return ((CurveGroupConfig) bean).getEntries();
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
   * The bean-builder for {@code CurveGroupConfig}.
   */
  private static final class Builder extends DirectFieldsBeanBuilder<CurveGroupConfig> {

    private String name;
    private List<CurveGroupEntry> entries = ImmutableList.of();

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    //-----------------------------------------------------------------------
    @Override
    public Object get(String propertyName) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          return name;
        case -1591573360:  // entries
          return entries;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 3373707:  // name
          this.name = (String) newValue;
          break;
        case -1591573360:  // entries
          this.entries = (List<CurveGroupEntry>) newValue;
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
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.setString(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public CurveGroupConfig build() {
      return new CurveGroupConfig(
          name,
          entries);
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("CurveGroupConfig.Builder{");
      buf.append("name").append('=').append(JodaBeanUtils.toString(name)).append(',').append(' ');
      buf.append("entries").append('=').append(JodaBeanUtils.toString(entries));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
