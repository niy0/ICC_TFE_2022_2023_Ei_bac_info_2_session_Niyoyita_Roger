package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class ByteArrayInputStreamSerializer extends StdSerializer<ByteArrayInputStream> {

    public ByteArrayInputStreamSerializer() {
        super(ByteArrayInputStream.class);
    }

    @Override
    public void serialize(ByteArrayInputStream value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        byte[] bytes = value.readAllBytes();
        gen.writeString(Base64.getEncoder().encodeToString(bytes));
    }
}

