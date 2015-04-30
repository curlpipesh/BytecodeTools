package me.curlpipesh.bytecodetools;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import me.curlpipesh.bytecodetools.inject.Inject;
import me.curlpipesh.bytecodetools.util.ClassEnumerator;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author audrey
 * @since 4/29/15
 */
public class BytecodeTools {


    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("args: " + agentArgs);
        String[] args = agentArgs.split(" ");
        System.out.println("Starting up...");
        OptionParser parser = new OptionParser();
        parser.accepts("injector").withRequiredArg();

        OptionSet options = parser.parse(args);
        if(!options.has("injector")) {
            System.err.println("Missing option: --injector");
            System.exit(1);
        }
        if(!options.hasArgument("injector")) {
            System.err.println("Missing argument to option: --injector");
            System.exit(1);
        }
        if(!options.valueOf("injector").toString().toLowerCase().endsWith(".jar")) {
            System.err.println("Injector is not a JAR!");
            System.exit(1);
        }
        System.out.println("Arguments look good!");
        System.out.println("--injector: " + options.valueOf("injector").toString());
        System.out.println("Loading transformers...");
        List<Class<?>> transformers = Collections.synchronizedList(ClassEnumerator
                .getClassesFromJar(new File(options.valueOf("injector").toString()),
                        BytecodeTools.class.getClassLoader()).stream()
                .filter(ClassFileTransformer.class::isAssignableFrom).collect(Collectors.toList()));
        if(transformers.size() == 0) {
            System.err.println("No transformers found!");
            System.exit(1);
        }
        transformers.stream().filter(ClassFileTransformer.class::isAssignableFrom)
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .forEach(transformer -> {
                    try {
                        inst.addTransformer((ClassFileTransformer) transformer.getConstructor().newInstance());
                        System.out.println("Added transformer: " + transformer.getName());
                    } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
    }


    /*@SuppressWarnings({"unchecked", "ConstantConditions"})
    public static void main(String[] args) {
        System.out.println("Starting up...");
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
        System.out.println("Arguments look good!");
        System.out.println("--target:   " + options.valueOf("target").toString());
        System.out.println("--injector: " + options.valueOf("injector").toString());
        System.out.println("Loading injectors...");
        List<Class<?>> injectors = Collections.synchronizedList(ClassEnumerator
                .getClassesFromJar(new File(options.valueOf("injector").toString()),
                        BytecodeTools.class.getClassLoader()).stream().collect(Collectors.toList()));
        if(injectors.size() == 0) {
            System.err.println("No injectors found!");
            System.exit(1);
        }
        System.out.println("Preparing target...");
        JarFile file;
        try {
            file = new JarFile(new File(options.valueOf("target").toString()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Doing the magic...");
        Enumeration<JarEntry> entries = file.entries();
        while(entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            System.out.println("Found entry: " + entry.getName());
            if(entry.getName().toLowerCase().endsWith(".class")) {
                System.out.println("Preparing to magick: " + entry.getName());
                ClassReader cr;
                InputStream stream;
                try {
                    stream = file.getInputStream(entry);
                    cr = new ClassReader(stream);
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
                                System.out.println("Magicking " + entry.getName() + " with " + m.getDeclaringClass().getName() + "#" + m.getName() + "...");
                                m.invoke(injector.newInstance(), cr, cn);
                            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("All done!");
    }*/
}
