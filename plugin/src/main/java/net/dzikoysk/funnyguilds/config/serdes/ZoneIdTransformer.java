package net.dzikoysk.funnyguilds.config.serdes;

import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import java.time.ZoneId;

public class ZoneIdTransformer extends BidirectionalTransformer<String, ZoneId> {

    @Override
    public GenericsPair<String, ZoneId> getPair() {
        return this.genericsPair(String.class, ZoneId.class);
    }

    @Override
    public ZoneId leftToRight(String data, SerdesContext serdesContext) {
        return ZoneId.of(data);
    }

    @Override
    public String rightToLeft(ZoneId data, SerdesContext serdesContext) {
        return data.getId();
    }

}
