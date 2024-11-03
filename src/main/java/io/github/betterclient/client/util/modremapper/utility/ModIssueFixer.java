package io.github.betterclient.client.util.modremapper.utility;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.downloader.MinecraftVersion;
import io.github.betterclient.client.util.modremapper.mixin.MixinMethodMapper;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.lang.reflect.Modifier;

import static io.github.betterclient.client.util.modremapper.utility.ModRemapperUtility.detectMixin;
import static org.objectweb.asm.Opcodes.*;

public class ModIssueFixer {
    public static void edit(ClassNode node, File currentMod) throws Exception {
        if(detectMixin(node)) {
            for (MethodNode method : node.methods) {
                if(method.visibleAnnotations != null) {
                    for (AnnotationNode visibleAnnotation : method.visibleAnnotations) {
                        if(MixinMethodMapper.OVERWRITE_MAPPER.detect(visibleAnnotation)) {
                            method.access = (Modifier.isStatic(method.access) ?  ACC_STATIC : 0) + ACC_PUBLIC;
                        }
                    }
                }
            }
        }

        if(Application.minecraft.version().version() == MinecraftVersion.Version.V1_19_4 || Application.minecraft.version().version() == MinecraftVersion.Version.COMBAT_TEST_8C)
            if(node.name.equals("net/fabricmc/fabric/mixin/entity/event/LivingEntityMixin") || node.name.equals("net/fabricmc/fabric/mixin/entity/event/client/LivingEntityMixin"))
                node.methods.removeIf(method -> method.name.equals("onGetSleepingDirection"));

        if ((
                Application.minecraft.version().version() == MinecraftVersion.Version.COMBAT_TEST_8C ||
                        Application.minecraft.version().version() == MinecraftVersion.Version.V1_19_4 ||
                        Application.minecraft.version().version() == MinecraftVersion.Version.V1_20_1
        ) && node.name.equals("net/fabricmc/fabric/mixin/screen/GameRendererMixin")) {
            node.methods.removeIf(methodNode -> methodNode.name.equals("onBeforeRenderScreen") || methodNode.name.equals("onAfterRenderScreen"));
        }

        IBridge.getPreLaunch().modifyVersion(node, currentMod);
    }
}
