/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product.collar;

import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.date.AdjustableDate;
import com.opengamma.strata.basics.date.BusinessDayAdjustment;
import com.opengamma.strata.basics.date.BusinessDayConventions;
import com.opengamma.strata.basics.date.DaysAdjustment;
import com.opengamma.strata.basics.schedule.Frequency;
import com.opengamma.strata.basics.schedule.PeriodicSchedule;
import com.opengamma.strata.basics.schedule.StubConvention;
import com.opengamma.strata.basics.value.ValueAdjustment;
import com.opengamma.strata.basics.value.ValueSchedule;
import com.opengamma.strata.basics.value.ValueStep;
import com.opengamma.strata.product.rate.IborRateComputation;
import com.opengamma.strata.product.swap.FixingRelativeTo;
import com.opengamma.strata.product.swap.IborRateCalculation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.opengamma.strata.basics.currency.Currency.EUR;
import static com.opengamma.strata.basics.currency.Currency.GBP;
import static com.opengamma.strata.basics.date.HolidayCalendarIds.EUTA;
import static com.opengamma.strata.basics.index.IborIndices.EUR_EURIBOR_3M;
import static com.opengamma.strata.basics.index.IborIndices.GBP_LIBOR_6M;
import static com.opengamma.strata.collect.TestHelper.*;
import static com.opengamma.strata.product.common.PayReceive.PAY;
import static com.opengamma.strata.product.common.PayReceive.RECEIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * Test {@link IborCollarLeg}.
 */
public class IborCollarLegTest {

  private static final ReferenceData REF_DATA = ReferenceData.standard();
  private static final LocalDate START = LocalDate.of(2011, 3, 17);
  private static final LocalDate END = LocalDate.of(2012, 3, 17);
  private static final IborRateCalculation RATE_CALCULATION = IborRateCalculation.of(EUR_EURIBOR_3M);
  private static final Frequency FREQUENCY = Frequency.P3M;
  private static final BusinessDayAdjustment BUSS_ADJ =
      BusinessDayAdjustment.of(BusinessDayConventions.FOLLOWING, EUTA);
  private static final PeriodicSchedule SCHEDULE = PeriodicSchedule.builder()
      .startDate(START)
      .endDate(END)
      .frequency(FREQUENCY)
      .businessDayAdjustment(BUSS_ADJ)
      .stubConvention(StubConvention.NONE)
      .build();
  private static final DaysAdjustment PAYMENT_OFFSET = DaysAdjustment.ofBusinessDays(2, EUTA);

  private static final double[] NOTIONALS = new double[] {1.0e6, 1.2e6, 0.8e6, 1.0e6};
  private static final double[] STRIKES = new double[] {0.03, 0.0275, 0.02, 0.0345};
  private static final ValueSchedule COLLAR = ValueSchedule.of(0.0325);
  private static final List<ValueStep> FLOOR_STEPS = new ArrayList<ValueStep>();
  private static final List<ValueStep> NOTIONAL_STEPS = new ArrayList<ValueStep>();
  static {
    FLOOR_STEPS.add(ValueStep.of(1, ValueAdjustment.ofReplace(STRIKES[1])));
    FLOOR_STEPS.add(ValueStep.of(2, ValueAdjustment.ofReplace(STRIKES[2])));
    FLOOR_STEPS.add(ValueStep.of(3, ValueAdjustment.ofReplace(STRIKES[3])));
    NOTIONAL_STEPS.add(ValueStep.of(1, ValueAdjustment.ofReplace(NOTIONALS[1])));
    NOTIONAL_STEPS.add(ValueStep.of(2, ValueAdjustment.ofReplace(NOTIONALS[2])));
    NOTIONAL_STEPS.add(ValueStep.of(3, ValueAdjustment.ofReplace(NOTIONALS[3])));
  }
  private static final ValueSchedule NOTIONAL = ValueSchedule.of(NOTIONALS[0], NOTIONAL_STEPS);

