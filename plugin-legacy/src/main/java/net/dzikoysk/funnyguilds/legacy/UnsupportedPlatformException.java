package net.dzikoysk.funnyguilds.legacy;

//https://github.com/PaperMC/paper-trail/blob/master/src/main/java/io/papermc/papertrail/UnsupportedPlatformException.java
public class UnsupportedPlatformException extends RuntimeException {
    UnsupportedPlatformException(String message) {
        super(message);
    }
}