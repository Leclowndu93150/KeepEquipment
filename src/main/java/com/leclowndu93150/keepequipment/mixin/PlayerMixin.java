package com.leclowndu93150.keepequipment.mixin;

import com.leclowndu93150.keepequipment.KeepEquipmentMain;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Random;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow @Final private Inventory inventory;
    @Shadow public int experienceLevel;

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow public abstract ItemEntity drop(ItemStack pDroppedItem, boolean pDropAround, boolean pIncludeThrowerName);
    @Shadow protected abstract void destroyVanishingCursedItems();
    @Shadow public abstract boolean isSpectator();
    @Shadow public abstract boolean isCreative();

    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    protected void dropEquipment(CallbackInfo ci) {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        int i;
        if (serverLevel.getGameRules().getInt(KeepEquipmentMain.RULE_EQUIPMENT_DEGRADATION_PERCENTAGE) > 0 && !this.isCreative() && !this.isSpectator()) {
            for (i = 0; i < this.inventory.getContainerSize(); ++i) {
                ItemStack item = this.inventory.getItem(i);
                if (item.isDamageableItem()) {
                    float damageMult = (float)serverLevel.getGameRules().getInt(KeepEquipmentMain.RULE_EQUIPMENT_DEGRADATION_PERCENTAGE) / 100.0F;
                    item.setDamageValue(Math.min(item.getDamageValue() + (int)((float)item.getMaxDamage() * damageMult), item.getMaxDamage() - 1));
                    this.inventory.setItem(i, item);
                }
            }
        }

        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && serverLevel.getGameRules().getBoolean(KeepEquipmentMain.RULE_KEEP_EQUIPMENT)) {
            this.destroyVanishingCursedItems();

            for (i = 0; i < this.inventory.getContainerSize(); ++i) {
                if (!this.inventory.getItem(i).is(KeepEquipmentMain.KEPT_WITH_KEEPEQUIPMENT)) {
                    this.drop(this.inventory.getItem(i), true, false);
                    this.inventory.removeItemNoUpdate(i);
                }
            }

            this.dropItemsAboveMaxKept(serverLevel);
            ci.cancel();
        }

        if (serverLevel.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            this.dropItemsAboveMaxKept(serverLevel);
        }
    }

    protected void dropItemsAboveMaxKept(ServerLevel serverLevel) {
        int maxItemsKept = serverLevel.getGameRules().getInt(KeepEquipmentMain.RULE_MAX_ITEMS_KEPT_ON_DEATH);
        if (maxItemsKept > 0) {
            ArrayList<Integer> itemIndexes = new ArrayList<>();

            int itemsToDrop;
            for (itemsToDrop = 0; itemsToDrop < this.inventory.getContainerSize(); ++itemsToDrop) {
                if (!this.inventory.getItem(itemsToDrop).isEmpty()) {
                    itemIndexes.add(itemsToDrop);
                }
            }

            itemsToDrop = itemIndexes.size() - maxItemsKept;
            if (itemsToDrop <= 0) {
                return;
            }

            Random rand = new Random();

            for (int i = 0; i < itemsToDrop; ++i) {
                int itemIndex = itemIndexes.remove(rand.nextInt(itemIndexes.size()));
                this.drop(this.inventory.getItem(itemIndex), true, false);
                this.inventory.removeItemNoUpdate(itemIndex);
            }
        } else {
            this.inventory.dropAll();
        }
    }

    @Inject(method = "getExperienceReward", at = @At("HEAD"), cancellable = true)
    protected void getExperienceReward(CallbackInfoReturnable<Integer> cir) {
        if (!(this.level() instanceof ServerLevel serverLevel)) return;

        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) &&
                serverLevel.getGameRules().getInt(KeepEquipmentMain.RULE_KEPT_EXPERIENCE_PERCENTAGE) > 0 &&
                !this.isSpectator()) {
            float keptExperiencePercent = Mth.clamp((float)serverLevel.getGameRules().getInt(KeepEquipmentMain.RULE_KEPT_EXPERIENCE_PERCENTAGE) / 100.0F, 0.0F, 1.0F);
            cir.setReturnValue(Math.min((int)Math.floor((double)((float)(this.experienceLevel * 7) * (1.0F - keptExperiencePercent))), 100));
        }
    }
}