package me.curlpipesh.bytecodetools.define;

import java.lang.instrument.ClassDefinition;

/**
 * @author audrey
 * @since 5/23/15
 */
public interface Redefiner {
    ClassDefinition redefine();
}
