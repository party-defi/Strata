package com.opengamma.strata.examples;

import com.google.common.collect.ImmutableList;
import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.StandardId;
import com.opengamma.strata.calc.CalculationRules;
import com.opengamma.strata.calc.CalculationRunner;
import com.opengamma.strata.calc.Column;
import com.opengamma.strata.calc.Results;
import com.opengamma.strata.calc.runner.CalculationFunctions;
import com.opengamma.strata.data.MarketData;
import com.opengamma.strata.examples.marketdata.ExampleData;
import com.opengamma.strata.examples.marketdata.ExampleMarketData;
import com.opengamma.strata.examples.marketdata.ExampleMarketDataBuilder;
import com.opengamma.strata.measure.AdvancedMeasures;
import com.opengamma.strata.measure.Measures;
import com.opengamma.strata.measure.StandardComponents;
import com.opengamma.strata.product.AttributeType;
import com.opengamma.strata.product.Trade;
import com.opengamma.strata.product.TradeInfo;
import com.opengamma.strata.product.common.BuySell;
import com.opengamma.strata.product.swap.type.FixedIborSwapConventions;
import com.opengamma.strata.report.ReportCalculationResults;
import com.opengamma.strata.report.trade.TradeReport;
import com.opengamma.strata.report.trade.TradeReportTemplate;

import java.time.LocalDate;
import java.util.List;

public class InterestRateCollarPricingExample {
    public static void main (String[] args) {
        // setup calculation runner component, which needs life-cycle management
        // a typical application might use dependency injection to obtain the instance
        try (CalculationRunner runner = CalculationRunner.ofMultiThreaded()) {
            calculate(runner);
        }
    }

    private static void calculate (CalculationRunner runner) {
        List<Trade> trades = createCapletFloorletTrades ();

        // the columns, specifying the measures to be calculated
        List<Column> columns = ImmutableList.of(
                Column.of(Measures.LEG_INITIAL_NOTIONAL),
                Column.of(Measures.PRESENT_VALUE),
                Column.of(Measures.LEG_PRESENT_VALUE),
                Column.of(Measures.PV01_CALIBRATED_SUM),
                Column.of(Measures.PAR_RATE),
                Column.of(Measures.ACCRUED_INTEREST),
                Column.of(Measures.PV01_CALIBRATED_BUCKETED),
                Column.of(AdvancedMeasures.PV01_SEMI_PARALLEL_GAMMA_BUCKETED));

        // use the built-in example market data
        LocalDate valuationDate = LocalDate.of(2014, 1, 22);
        ExampleMarketDataBuilder marketDataBuilder = ExampleMarketData.builder();
        MarketData marketData = marketDataBuilder.buildSnapshot(valuationDate);

        // the complete set of rules for calculating measures
        CalculationFunctions functions = StandardComponents.calculationFunctions();
        CalculationRules rules = CalculationRules.of(functions, marketDataBuilder.ratesLookup(valuationDate));

        // the reference data, such as holidays and securities
        ReferenceData refData = ReferenceData.standard();

        // calculate the results
        Results results = runner.calculate(rules, trades, columns, marketData, refData);

        // use the report runner to transform the engine results into a trade report
        ReportCalculationResults calculationResults =
                ReportCalculationResults.of(valuationDate, trades, columns, results, functions, refData);

        TradeReportTemplate reportTemplate = ExampleData.loadTradeReportTemplate("swap-report-template");
        TradeReport tradeReport = TradeReport.of(calculationResults, reportTemplate);
        tradeReport.writeAsciiTable(System.out);
    }

    private static List<Trade> createCapletFloorletTrades () {
       return ImmutableList.of (
        createBasicFixedVsLibor3mCollar()
       );
    }

    private static Trade createBasicFixedVsLibor3mCollar () {
        TradeInfo tradeInfo = TradeInfo.builder()
                .id(StandardId.of("example", "1"))
                .addAttribute(AttributeType.DESCRIPTION, "Fixed vs Libor 3m")
                .counterparty(StandardId.of("example", "A"))
                .settlementDate(LocalDate.of(2027, 9, 12))
                .build();
        return FixedIborSwapConventions.USD_FIXED_6M_LIBOR_3M.toTrade(
                tradeInfo,
                LocalDate.of(2025, 9, 12), // the start date
                LocalDate.of(2028, 9, 12), // the end date
                BuySell.BUY,               // indicates wheter this trade is a buy or sell
                100_000_000,               // the notional amount
                0.015);                    // the fixed interest rate
    }


}
