package net.dzikoysk.funnyguilds.nms.v1_21R1;

import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.v1_20R5.V1_20R5NmsAccessor;
import net.dzikoysk.funnyguilds.nms.v1_21R1.entity.V1_21R1EntityAccessor;

public class V1_21R1NmsAccessor extends V1_20R5NmsAccessor {

    @Override
    public EntityAccessor getEntityAccessor() {
        return new V1_21R1EntityAccessor();
    }

}
