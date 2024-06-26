package net.dzikoysk.funnyguilds.config;

import org.jetbrains.annotations.Nullable;

public interface StringValidation {

    int getMinLength();

    int getMaxLength();

    FunnyPattern getRegex();

    default boolean validate(@Nullable String string) {
        if (string == null) {
            return false;
        }

        int length = string.length();
        if (length < this.getMinLength() || length > this.getMaxLength()) {
            return false;
        }

        return this.getRegex().matches(string);
    }

}
