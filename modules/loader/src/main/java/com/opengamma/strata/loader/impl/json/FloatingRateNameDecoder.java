package com.opengamma.strata.loader.impl.json;

//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.ObjectCodec;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
//import com.opengamma.strata.basics.index.FloatingRateName;
//import com.opengamma.strata.basics.index.ImmutableFloatingRateName;
//
//import java.io.IOException;
//
//public class FloatingRateNameDecoder extends StdDeserializer<FloatingRateName> {
//
//    public FloatingRateNameDecoder() {
//        this(null);
//    }
//
//    public FloatingRateNameDecoder(Class<?> vc) {
//        super(vc);
//    }
//
//    @Override
//    public FloatingRateName deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
//
//        ObjectCodec codec = parser.getCodec();
//        JsonNode node = codec.readTree(parser);
//        JsonNode currencyNode = node.get("floatingRateName");
//
//        return FloatingRateName.of(currencyNode.asText());
//    }
//}