/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.pricer.collar;

import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.date.BusinessDayAdjustment;
import com.opengamma.strata.basics.date.BusinessDayConventions;
import com.opengamma.strata.basics.date.DaysAdjustment;
import com.opengamma.strata.basics.index.IborIndex;
import com.opengamma.strata.basics.schedule.Frequency;
import com.opengamma.strata.basics.schedule.PeriodicSchedule;
import com.opengamma.strata.basics.schedule.RollConventions;
import com.opengamma.strata.basics.schedule.StubConvention;
import com.opengamma.strata.basics.value.ValueSchedule;
import com.opengamma.strata.pricer.collar.IborCollarletDataSet;
import com.opengamma.strata.product.collar.IborCollarLeg;
import com.opengamma.strata.product.collar.ResolvedIborCollarLeg;
import com.opengamma.strata.product.common.PayReceive;
import com.opengamma.strata.product.common.PutCall;
import com.opengamma.strata.product.swap.*;

import java.time.LocalDate;

import static com.opengamma.strata.basics.currency.Currency.EUR;
import static com.opengamma.strata.basics.date.DayCounts.ACT_360;
import static com.opengamma.strata.basics.date.HolidayCalendarIds.EUTA;

/**
 * Data set of Ibor collar/floor securities.
 */
public class IborCollarDataSet {

  private static final ReferenceData REF_DATA = ReferenceData.standard();
  private static final BusinessDayAdjustment BUSINESS_ADJ = BusinessDayAdjustment.of(
      BusinessDayConventions.MODIFIED_FOLLOWING, EUTA);

  //-------------------------------------------------------------------------
  /**
   * Creates an Ibor collar/floor leg.
   * <p>
   * The Ibor index should be {@code EUR_EURIBOR_3M} or {@code EUR_EURIBOR_6M} to match the availability of the curve 
   * data in {@link IborCollarletDataSet}.
   * 
   * @param index  the index
   * @param startDate  the start date
   * @param endDate  the end date
   * @param strikeSchedule  the strike
   * @param notionalSchedule  the notional
   * @param payRec  pay or receive
   * @return the instance
   */
  public static ResolvedIborCollarLeg createCollarLeg(
      IborIndex index,
      LocalDate startDate,
      LocalDate endDate,
      ValueSchedule strikeSchedule,
      ValueSchedule notionalSchedule,
      PayReceive payRec) {

    IborCollarLeg leg =
        createCollarLegUnresolved(index, startDate, endDate, strikeSchedule, notionalSchedule, payRec);
    return leg.resolve(REF_DATA);
  }

  /**
   * Creates an Ibor collar/floor leg.
   * <p>
   * The Ibor index should be {@code EUR_EURIBOR_3M} or {@code EUR_EURIBOR_6M} to match the availability of the curve 
   * data in {@link IborCollarletDataSet}.
   * 
   * @param index  the index
   * @param startDate  the start date
   * @param endDate  the end date
   * @param strikeSchedule  the strike
   * @param notionalSchedule  the notional
   * @param payRec  pay or receive
   * @return the instance
   */
  public static IborCollarLeg createCollarLegUnresolved(
      IborIndex index,
      LocalDate startDate,
      LocalDate endDate,
      ValueSchedule strikeSchedule,
      ValueSchedule notionalSchedule,
      PayReceive payRec) {

    Frequency frequency = Frequency.of(index.getTenor().getPeriod());
    PeriodicSchedule paySchedule =
        PeriodicSchedule.of(startDate, endDate, frequency, BUSINESS_ADJ, StubConvention.NONE, RollConventions.NONE);
    IborRateCalculation rateCalculation = IborRateCalculation.of(index);
      return IborCollarLeg.builder()
          .calculation(rateCalculation)
          .collarSchedule(strikeSchedule)
          .notional(notionalSchedule)
          .paymentSchedule(paySchedule)
          .payReceive(payRec)
          .build();
  }

  //-------------------------------------------------------------------------
  /**
   * Create a pay leg.
   * <p>
   * The pay leg created is periodic fixed rate payments without compounding.
   * The Ibor index is used to specify the payment frequency.
   * 
   * @param index  the Ibor index
   * @param startDate  the start date
   * @param endDate  the end date
   * @param fixedRate  the fixed rate
   * @param notional  the notional
   * @param payRec  pay or receive 
   * @return the instance
   */
  public static ResolvedSwapLeg createFixedPayLeg(
      IborIndex index,
      LocalDate startDate,
      LocalDate endDate,
      double fixedRate,
      double notional,
      PayReceive payRec) {

    SwapLeg leg = createFixedPayLegUnresolved(index, startDate, endDate, fixedRate, notional, payRec);
    return leg.resolve(REF_DATA);
  }

  /**
   * Create a pay leg.
   * <p>
   * The pay leg created is periodic fixed rate payments without compounding.
   * The Ibor index is used to specify the payment frequency.
   * 
   * @param index  the Ibor index
   * @param startDate  the start date
   * @param endDate  the end date
   * @param fixedRate  the fixed rate
   * @param notional  the notional
   * @param payRec  pay or receive 
   * @return the instance
   */
  public static SwapLeg createFixedPayLegUnresolved(
      IborIndex index,
      LocalDate startDate,
      LocalDate endDate,
      double fixedRate,
      double notional,
      PayReceive payRec) {

    Frequency frequency = Frequency.of(index.getTenor().getPeriod());
    PeriodicSchedule accSchedule =
        PeriodicSchedule.of(startDate, endDate, frequency, BUSINESS_ADJ, StubConvention.NONE, RollConventions.NONE);
    return RateCalculationSwapLeg.builder()
        .payReceive(payRec)
        .accrualSchedule(accSchedule)
        .calculation(
            FixedRateCalculation.of(fixedRate, ACT_360))
        .paymentSchedule(
            PaymentSchedule.builder().paymentFrequency(frequency).paymentDateOffset(DaysAdjustment.NONE).build())
        .notionalSchedule(
            NotionalSchedule.of(CurrencyAmount.of(EUR, notional)))
        .build();
  }

}
