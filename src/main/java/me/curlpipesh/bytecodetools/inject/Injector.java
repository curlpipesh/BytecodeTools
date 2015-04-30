package me.curlpipesh.bytecodetools.inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author audrey
 * @since 4/29/15
 */
public abstract class Injector implements ClassFileTransformer {
    protected final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

    @Override
    @SuppressWarnings("ConstantConditions")
    public final byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
        if(getClass().getDeclaredAnnotation(Inject.class).value().equals(s)) {
            System.out.println("> Starting injection...");
            ClassReader cr = new ClassReader(bytes);
            cr.accept(getVisitor(), 0);
            return cw.toByteArray();
        } else {
            throw new IllegalStateException("@Inject isn't present!?");
        }
    }

    protected abstract ClassVisitor getVisitor();
}
