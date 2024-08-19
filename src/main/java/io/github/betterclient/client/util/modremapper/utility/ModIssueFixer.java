package io.github.betterclient.client.util.modremapper.utility;

import io.github.betterclient.client.Application;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.util.downloader.MinecraftVersion;
import io.github.betterclient.client.util.modremapper.mixin.MixinMethodMapper;
import io.github.betterclient.fabric.FabricLoader;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static io.github.betterclient.client.util.modremapper.utility.ModRemapperUtility.detectMixin;
import static org.objectweb.asm.Opcodes.*;

/**
 * fixes issues in replaymod and iris
 * <p>
 * these issues exist in the fabric version of the mods aswell
 */
public class ModIssueFixer {
    public static void edit(ClassNode node, File currentMod) throws Exception {
        if(detectMixin(node)) {
            for (MethodNode method : node.methods) {
                if(method.visibleAnnotations == null) {
                    method.visibleAnnotations = new ArrayList<>(List.of(new AnnotationNode("Lorg/spongepowered/asm/mixin/Unique;")));
                    if(Modifier.isStatic(method.access) && !Modifier.isAbstract(node.access)) {
                        method.access = ACC_STATIC + ACC_PRIVATE;
                    }
                } else {
                    for (AnnotationNode visibleAnnotation : method.visibleAnnotations) {
                        if(MixinMethodMapper.OVERWRITE_MAPPER.detect(visibleAnnotation)) {
                            method.access = (Modifier.isStatic(method.access) ?  ACC_STATIC : 0) + ACC_PUBLIC;
                        }
                    }
                }
            }
        }

        if(Application.minecraft.version().version() != MinecraftVersion.Version.V1_20_6)
            if(node.name.equals("net/fabricmc/fabric/mixin/entity/event/LivingEntityMixin") || node.name.equals("net/fabricmc/fabric/mixin/entity/event/client/LivingEntityMixin"))
                node.methods.removeIf(method -> method.name.equals("onGetSleepingDirection"));

        IBridge.getPreLaunch().modifyVersion(node, currentMod);
    }
}
