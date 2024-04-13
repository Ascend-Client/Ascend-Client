package io.github.betterclient.version.mixin.bridge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.betterclient.client.bridge.IBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(CommandDispatcher.class)
public abstract class MixinCommandDispatcher<S> implements IBridge.CommandDispatcher {
    @Shadow public abstract LiteralCommandNode<S> register(LiteralArgumentBuilder<S> command);

    @Override
    public void register(String commandName, IBridge.CommandExecutor executor, IBridge.CommandArgument... arguments) {
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.literal(commandName);
        for (IBridge.CommandArgument argument : arguments) {
            if(argument.clazz().equals(boolean.class)) {
                builder = builder.then(RequiredArgumentBuilder.argument(argument.name(), BoolArgumentType.bool()));
            } else if(argument.clazz().equals(double.class)) {
                builder = builder.then(RequiredArgumentBuilder.argument(argument.name(), DoubleArgumentType.doubleArg()));
            } else if(argument.clazz().equals(float.class)) {
                builder = builder.then(RequiredArgumentBuilder.argument(argument.name(), FloatArgumentType.floatArg()));
            } else if(argument.clazz().equals(int.class)) {
                builder = builder.then(RequiredArgumentBuilder.argument(argument.name(), IntegerArgumentType.integer()));
            } else if(argument.clazz().equals(long.class)) {
                builder = builder.then(RequiredArgumentBuilder.argument(argument.name(), LongArgumentType.longArg()));
            } else if(argument.clazz().equals(String.class)) {
                builder = builder.then(RequiredArgumentBuilder.argument(argument.name(), StringArgumentType.string()));
            } else {
                throw new RuntimeException("Invalid argument!");
            }
        }
        this.register(builder.executes(context -> {
            List<Object> args = new ArrayList<>();
            for (IBridge.CommandArgument argument : arguments) {
                if(argument.clazz().isInstance(boolean.class)) {
                    args.add(BoolArgumentType.getBool(context, argument.name()));
                } else if(argument.clazz().isInstance(double.class)) {
                    args.add(DoubleArgumentType.getDouble(context, argument.name()));
                } else if(argument.clazz().isInstance(float.class)) {
                    args.add(FloatArgumentType.getFloat(context, argument.name()));
                } else if(argument.clazz().isInstance(int.class)) {
                    args.add(IntegerArgumentType.getInteger(context, argument.name()));
                } else if(argument.clazz().isInstance(long.class)) {
                    args.add(LongArgumentType.getLong(context, argument.name()));
                } else if(argument.clazz().isInstance(String.class)) {
                    args.add(StringArgumentType.getString(context, argument.name()));
                } else {
                    throw new RuntimeException("Invalid argument!");
                }
            }

            return executor.execute(args);
        }));
    }
}
