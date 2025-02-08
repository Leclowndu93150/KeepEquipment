package com.leclowndu93150.keepequipment.mixin;

import com.leclowndu93150.keepequipment.KeepEquipmentMain;
import com.leclowndu93150.keepequipment.KeepEquipmentUtil;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile) {
        super(pLevel, pPos, pYRot, pGameProfile);
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    public void restoreFrom(ServerPlayer pThat, boolean pKeepEverything, CallbackInfo ci) {
        if (!pKeepEverything && !this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            if (this.level().getGameRules().getBoolean(KeepEquipmentMain.RULE_KEEP_EQUIPMENT)) {
                this.getInventory().replaceWith(pThat.getInventory());
            }

            if (this.level().getGameRules().getInt(KeepEquipmentMain.RULE_KEPT_EXPERIENCE_PERCENTAGE) > 0) {
                float keptExperiencePercent = Mth.clamp((float)this.level().getGameRules().getInt(KeepEquipmentMain.RULE_KEPT_EXPERIENCE_PERCENTAGE) / 100.0F, 0.0F, 1.0F);
                int newExperience = (int)((float) KeepEquipmentUtil.levelsToXP((float)pThat.experienceLevel + pThat.experienceProgress) * keptExperiencePercent);
                float newLevels = KeepEquipmentUtil.xpToLevels(newExperience);
                float newProgress = newLevels % 1.0F;
                newLevels = (float)Mth.floor(newLevels);
                this.experienceLevel = (int)newLevels;
                this.experienceProgress = newProgress;
                this.totalExperience = newExperience;
            }
        }
    }
}