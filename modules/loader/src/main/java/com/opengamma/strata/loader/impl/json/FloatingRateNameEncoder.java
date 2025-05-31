package com.opengamma.strata.loader.impl.json;

//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import com.fasterxml.jackson.databind.ser.std.StdSerializer;
//import com.opengamma.strata.basics.index.FloatingRateName;
//import com.opengamma.strata.basics.index.ImmutableIborIndex;
//
//import java.io.IOException;
//
//public class FloatingRateNameEncoder extends StdSerializer<FloatingRateName> {
//    public FloatingRateNameEncoder() {
//        this(null);
//    }
//
//    public FloatingRateNameEncoder(Class<FloatingRateName> t) {
//        super(t);
//    }
//
//    @Override
//    public void serialize(
//            FloatingRateName index,
//            JsonGenerator jsonGenerator,
//            SerializerProvider serializer
//    ) throws IOException {
//       jsonGenerator.writeString(index.getFloatingRateName().getName());
//    }
//}
