package com.opengamma.strata.examples;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.StandardId;
import com.opengamma.strata.basics.currency.AdjustablePayment;
import com.opengamma.strata.basics.currency.CurrencyAmount;
import com.opengamma.strata.basics.date.*;
import com.opengamma.strata.basics.schedule.Frequency;
import com.opengamma.strata.basics.schedule.PeriodicSchedule;
import com.opengamma.strata.basics.schedule.StubConvention;
import com.opengamma.strata.basics.value.ValueSchedule;
import com.opengamma.strata.calc.CalculationRules;
import com.opengamma.strata.calc.CalculationRunner;
import com.opengamma.strata.calc.Column;
import com.opengamma.strata.calc.Results;
import com.opengamma.strata.calc.runner.CalculationFunctions;
import com.opengamma.strata.calc.runner.CalculationParameters;
import com.opengamma.strata.data.ImmutableMarketData;
import com.opengamma.strata.data.MarketData;
import com.opengamma.strata.examples.marketdata.ExampleData;
import com.opengamma.strata.examples.marketdata.ExampleMarketData;
import com.opengamma.strata.examples.marketdata.ExampleMarketDataBuilder;
import com.opengamma.strata.market.curve.ConstantCurve;
import com.opengamma.strata.market.surface.ConstantSurface;
import com.opengamma.strata.market.surface.Surface;
import com.opengamma.strata.market.surface.SurfaceMetadata;
import com.opengamma.strata.market.surface.Surfaces;
import com.opengamma.strata.measure.AdvancedMeasures;
import com.opengamma.strata.measure.Measures;
import com.opengamma.strata.measure.StandardComponents;
import com.opengamma.strata.measure.collar.IborCollarMarketDataLookup;
import com.opengamma.strata.pricer.collar.IborCollarletVolatilities;
import com.opengamma.strata.pricer.collar.IborCollarletVolatilitiesId;
import com.opengamma.strata.pricer.collar.IborCollarletVolatilitiesName;
import com.opengamma.strata.pricer.collar.NormalIborCollarletExpiryStrikeVolatilities;
import com.opengamma.strata.product.AttributeType;
import com.opengamma.strata.product.Trade;
import com.opengamma.strata.product.TradeInfo;
import com.opengamma.strata.product.collar.IborCollar;
import com.opengamma.strata.product.collar.IborCollarLeg;
import com.opengamma.strata.product.collar.IborCollarTrade;
import com.opengamma.strata.product.swap.*;
import com.opengamma.strata.report.ReportCalculationResults;
import com.opengamma.strata.report.trade.TradeReport;
import com.opengamma.strata.report.trade.TradeReportTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.opengamma.strata.basics.currency.Currency.*;
import static com.opengamma.strata.basics.date.BusinessDayConventions.MODIFIED_FOLLOWING;
import static com.opengamma.strata.basics.date.DayCounts.THIRTY_U_360;
import static com.opengamma.strata.basics.date.HolidayCalendarIds.EUTA;
import static com.opengamma.strata.basics.index.IborIndices.EUR_EURIBOR_3M;
import static com.opengamma.strata.basics.schedule.Frequency.P6M;
import static com.opengamma.strata.product.common.PayReceive.PAY;
import static com.opengamma.strata.product.common.PayReceive.RECEIVE;

public class InterestRateCollarPricingExample {
    public static void main (String[] args) {
        // setup calculation runner component, which needs life-cycle management
        // a typical application might use dependency injection to obtain the instance
        try (CalculationRunner runner = CalculationRunner.ofMultiThreaded()) {
            calculate(runner);
        }
    }

    private static void calculate (CalculationRunner runner) {
        List<Trade> trades = createsCollarletTrades ();

        // the columns, specifying the measures to be calculated
        List<Column> columns = ImmutableList.of(
//                Column.of(Measures.LEG_INITIAL_NOTIONAL)//,
                Column.of(Measures.PRESENT_VALUE),
                Column.of(Measures.LEG_PRESENT_VALUE));//,
//                Column.of(Measures.PV01_CALIBRATED_SUM),
//                Column.of(Measures.PAR_RATE),
//                Column.of(Measures.ACCRUED_INTEREST),
//                Column.of(Measures.PV01_CALIBRATED_BUCKETED),
//                Column.of(AdvancedMeasures.PV01_SEMI_PARALLEL_GAMMA_BUCKETED));

        // use the built-in example market data
        LocalDate valuationDate = LocalDate.of(2025, 1, 17);
        ExampleMarketDataBuilder marketDataBuilder = ExampleMarketData.builder();
        MarketData marketData = marketDataBuilder.buildSnapshot(valuationDate);

        // Create volatilities for the collar with a unique name to avoid conflicts
        IborCollarletVolatilitiesName volName = IborCollarletVolatilitiesName.of("CustomNormalVol");
        IborCollarletVolatilitiesId collarVolId = IborCollarletVolatilitiesId.of(volName);

        // Create a constant surface for the volatilities with a unique name
        SurfaceMetadata metadata = Surfaces.normalVolatilityByExpiryStrike("CustomNormalVol", DayCounts.ACT_365F);
        Surface surface = ConstantSurface.of(metadata, 0.01); // 1% normal volatility

        // Create the volatilities
        ZonedDateTime valuationDateTime = valuationDate.atStartOfDay(ZoneId.systemDefault());
        NormalIborCollarletExpiryStrikeVolatilities volatilities = NormalIborCollarletExpiryStrikeVolatilities.of(
            EUR_EURIBOR_3M, valuationDateTime, surface);

        // Create a completely new market data with our volatilities
        MarketData enhancedMarketData = ImmutableMarketData.builder(valuationDate)
            .addValueUnsafe(collarVolId, volatilities)
            .add(marketData)
            .build();

        // the complete set of rules for calculating measures
        CalculationFunctions functions = StandardComponents.calculationFunctions();

        // create a collar market data lookup for the EUR_EURIBOR_3M index
        IborCollarMarketDataLookup collarLookup = IborCollarMarketDataLookup.of(EUR_EURIBOR_3M, collarVolId);

        // Debug output
        System.out.println("Volatilities ID: " + collarVolId);
        System.out.println("Volatilities ID class: " + collarVolId.getClass().getName());
        System.out.println("Collar lookup index: " + EUR_EURIBOR_3M);
        System.out.println("Collar lookup volatility ID: " + collarLookup.getVolatilityIds(EUR_EURIBOR_3M));

        // Debug output
        System.out.println("Rates lookup: " + marketDataBuilder.ratesLookup(valuationDate));
        System.out.println("Collar lookup: " + collarLookup);

        // Create calculation rules with both lookups using varargs
        CalculationRules rules = CalculationRules.of(
            functions,
            marketDataBuilder.ratesLookup(valuationDate),
            collarLookup);

        // the reference data, such as holidays and securities
        ReferenceData refData = ReferenceData.standard();

        // calculate the results using the enhanced market data that includes volatilities
        Results results = runner.calculate(rules, trades, columns, enhancedMarketData, refData);

        // use the report runner to transform the engine results into a trade report
        ReportCalculationResults calculationResults =
                ReportCalculationResults.of(valuationDate, trades, columns, results, functions, refData);

        TradeReportTemplate reportTemplate = ExampleData.loadTradeReportTemplate("collared-swap-report-template");
        TradeReport tradeReport = TradeReport.of(calculationResults, reportTemplate);
        tradeReport.writeAsciiTable(System.out);
    }

