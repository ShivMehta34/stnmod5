package net.tacoman.stnmod.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.tacoman.stnmod.client.ClientModels;

import java.util.function.Consumer;

public class PaladinArmorItem extends ArmorItem {
    public PaladinArmorItem(ArmorMaterial material, Type type, Item.Properties props) {
        super(material, type, props);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        ClientModels.attachPaladinArmorClientExtensions(consumer);
    }

    // Put the texture override on the item (stable across Forge versions)
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "stnmod:textures/models/armor/paladin_armor.png";
    }
}
