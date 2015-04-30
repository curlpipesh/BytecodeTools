package me.curlpipesh.bytecodetools.inject;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author audrey
 * @since 4/29/15
 */
@Inject("Thing")
public class SimpleInjector extends Injector {
    /*@SuppressWarnings({"unchecked", "deprecation"})
    public byte[] inject(byte[] bytes) {
        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cr.accept(cw, 0);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);
        System.out.println("> Doing the magick...");
        for(MethodNode m : (List<MethodNode>)cn.methods) {
            if(m.name.equalsIgnoreCase("hake")) {
                Iterator<AbstractInsnNode> i = m.instructions.iterator();
                while(i.hasNext()) {
                    System.out.println(i.next());
                }

                InsnList list = new InsnList();
                list.add(new LabelNode(new Label()));
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;"));
                list.add(new LdcInsnNode("haked"));
                list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V"));

                m.instructions.insert(list);
                System.out.println("> Done!");
                Iterator<AbstractInsnNode> i2 = m.instructions.iterator();
                while(i2.hasNext()) {
                    System.out.println(i2.next());
                }
                break;
            }
        }
        return cw.toByteArray();
    }*/

    @Override
    protected ClassVisitor getVisitor() {
        return new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor m = new TestMethodVisitor(name, desc);
                m.visitEnd();
                return m;
            }
        };
    }

    private class TestMethodVisitor extends MethodVisitor {
        private final String name;
        private final String desc;

        public TestMethodVisitor(String n, String d) {
            super(Opcodes.ASM5);
            name = n;
            desc = d;
        }

        @Override
        public void visitCode() {
            if(name.equals("hake") && desc.equals("()V")) {
                System.out.println("> Visitation underway...");
                visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                visitLdcInsn("haked");
                visitMethodInsn(Opcodes.GETSTATIC, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                System.out.println("> Done!");
            }
        }
    }
}
