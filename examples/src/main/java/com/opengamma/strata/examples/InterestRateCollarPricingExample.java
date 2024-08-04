package com.opengamma.strata.examples;

import com.google.common.collect.ImmutableList;
import com.opengamma.strata.basics.StandardId;
import com.opengamma.strata.calc.CalculationRunner;
import com.opengamma.strata.product.AttributeType;
import com.opengamma.strata.product.Trade;
import com.opengamma.strata.product.TradeInfo;
import com.opengamma.strata.product.common.BuySell;
import com.opengamma.strata.product.swap.type.FixedIborSwapConventions;

import java.time.LocalDate;
import java.util.List;

public class InterestRateCollarPricingExample {
    public static void main (String[] args) {

    }

    private static void calculate (CalculationRunner runner) {

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
                LocalDate.of(2027, 9, 12), // the start date
                LocalDate.of(2024, 9, 12), // the end date
                BuySell.BUY,               // indicates wheter this trade is a buy or sell
                100_000_000,               // the notional amount
                0.015);                    // the fixed interest rate
    }


}