    private static List<Trade> createsCollarletTrades () {
       return ImmutableList.of (
        createBasicFixedVsLibor3mCollar()
       );
    }

    private static final LocalDate START = LocalDate.of(2025, 9, 17);
    private static final LocalDate END = LocalDate.of(2029, 9, 17);
    private static final IborRateCalculation RATE_CALCULATION = IborRateCalculation.of(EUR_EURIBOR_3M);
    private static final Frequency FREQUENCY = Frequency.P3M;
    private static final BusinessDayAdjustment BUSS_ADJ =
            BusinessDayAdjustment.of(BusinessDayConventions.FOLLOWING, EUTA);
    private static final PeriodicSchedule SCHEDULE = PeriodicSchedule.builder()
            .startDate(START)
            .endDate(END)
            .frequency(FREQUENCY)
            .businessDayAdjustment(BUSS_ADJ)
            .build();
    private static final DaysAdjustment PAYMENT_OFFSET = DaysAdjustment.ofBusinessDays(2, EUTA);
    // SWAP COPIED BITS
    private static final NotionalSchedule UNIT_NOTIONAL = NotionalSchedule.of(GBP, 1d);
    private static final HolidayCalendarId CALENDAR = HolidayCalendarIds.SAT_SUN;
    private static final BusinessDayAdjustment BDA_MF = BusinessDayAdjustment.of(MODIFIED_FOLLOWING, CALENDAR);

    private static final double RATE = 0.0175;

    private static final SwapLeg FIXED_LEG =
            RateCalculationSwapLeg
                .builder()
                .payReceive(RECEIVE)
                .accrualSchedule(PeriodicSchedule.builder()
                    .startDate(START)
                    .endDate(END)
                    .frequency(P6M)
                    .businessDayAdjustment(BDA_MF)
                    .stubConvention(StubConvention.SHORT_FINAL)
                    .build())
                .paymentSchedule(PaymentSchedule.builder()
                    .paymentFrequency(P6M)
                    .paymentDateOffset(DaysAdjustment.NONE)
                    .build())
                .notionalSchedule(UNIT_NOTIONAL)
                .calculation(FixedRateCalculation.builder()
                    .dayCount(THIRTY_U_360)
                    .rate(ValueSchedule.of(RATE))
                    .build())
                .build();
    // END SWAP BITS
    private static final ValueSchedule COLLAR = ValueSchedule.of(0.0325);
    private static final double NOTIONAL_VALUE = 1.0e6;
    private static final ValueSchedule NOTIONAL = ValueSchedule.of(NOTIONAL_VALUE);
    private static final IborCollarLeg COLLAR_LEG =
            IborCollarLeg.builder()
                .calculation(RATE_CALCULATION)
                .collarSchedule(COLLAR)
                .notional(NOTIONAL)
                .paymentDateOffset(PAYMENT_OFFSET)
                .paymentSchedule(SCHEDULE)
                .payReceive(PAY)
                .build();

    private static final IborCollar PRODUCT = IborCollar.of(COLLAR_LEG, FIXED_LEG);
    private static final AdjustablePayment PREMIUM =
            AdjustablePayment.of(CurrencyAmount.of(GBP, NOTIONAL_VALUE), LocalDate.of(2025, 9, 17));

    private static Trade createBasicFixedVsLibor3mCollar () {
        TradeInfo tradeInfo =
                TradeInfo.builder()
                        .id(StandardId.of("example", "1"))
                        .addAttribute(AttributeType.DESCRIPTION, "Fixed vs Libor 3m collar")
                        .counterparty(StandardId.of("example", "A"))
                        .settlementDate(LocalDate.of(2029, 9, 17))
                        .build();

        return IborCollarTrade
                    .builder()
                    .info(tradeInfo)
                    .product(PRODUCT)
                    .premium(PREMIUM)
                    .build();
    }


}
