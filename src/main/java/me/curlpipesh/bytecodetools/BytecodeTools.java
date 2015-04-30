package me.curlpipesh.bytecodetools;

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
        String[] args = agentArgs.split(" ");
        System.out.println("Loading transformers...");
        List<Class<?>> transformers = Collections.synchronizedList(ClassEnumerator
                .getClassesFromJar(new File(args[0]),
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
}
