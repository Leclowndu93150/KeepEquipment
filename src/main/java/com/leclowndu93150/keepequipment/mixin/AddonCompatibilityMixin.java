package com.leclowndu93150.keepequipment.mixin;

import com.leclowndu93150.keepequipment.KeepEquipmentMain;
import de.rubixdev.inventorio.player.PlayerInventoryAddon;
import de.rubixdev.inventorio.util.PlayerDuck;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, priority = 900)
public abstract class AddonCompatibilityMixin extends LivingEntity {
    protected AddonCompatibilityMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "dropEquipment", at = @At("HEAD"))
    protected void handleAddonInventoryDrop(CallbackInfo ci) {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        PlayerInventoryAddon addon = ((PlayerDuck)this).inventorio$getInventorioAddon();
        if (addon == null) return;

        // Apply damage to tool belt items
        if (serverLevel.getGameRules().getInt(KeepEquipmentMain.RULE_EQUIPMENT_DEGRADATION_PERCENTAGE) > 0) {
            float damageMult = (float)serverLevel.getGameRules().getInt(KeepEquipmentMain.RULE_EQUIPMENT_DEGRADATION_PERCENTAGE) / 100.0F;
            for (ItemStack item : addon.toolBelt) {
                if (item.isDamageableItem()) {
                    item.setDamageValue(Math.min(item.getDamageValue() +
                            (int)((float)item.getMaxDamage() * damageMult), item.getMaxDamage() - 1));
                }
            }
        }

        // Drop non-tagged utility belt items
        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            for (int i = 0; i < addon.utilityBelt.size(); i++) {
                ItemStack stack = addon.utilityBelt.get(i);
                if (!stack.is(KeepEquipmentMain.KEPT_WITH_KEEPEQUIPMENT)) {
                    ((Player)(Object)this).drop(stack, true, false);
                    addon.utilityBelt.set(i, ItemStack.EMPTY);
                }
            }
        }
    }
}
