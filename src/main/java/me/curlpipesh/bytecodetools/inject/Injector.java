package me.curlpipesh.bytecodetools.inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author audrey
 * @since 4/29/15
 */
public interface Injector {
    void inject(ClassReader cr, ClassNode cn);
}
