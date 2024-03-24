package io.github.betterclient.client.mixin.client.network;

import com.mojang.brigadier.CommandDispatcher;
import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.event.impl.RegisterCommandsEvent;
import net.minecraft.server.command.SayCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

@Mixin(CommandManager.class)
public abstract class MixinCommandManager {
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/SayCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V"), method = "<init>")
	private void fabric_addCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		BallSack.getInstance().bus.call(new RegisterCommandsEvent(dispatcher));

		SayCommand.register(dispatcher);
	}
}