package me.curlpipesh.bytecodetools.inject;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

/**
 * @author audrey
 * @since 4/29/15
 */
@Inject("$_Dummy_bf1b99a12fe1b880fc97be4da0b2437ea6a9191877d308d08c9a60318adcc62a5de7c90eaf4ac992612c447eda31cda016cc90cbcc024a3719a4a79cd12827a9_$")
public class SimpleInjector implements Injector {
    @Override
    @SuppressWarnings("unchecked")
    public void inject(ClassReader cr, ClassNode cn) {
        for(MethodNode node : (List<MethodNode>)cn.methods) {

        }
    }
}
