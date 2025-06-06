package com.opengamma.strata.loader.json;

import com.google.common.io.ByteSource;
import com.opengamma.strata.collect.io.ResourceLocator;
import com.opengamma.strata.loader.fpml.FpmlDocumentParser;
import com.opengamma.strata.loader.fpml.FpmlPartySelector;
import com.opengamma.strata.product.Trade;
import com.opengamma.strata.product.TradeInfo;
import com.opengamma.strata.product.swap.Swap;
import com.opengamma.strata.product.swaption.Swaption;
import com.opengamma.strata.product.swaption.SwaptionExercise;
import com.opengamma.strata.product.swaption.SwaptionTrade;
import org.joda.beans.Bean;
import org.joda.beans.ser.JodaBeanSer;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;
import static org.assertj.core.api.Assertions.assertThat;

public class JsonParserTest {

    @Test
    public void serialise_swaption_bermuda() {
        // Use JodaBeanSer for serialization/deserialization
        JodaBeanSer serializer = JodaBeanSer.COMPACT;

        // Get the resource
        String fpmlLocation = "classpath:com/opengamma/strata/loader/fpml/ird-ex14-berm-swaption.xml";
        ByteSource resource = ResourceLocator.of(fpmlLocation).getByteSource();
        FpmlDocumentParser parser = FpmlDocumentParser.of(FpmlPartySelector.matching("Party1"));

        // Parse the trades
        assertThat(parser.isKnownFormat(resource)).isTrue();
        List<Trade> trades = parser.parseTrades(resource);
        SwaptionTrade trade = (SwaptionTrade) trades.get(0);

        List<String> tradeJsonList = new ArrayList<>();
        try {
            out.println("Number of trades: " + trades.size());

            // Extract trade components
            TradeInfo info = trade.getInfo();
            Swaption swaption = (Swaption) trade.getProduct();
            SwaptionExercise exercise = swaption.getExerciseInfo().get();

            // Serialize to JSON using JodaBeanSer
            String exerciseJson = serializer.jsonWriter().write(exercise);
            String swaptionJson = serializer.jsonWriter().write(swaption);
            String infoJson = serializer.jsonWriter().write(info);
            String tradeJson = serializer.jsonWriter().write(trade);

            // Store JSON representations
            tradeJsonList.add(infoJson);
            tradeJsonList.add(swaptionJson);
            tradeJsonList.add(tradeJson);

            // Deserialize from JSON
            SwaptionExercise exerciseBean = (SwaptionExercise) serializer.jsonReader().read(exerciseJson, SwaptionExercise.class);
            Swaption swaptionBean = (Swaption) serializer.jsonReader().read(swaptionJson, Swaption.class);

            // Output for debugging
            out.println("Exercise: " + exerciseJson);
            out.println("Swaption: " + swaptionJson);

            ZonedDateTime expiryDT = swaption.getExpiry();
            out.println("Expiry: " + expiryDT);

            // Serialize underlying swap if needed
            Swap underlying = swaption.getUnderlying();
            String underlyingJson = serializer.jsonWriter().write(underlying);

            // Deserialize the swap
            Swap underlyingBean = (Swap) serializer.jsonReader().read(underlyingJson, Swap.class);

            out.println("Underlying: " + underlyingJson);
            out.println("Trade JSON: " + tradeJson);

            // Deserialize complete trade
            Bean recoveredBean = serializer.jsonReader().read(tradeJson, SwaptionTrade.class);
            Trade recoveredTrade = (Trade) recoveredBean;

            // Verify recovered trade matches original
            out.println("Recovered Trade: " + recoveredTrade);
            assertThat(recoveredTrade).isEqualTo(trade);

        } catch (Exception e) {
            out.println(e.getMessage());
            assertThat(false).isTrue(); // Fail the test
        }
    }

}