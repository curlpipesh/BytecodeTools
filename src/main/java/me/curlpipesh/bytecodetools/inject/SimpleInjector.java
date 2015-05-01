package me.curlpipesh.bytecodetools.inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * @author audrey
 * @since 4/29/15
 */
@Inject("Thing")
@SuppressWarnings("unused")
public class SimpleInjector extends Injector {
    @Override
    @SuppressWarnings("unchecked")
    protected void inject(ClassReader cr, ClassNode cn) {
        ((List<MethodNode>) cn.methods).stream().filter(m -> m.name.equals("hake") && m.desc.equals("()V")).forEach(m -> {
            System.out.println("> Injecting...");
            InsnList list = new InsnList();
            list.add(new FieldInsnNode(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
            list.add(new LdcInsnNode("haked"));
            list.add(new MethodInsnNode(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
            m.instructions.insert(list);
            System.out.println("> Done!");
        });
    }
}
