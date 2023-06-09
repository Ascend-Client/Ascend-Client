package io.github.betterclient.client.mixin;

import io.github.betterclient.client.mod.impl.other.CrystalOptimizer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalEntity.class)
public abstract class MixinEndCrystal extends Entity {
    private MixinEndCrystal() {
        super(null, null);
        throw new AssertionError("The life is hard, but initializing @Mixin is harder.");
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void hcscr$hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!CrystalOptimizer.get().explodesClientSide((EndCrystalEntity) (Object) this, source, amount)) {
            return;
        }

        remove();
        cir.setReturnValue(true);
    }
}