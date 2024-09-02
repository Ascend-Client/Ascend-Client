package io.github.betterclient.version.mixin.client.network;

import com.mojang.brigadier.CommandDispatcher;
import io.github.betterclient.client.Ascend;
import io.github.betterclient.client.bridge.IBridge;
import io.github.betterclient.client.event.impl.RegisterCommandsEvent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.SayCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandManager.class)
public abstract class MixinCommandManager {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/SayCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V"), method = "<init>")
	private void fabric_addCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		Ascend.getInstance().bus.call(new RegisterCommandsEvent((IBridge.CommandDispatcher) dispatcher));

		SayCommand.register(dispatcher);
	}
}