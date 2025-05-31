package com.opengamma.strata.loader.impl.json;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.ObjectCodec;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
//import com.opengamma.strata.basics.currency.Currency;
//
//import java.io.IOException;
//
//public class CurrencyDecoder extends StdDeserializer<Currency> {
//
//    public CurrencyDecoder() {
//        this(null);
//    }
//
//    public CurrencyDecoder(Class<?> vc) {
//        super(vc);
//    }
//
//    @Override
//    public Currency deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
//
//        ObjectCodec codec = parser.getCodec();
//        JsonNode node = codec.readTree(parser);
//        JsonNode currencyNode = node.get("currency");
//
//        return Currency.of(currencyNode.asText());
//    }
//}