package me.curlpipesh.bytecodetools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.curlpipesh.bytecodetools.inject.Inject;
import me.curlpipesh.bytecodetools.inject.Injector;
import me.curlpipesh.bytecodetools.util.ClassEnumerator;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * @author audrey
 * @since 4/29/15
 */
public class BytecodeTools {
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        // JAR or class to inject into
        parser.accepts("target").withRequiredArg();
        // JAR or class to inject with
        parser.accepts("injector").withRequiredArg();

        OptionSet options = parser.parse(args);
        // Verify that everything needed is present
        if(!options.has("target")) {
            System.err.println("Missing option: --target");
            System.exit(1);
        }
        if(!options.has("injector")) {
            System.err.println("Missing option: --injector");
            System.exit(1);
        }
        if(!options.hasArgument("target")) {
            System.err.println("Missing argument to option: --target");
            System.exit(1);
        }
        if(!options.hasArgument("injector")) {
            System.err.println("Missing argument to option: --injector");
            System.exit(1);
        }
        // Verify that the target and the injector are JARs or classes
        if(!options.valueOf("target").toString().toLowerCase().endsWith(".jar")) {
            System.err.println("Target is not a JAR!");
            System.exit(1);
        }
        if(!options.valueOf("injector").toString().toLowerCase().endsWith(".jar")) {
            System.err.println("Injector is not a JAR!");
            System.exit(1);
        }
        List<Class<?>> injectors = Collections.synchronizedList(ClassEnumerator
                .getClassesFromJar(new File(options.valueOf("injector").toString()),
                        BytecodeTools.class.getClassLoader()).stream().collect(Collectors.toList()));
        if(injectors.size() == 0) {
            System.err.println("No injectors found!");
            System.exit(1);
        }

        JarFile file;
        try {
            file = new JarFile(new File(options.valueOf("target").toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while(file.entries().hasMoreElements()) {
            JarEntry entry = file.entries().nextElement();
            if(entry.getName().toLowerCase().endsWith(".class")) {
                ClassReader cr;
                try {
                    cr = new ClassReader(file.getInputStream(entry));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ClassNode cn = new ClassNode();
                cr.accept(cn, 0);
                for(Class<?> injector : injectors) {
                    if(injector.isAnnotationPresent(Inject.class)) {
                        if (injector.getDeclaredAnnotation(Inject.class).value().equals(entry.getName().substring(0, entry.getName().length() - 6))) {
                            Method m;
                            try {
                                m = injector.getDeclaredMethod("inject", ClassReader.class, ClassNode.class);
                            } catch (NoSuchMethodException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                m.invoke(injector.newInstance(), cr, cn);
                            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }
}
