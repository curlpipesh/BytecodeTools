package me.curlpipesh.bytecodetools.inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import static me.curlpipesh.bytecodetools.BytecodeTools.log;

/**
 * @author audrey
 * @since 4/29/15
 */
public abstract class Injector implements ClassFileTransformer, Opcodes {
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public final byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
        if(getClass().getDeclaredAnnotation(Inject.class).value().equals(s)) {
            log("Injecting " + getClass().getDeclaredAnnotation(Inject.class).value() + "...");
            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode();
            cr.accept(cn, 0);
            inject(cr, cn);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            log("Done!");
            return cw.toByteArray();
        } else {
            throw new IllegalStateException("@Inject isn't present!?");
        }
    }

    protected abstract void inject(ClassReader cr, ClassNode cn);
}
