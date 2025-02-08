package com.leclowndu93150.keepequipment;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(KeepEquipmentMain.MODID)
public class KeepEquipmentMain {
    public static final String MODID = "keepequipment";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final GameRules.Key<GameRules.BooleanValue> RULE_KEEP_EQUIPMENT;
    public static final GameRules.Key<GameRules.IntegerValue> RULE_EQUIPMENT_DEGRADATION_PERCENTAGE;
    public static final GameRules.Key<GameRules.IntegerValue> RULE_MAX_ITEMS_KEPT_ON_DEATH;
    public static final GameRules.Key<GameRules.IntegerValue> RULE_KEPT_EXPERIENCE_PERCENTAGE;
    public static final TagKey<Item> KEPT_WITH_KEEPEQUIPMENT;

    static {
        RULE_KEEP_EQUIPMENT = GameRules.register("keepEquipment", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
        RULE_EQUIPMENT_DEGRADATION_PERCENTAGE = GameRules.register("equipmentDegradationPercentage", GameRules.Category.PLAYER, GameRules.IntegerValue.create(15));
        RULE_MAX_ITEMS_KEPT_ON_DEATH = GameRules.register("maxItemsKept", GameRules.Category.PLAYER, GameRules.IntegerValue.create(999));
        RULE_KEPT_EXPERIENCE_PERCENTAGE = GameRules.register("keptExperiencePercentage", GameRules.Category.PLAYER, GameRules.IntegerValue.create(50));
        KEPT_WITH_KEEPEQUIPMENT = ItemTags.create(new ResourceLocation("keepequipment", "kept_with_keepequipment"));
    }
}
