package io.github.betterclient.client.mixin.client.network;

import com.mojang.brigadier.AmbiguityConsumer;
import com.mojang.brigadier.CommandDispatcher;
import io.github.betterclient.client.BallSack;
import io.github.betterclient.client.event.impl.RegisterCommandsEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

@Mixin(CommandManager.class)
public abstract class MixinCommandManager {
	@Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;findAmbiguities(Lcom/mojang/brigadier/AmbiguityConsumer;)V"), method = "<init>")
	private void fabric_addCommands(CommandDispatcher<ServerCommandSource> dispatcher, AmbiguityConsumer<ServerCommandSource> ambiguityConsumer, CommandManager.RegistrationEnvironment registrationEnvironment) {
		BallSack.getInstance().bus.call(new RegisterCommandsEvent(dispatcher));

		dispatcher.findAmbiguities(ambiguityConsumer);
	}
}