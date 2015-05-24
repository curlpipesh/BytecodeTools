package me.curlpipesh.bytecodetools.define;

/**
 * @author audrey
 * @since 5/24/15
 */
public interface Predefiner {
    byte[] predefine();

    String name();
}
