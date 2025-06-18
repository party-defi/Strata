package com.opengamma.strata.examples.data.export;

import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;

import java.io.IOException;
import java.util.Optional;

/**
 * Custom encoder for java.util.Optional for use with jsoniter.
 * Avoids the need for reflective access to Optional's private fields which
 * is restricted by the Java module system in Java 9+.
 */
public class OptionalEncoder implements Encoder {

    /**
     * Register this encoder with jsoniter.
     * Call this method before any serialization happens.
     */
    public static void register() {
        JsoniterSpi.registerTypeEncoder(Optional.class, new OptionalEncoder());
    }

    @Override
    public void encode(Object obj, JsonStream stream) throws IOException {
        if (obj == null) {
            stream.writeNull();
            return;
        }

        @SuppressWarnings("unchecked")
        Optional<?> optional = (Optional<?>) obj;
        
        if (optional.isPresent()) {
            // Get the value and serialize it directly
            Object value = optional.get();
            stream.writeVal(value);
        } else {
            // Empty optional is serialized as null
            stream.writeNull();
        }
    }
}