package net.dzikoysk.funnyguilds.feature.security.cheat;

import java.util.function.Function;
import net.dzikoysk.funnyguilds.config.message.MessageConfiguration;

public enum CheatType {

    REACH("Reach", config -> config.admin.securitySystem.reach),
    FREE_CAM("FreeCam", config -> config.admin.securitySystem.freeCam);

    private final String name;
    private final Function<MessageConfiguration, String> noteSupplier;

    CheatType(String name, Function<MessageConfiguration, String> noteSupplier) {
        this.name = name;
        this.noteSupplier = noteSupplier;
    }

    public String getName() {
        return this.name;
    }

    public Function<MessageConfiguration, String> getNoteSupplier() {
        return this.noteSupplier;
    }

}
