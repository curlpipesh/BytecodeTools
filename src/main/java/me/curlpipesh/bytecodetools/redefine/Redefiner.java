package me.curlpipesh.bytecodetools.redefine;

import java.lang.instrument.ClassDefinition;

/**
 * @author audrey
 * @since 5/23/15
 */
public interface Redefiner {
    ClassDefinition redefine();
}
