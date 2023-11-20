package net.dzikoysk.funnyguilds.feature.command.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.dzikoysk.funnyguilds.feature.command.AbstractFunnyCommand;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandRegister {

    String key();

    Class<? extends AbstractFunnyCommand> as();

}
