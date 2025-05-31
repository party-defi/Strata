//package com.opengamma.strata.loader.json;
//
////import com.fasterxml.jackson.databind.json.JsonMapper;
////import com.fasterxml.jackson.databind.module.SimpleModule;
////import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
////import com.fasterxml.jackson.datatype.joda.JodaModule;
//import com.google.common.io.ByteSource;
//import com.opengamma.strata.basics.currency.Currency;
//import com.opengamma.strata.basics.index.FloatingRateName;
//import com.opengamma.strata.collect.io.ResourceLocator;
//import com.opengamma.strata.loader.fpml.FpmlDocumentParser;
//import com.opengamma.strata.loader.fpml.FpmlPartySelector;
////import com.opengamma.strata.loader.impl.json.CurrencyDecoder;
////import com.opengamma.strata.loader.impl.json.CurrencyEncoder;
////import com.opengamma.strata.loader.impl.json.FloatingRateNameDecoder;
////import com.opengamma.strata.loader.impl.json.FloatingRateNameEncoder;
//import com.opengamma.strata.product.Trade;
//import com.opengamma.strata.product.TradeInfo;
//import com.opengamma.strata.product.swap.Swap;
//import com.opengamma.strata.product.swaption.SwaptionTrade;
//import com.opengamma.strata.product.swaption.*;
//import org.joda.beans.Bean;
//import org.joda.beans.ImmutableBean;
//import org.joda.beans.JodaBeanUtils;
//import org.joda.beans.MetaBean;
//import org.joda.beans.gen.BeanDefinition;
//import org.joda.beans.ser.JodaBeanSmartReader;
//import org.joda.beans.ser.SerDeserializers;
//import org.joda.beans.ser.SerTypeMapper;
//import org.joda.beans.ser.json.JodaBeanJsonReader;
//import org.joda.beans.ser.json.JodaBeanJsonWriter;
//import org.junit.jupiter.api.Test;
//
//import java.io.Serializable;
//import java.nio.charset.StandardCharsets;
//import java.time.ZonedDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//import org.joda.beans.ser.JodaBeanSer;
//
//import static java.lang.System.out;
//import static org.assertj.core.api.Assertions.assertThat;
//
//public class JsonParserTest {
//
////    @BeanDefinition
////    public static class TestBean implements ImmutableBean, Serializable {
////
////        public SwaptionTrade trade;
////
////        @Override
////        public MetaBean metaBean() {
////            return TestBean.Meta.INSTANCE;
////        }
////    }
//
//    @Test
//    public void swaption_bermuda() {
////        ObjectMapper mapper =
////            JsonMapper
////                .builder()
////                .addModule(new Jdk8Module())
////                .addModule(new JavaTimeModule())
////                .addModule(new JodaModule())
////                .build();
//
//
//        JodaBeanSer serializer = JodaBeanSer.COMPACT;
//        JodaBeanJsonWriter jodaWriter = serializer.jsonWriter();
//        JodaBeanJsonReader jodaReader = serializer.jsonReader();
//
////        SimpleModule currencyEncoder =
////            new SimpleModule(
////                "CurrencyEncoder",
////                new Version(1, 0, 0, null, null, null));
////        currencyEncoder.addSerializer(Currency.class, new CurrencyEncoder());
////        mapper.registerModule(currencyEncoder);
////
////        SimpleModule currencyDecoder =
////                new SimpleModule("CurrencyDecoder", new Version(1, 0, 0, null, null, null));
////        currencyDecoder.addDeserializer(Currency.class, new CurrencyDecoder());
////        mapper.registerModule(currencyDecoder);
////        //Currency car = mapper.readValue(json, Car.class);
////
////        SimpleModule floatingRateEncoder = new SimpleModule("FloatingRateNameEncoder", new Version(1, 0, 0, null, null, null));
////        floatingRateEncoder.addSerializer(FloatingRateName.class, new FloatingRateNameEncoder());
////        mapper.registerModule(floatingRateEncoder);
////
////        SimpleModule floatingRateNameDecoder =
////                new SimpleModule("FloatingRateNameDecoder", new Version(1, 0, 0, null, null, null));
////        currencyDecoder.addDeserializer(FloatingRateName.class, new FloatingRateNameDecoder());
////        mapper.registerModule(floatingRateNameDecoder);
//
//        String fmplLocation = "classpath:com/opengamma/strata/loader/fpml/ird-ex14-berm-swaption.xml";
//        ByteSource resource = ResourceLocator.of(fmplLocation).getByteSource();
//        FpmlDocumentParser parser = FpmlDocumentParser.of(FpmlPartySelector.matching("Party1"));
//        assertThat(parser.isKnownFormat(resource)).isTrue();
//        List<Trade> trades = parser.parseTrades(resource);
//        SwaptionTrade trade = (com.opengamma.strata.product.swaption.SwaptionTrade) trades.get(0);
//
//        List<String> tradeJsonList = new ArrayList<>();
//        try {
//            int i = 0;
////            Currency ccy = Currency.USD;
////            String ccyJson = mapper.writeValueAsString(ccy);
////            out.println("TEST CURR USD " + ccyJson);
//            out.println ("number of trades " + trades.size());
//            TradeInfo info = trade.getInfo();
//            Swaption swaption = (Swaption) trade.getProduct();
//            SwaptionExercise exercise = swaption.getExerciseInfo().get();
//            String exerciseJson = jodaWriter.write(exercise);
//            SwaptionExercise exerciseBean = (SwaptionExercise) jodaReader.read(exerciseJson, SwaptionExercise.class);
//
//            String swaptionJson = jodaWriter.write(swaption);
//            Bean swaptionBean = jodaReader.read(swaptionJson, Swaption.class);
//
//            out.println ("exercise " + exerciseJson);
//            out.println ("swaption " + swaptionJson);
////            SwaptionSettlement settlement = swaption.getSwaptionSettlement();
////            String settlementJson = jodaWriter.write(settlement);
////            out.println ("settlement " + settlementJson);
//            ZonedDateTime expryDT = swaption.getExpiry();
//            out.println ("expiry " + expryDT);
////            String expiryJson = jodaWriter.write(expryDT);
////            out.println ("expiry " + expiryJson);
////            Swap underlying = swaption.getUnderlying();
////            String underlyingJson = jodaWriter.write(underlying);
//
////            Swap underlyingBean = (Swap) jodaReader.read(underlyingJson.getBytes(StandardCharsets.UTF_8), Swap.class);
////            out.println ("underlying " + underlyingJson);
//            String infoJson = jodaWriter.write(info);
//            tradeJsonList.add(infoJson);
//            String productJson = jodaWriter.write(swaption);
//            tradeJsonList.add(productJson);
//            out.println (trade.getClass().getName());
//            String tradeJson = jodaWriter.write(trade);
//            out.println ("Trade json " + tradeJson);
//            tradeJsonList.add(tradeJson);
//            //JodaBeanJsonReader jodaReader2 = JodaBeanSer.COMPACT.jsonReader();
//            SerDeserializers deserializers = new SerDeserializers();
//            Bean recoveredBean = serializer.jsonReader().read(tradeJson, SwaptionTrade.class);
//            Trade recoveredTrade = (Trade) recoveredBean;
//            out.println("Etuka Trade: " + recoveredTrade);
//        } catch (Exception e) {
//            out.println (e.getMessage());
//            assert (false);
//        }
//    }
//}

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
    public void swaption_first_try_bermuda() {
        JodaBeanSer serializer = JodaBeanSer.COMPACT;

        String fmplLocation = "classpath:com/opengamma/strata/loader/fpml/ird-ex14-berm-swaption.xml";
        ByteSource resource = ResourceLocator.of(fmplLocation).getByteSource();
        FpmlDocumentParser parser = FpmlDocumentParser.of(FpmlPartySelector.matching("Party1"));
        assertThat(parser.isKnownFormat(resource)).isTrue();
        List<Trade> trades = parser.parseTrades(resource);
        SwaptionTrade trade = (com.opengamma.strata.product.swaption.SwaptionTrade) trades.get(0);

        List<String> tradeJsonList = new ArrayList<>();
        try {
            int i = 0;
            out.println("number of trades " + trades.size());
            TradeInfo info = trade.getInfo();
            Swaption swaption = (Swaption) trade.getProduct();
            SwaptionExercise exercise = swaption.getExerciseInfo().get();
            String exerciseJson = serializer.jsonWriter().write(exercise);
            SwaptionExercise exerciseBean = (SwaptionExercise) serializer.jsonReader().read(exerciseJson, SwaptionExercise.class);

            String swaptionJson = serializer.jsonWriter().write(swaption);
            Bean swaptionBean = serializer.jsonReader().read(swaptionJson, Swaption.class);

            out.println("exercise " + exerciseJson);
            out.println("swaption " + swaptionJson);
            ZonedDateTime expryDT = swaption.getExpiry();
            out.println("expiry " + expryDT);
            String infoJson = serializer.jsonWriter().write(info);
            tradeJsonList.add(infoJson);
            String productJson = serializer.jsonWriter().write(swaption);
            tradeJsonList.add(productJson);
            out.println(trade.getClass().getName());
            String tradeJson = serializer.jsonWriter().write(trade);
            out.println("Trade json " + tradeJson);
            tradeJsonList.add(tradeJson);
            Bean recoveredBean = serializer.jsonReader().read(tradeJson, SwaptionTrade.class);
            Trade recoveredTrade = (Trade) recoveredBean;
            out.println("Etuka Trade: " + recoveredTrade);
        } catch (Exception e) {
            out.println(e.getMessage());
            assert (false);
        }
    }


    @Test
    public void swaption_bermuda() {
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