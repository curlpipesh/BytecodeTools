package me.curlpipesh.bytecodetools;

import me.curlpipesh.bytecodetools.inject.Inject;
import me.curlpipesh.bytecodetools.redefine.Redefiner;
import me.curlpipesh.bytecodetools.util.ClassEnumerator;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
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
        log("Loading transformers...");
        List<Class<?>> allClasses = Collections.synchronizedList(ClassEnumerator
                        .getClassesFromJar(new File(args[0]),
                                BytecodeTools.class.getClassLoader()));
        List<Class<?>> transformers = allClasses.stream()
                .filter(ClassFileTransformer.class::isAssignableFrom).collect(Collectors.toList());
        if(transformers.size() == 0) {
            log("No transformers found!");
            System.exit(1);
        }
        transformers.stream().filter(ClassFileTransformer.class::isAssignableFrom)
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .forEach(transformer -> {
                    try {
                        inst.addTransformer((ClassFileTransformer) transformer.getConstructor().newInstance());
                        log("Added transformer: " + transformer.getName());
                    } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
        log("Loading redefiners...");
        List<Class<?>> redefiners = new ArrayList<>();
        allClasses.stream().filter(Redefiner.class::isAssignableFrom).filter(c -> !c.equals(Redefiner.class))
                .forEach(redefiners::add);
        if(redefiners.size() > 0) {
            for(Class<?> e : redefiners) {
                try {
                    Redefiner r = (Redefiner) e.getConstructor().newInstance();
                    ClassDefinition d = r.redefine();
                    inst.redefineClasses(d);
                } catch(ClassNotFoundException | UnmodifiableClassException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e1) {
                    log(e.getName());
                    throw new RuntimeException(e1);
                }
            }

        }
    }

    public static void log(String... messages) {
        Arrays.stream(messages).forEach(m -> System.out.println("> " + m));
    }
}
