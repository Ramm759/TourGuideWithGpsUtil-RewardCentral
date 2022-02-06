package com.tourguide.tourguide.helper;

import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Encoder;
import com.jsoniter.spi.JsoniterSpi;
import org.javamoney.moneta.Money;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;


@JsonComponent
public class MoneySerializer {
    MoneySerializer() {
        JsoniterSpi.registerTypeEncoder(Money.class, new Encoder() {
            @Override
            public void encode(Object obj, JsonStream stream) throws IOException {
                stream.writeVal(((Money) obj).getNumberStripped().doubleValue());
            }
        });
    }
}
