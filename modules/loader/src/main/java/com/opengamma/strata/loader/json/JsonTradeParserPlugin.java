package com.opengamma.strata.loader.json;

//import com.fasterxml.jackson.core.JacksonException;
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.ObjectCodec;
//import com.fasterxml.jackson.databind.*;
//import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
//import com.fasterxml.jackson.databind.json.JsonMapper;
//import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
//import com.fasterxml.jackson.datatype.joda.JodaModule;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.opengamma.strata.product.Trade;
//import org.joda.beans.Bean;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.util.Iterator;
//import java.util.Map;
//
//import static java.lang.System.out;
//
///**
// * Assumptions: every sub-element of a type passed to the
// * @param <T>
// */
//
//public class JsonTradeParserPlugin<T extends Trade & Bean> extends StdDeserializer<T> {
//
//    static final ObjectMapper mapper = buildMapper();
//
//    static ObjectMapper buildMapper() {
//        return JsonMapper
//                .builder()
//                .addModule(new Jdk8Module())
//                .addModule(new JavaTimeModule())
//                .addModule(new JodaModule())
//                .build();
//    }
//    public JsonTradeParserPlugin() {
//        this(null);
//
//    }
//    public JsonTradeParserPlugin(Class<T> type) {
//        super(type);
//
//    }
//
//    @Override
//    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
//        ObjectCodec codec = p.getCodec();
//        JsonNode node = codec.readTree(p);
//        // we assume that if we can see it in the json, then it is registered with mapper or can be recursed into.
//        try {
//            for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
//                Map.Entry<String, JsonNode> child = it.next();
//                Field field = _valueClass.getField(child.getKey());
//
//            }
//        } catch (NoSuchFieldException ex) {
//            out.println ("OUCH" + ex.getMessage());
//        }
//        return null;
//    }
//}
