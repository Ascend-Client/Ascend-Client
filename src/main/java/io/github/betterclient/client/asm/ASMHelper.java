package io.github.betterclient.client.asm;

import org.objectweb.asm.Opcodes;

public class ASMHelper {
    public static boolean isPublic(int modifiers) {
        return (modifiers & 0x0001) != 0;
    }

    public static boolean isFinal(int modifiers) {
        return (modifiers & 0x0010) != 0;
    }

    public static boolean isInterface(int modifiers) {
        return (modifiers & Opcodes.ACC_INTERFACE) != 0;
    }

    public static boolean isEnum(int modifiers) {
        return (modifiers & Opcodes.ACC_ENUM) != 0;
    }

    public static boolean isStatic(int modifiers) {
        return (modifiers & Opcodes.ACC_STATIC) != 0;
    }
}