  @Test
  public void test_builder_full() {
    IborCollarLeg test = IborCollarLeg.builder()
        .calculation(RATE_CALCULATION)
        .collarSchedule(COLLAR)
        .currency(GBP)
        .notional(NOTIONAL)
        .paymentDateOffset(PAYMENT_OFFSET)
        .paymentSchedule(SCHEDULE)
        .payReceive(PAY)
        .build();
    assertThat(test.getCalculation()).isEqualTo(RATE_CALCULATION);
    assertThat(test.getCollarSchedule().get()).isEqualTo(COLLAR);
    assertThat(test.getCurrency()).isEqualTo(GBP);
    assertThat(test.getNotional()).isEqualTo(NOTIONAL);
    assertThat(test.getPaymentDateOffset()).isEqualTo(PAYMENT_OFFSET);
    assertThat(test.getPaymentSchedule()).isEqualTo(SCHEDULE);
    assertThat(test.getPayReceive()).isEqualTo(PAY);
    assertThat(test.getStartDate()).isEqualTo(AdjustableDate.of(START, BUSS_ADJ));
    assertThat(test.getEndDate()).isEqualTo(AdjustableDate.of(END, BUSS_ADJ));
    assertThat(test.getIndex()).isEqualTo(EUR_EURIBOR_3M);
  }

  @Test
  public void test_builder_min() {
    IborCollarLeg test = IborCollarLeg.builder()
        .calculation(RATE_CALCULATION)
        .notional(NOTIONAL)
        .paymentSchedule(SCHEDULE)
        .payReceive(RECEIVE)
        .build();
    assertThat(test.getCalculation()).isEqualTo(RATE_CALCULATION);
    assertThat(test.getCollarSchedule()).isNotPresent();
    assertThat(test.getCurrency()).isEqualTo(EUR);
    assertThat(test.getNotional()).isEqualTo(NOTIONAL);
    assertThat(test.getPaymentDateOffset()).isEqualTo(DaysAdjustment.NONE);
    assertThat(test.getPaymentSchedule()).isEqualTo(SCHEDULE);
    assertThat(test.getPayReceive()).isEqualTo(RECEIVE);
    assertThat(test.getStartDate()).isEqualTo(AdjustableDate.of(START, BUSS_ADJ));
    assertThat(test.getEndDate()).isEqualTo(AdjustableDate.of(END, BUSS_ADJ));
  }

  @Test
  public void test_builder_fail() {
    // collar missing
    assertThatIllegalArgumentException()
        .isThrownBy(() -> IborCollarLeg.builder()
            .calculation(RATE_CALCULATION)
            .notional(NOTIONAL)
            .paymentSchedule(SCHEDULE)
            .payReceive(RECEIVE)
            .build());

    // stub type
    assertThatIllegalArgumentException()
        .isThrownBy(() -> IborCollarLeg.builder()
            .calculation(RATE_CALCULATION)
            .collarSchedule(COLLAR)
            .currency(GBP)
            .notional(NOTIONAL)
            .paymentDateOffset(PAYMENT_OFFSET)
            .paymentSchedule(PeriodicSchedule.builder()
                .startDate(START)
                .endDate(END)
                .frequency(FREQUENCY)
                .businessDayAdjustment(BUSS_ADJ)
                .stubConvention(StubConvention.SHORT_FINAL)
                .build())
            .payReceive(PAY)
            .build());
  }

  @Test
  public void test_resolve_collar() {
    IborRateCalculation rateCalc = IborRateCalculation.builder()
        .index(EUR_EURIBOR_3M)
        .fixingRelativeTo(FixingRelativeTo.PERIOD_END)
        .fixingDateOffset(EUR_EURIBOR_3M.getFixingDateOffset())
        .build();
    IborCollarLeg base = IborCollarLeg.builder()
        .calculation(rateCalc)
        .collarSchedule(COLLAR)
        .notional(NOTIONAL)
        .paymentDateOffset(PAYMENT_OFFSET)
        .paymentSchedule(SCHEDULE)
        .payReceive(RECEIVE)
        .build();
    LocalDate[] unadjustedDates =
        new LocalDate[] {START, START.plusMonths(3), START.plusMonths(6), START.plusMonths(9), START.plusMonths(12)};
    IborCollarletPeriod[] periods = new IborCollarletPeriod[4];
    for (int i = 0; i < 4; ++i) {
      LocalDate start = BUSS_ADJ.adjust(unadjustedDates[i], REF_DATA);
      LocalDate end = BUSS_ADJ.adjust(unadjustedDates[i + 1], REF_DATA);
      double yearFraction = EUR_EURIBOR_3M.getDayCount().relativeYearFraction(start, end);
      periods[i] = IborCollarletPeriod.builder()
          .collarlet(COLLAR.getInitialValue())
          .currency(EUR)
          .startDate(start)
          .endDate(end)
          .unadjustedStartDate(unadjustedDates[i])
          .unadjustedEndDate(unadjustedDates[i + 1])
          .paymentDate(PAYMENT_OFFSET.adjust(end, REF_DATA))
          .notional(NOTIONALS[i])
          .iborRate(IborRateComputation.of(EUR_EURIBOR_3M, rateCalc.getFixingDateOffset().adjust(end, REF_DATA), REF_DATA))
          .yearFraction(yearFraction)
          .build();
    }
    ResolvedIborCollarLeg expected = ResolvedIborCollarLeg.builder()
        .payReceive(RECEIVE)
        .build();
    ResolvedIborCollarLeg computed = base.resolve(REF_DATA);
    assertThat(computed).isEqualTo(expected);
  }

