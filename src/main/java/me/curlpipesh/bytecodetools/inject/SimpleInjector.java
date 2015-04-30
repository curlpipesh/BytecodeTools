package me.curlpipesh.bytecodetools.inject;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author audrey
 * @since 4/29/15
 */
@Inject("Thing")
@Deprecated
class SimpleInjector extends Injector {
    @Override
    protected ClassVisitor getVisitor() {
        return new ClassVisitor(Opcodes.ASM5, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                if(name.equals("hake") && desc.equals("()V")) {
                    System.out.println("> Injecting...");
                    mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                    mv.visitLdcInsn("haked");
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                    System.out.println("> Done!");
                }
                return mv;
            }
        };
    }
}