  @Test
  public void test_resolve_floor() {
    IborCollarLeg base = IborCollarLeg.builder()
        .calculation(RATE_CALCULATION)
        .currency(GBP)
        .notional(NOTIONAL)
        .paymentDateOffset(PAYMENT_OFFSET)
        .paymentSchedule(SCHEDULE)
        .payReceive(PAY)
        .build();
    LocalDate[] unadjustedDates =
        new LocalDate[] {START, START.plusMonths(3), START.plusMonths(6), START.plusMonths(9), START.plusMonths(12)};
    IborCollarletPeriod[] periods = new IborCollarletPeriod[4];
    for (int i = 0; i < 4; ++i) {
      LocalDate start = BUSS_ADJ.adjust(unadjustedDates[i], REF_DATA);
      LocalDate end = BUSS_ADJ.adjust(unadjustedDates[i + 1], REF_DATA);
      double yearFraction = EUR_EURIBOR_3M.getDayCount().relativeYearFraction(start, end);
      LocalDate fixingDate = RATE_CALCULATION.getFixingDateOffset().adjust(start, REF_DATA);
      periods[i] = IborCollarletPeriod.builder()
          .floorlet(STRIKES[i])
          .currency(GBP)
          .startDate(start)
          .endDate(end)
          .unadjustedStartDate(unadjustedDates[i])
          .unadjustedEndDate(unadjustedDates[i + 1])
          .paymentDate(PAYMENT_OFFSET.adjust(end, REF_DATA))
          .notional(-NOTIONALS[i])
          .iborRate(IborRateComputation.of(EUR_EURIBOR_3M, fixingDate, REF_DATA))
          .yearFraction(yearFraction)
          .build();
    }
    ResolvedIborCollarLeg expected = ResolvedIborCollarLeg.builder()
        .collarletPeriods(periods)
        .payReceive(PAY)
        .build();
    ResolvedIborCollarLeg computed = base.resolve(REF_DATA);
    assertThat(computed).isEqualTo(expected);
  }

  //-------------------------------------------------------------------------
  @Test
  public void coverage() {
    IborCollarLeg test1 = IborCollarLeg.builder()
        .calculation(RATE_CALCULATION)
        .notional(NOTIONAL)
        .paymentSchedule(SCHEDULE)
        .payReceive(RECEIVE)
        .build();
    coverImmutableBean(test1);
    IborCollarLeg test2 = IborCollarLeg.builder()
        .calculation(IborRateCalculation.of(GBP_LIBOR_6M))
        .collarSchedule(COLLAR)
        .notional(ValueSchedule.of(1000))
        .paymentDateOffset(PAYMENT_OFFSET)
        .paymentSchedule(PeriodicSchedule.builder()
            .startDate(START)
            .endDate(END)
            .frequency(Frequency.P6M)
            .businessDayAdjustment(BUSS_ADJ)
            .build())
        .payReceive(PAY)
        .build();
    coverBeanEquals(test1, test2);
  }

  @Test
  public void test_serialization() {
    IborCollarLeg test = IborCollarLeg.builder()
        .calculation(RATE_CALCULATION)
        .notional(NOTIONAL)
        .paymentSchedule(SCHEDULE)
        .payReceive(RECEIVE)
        .build();
    assertSerialization(test);
  }

}
