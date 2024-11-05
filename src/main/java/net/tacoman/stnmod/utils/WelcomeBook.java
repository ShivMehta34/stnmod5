package net.tacoman.stnmod.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;

public class WelcomeBook {

    public static ItemStack createWelcomeBook() {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        CompoundTag tag = book.getOrCreateTag();
        tag.putString("author", "Tacoman171717192");
        tag.putString("title", "Welcome to STN Mod");

        ListTag pages = new ListTag();
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(
                "Welcome to Tacoman171717192's STN Mod!\n\n" +
                        "I would like to thank Jimmy for his amazing books, which inspired me to create this mod and made reading enjoyable for many."
        ))));
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(
                "This is just the first version, made in 2 weeks starting with no coding experience, so this is just the beginning.\n\n"
        ))));
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(
                "The Upgrade Table recipe is the same as the book:\n- 4 Gold Blocks\n- 2 Diamonds\n- 1 Book\n\n" +
                        "To get custom weapons and class-specific armor, you need to get Bob by upgrading an Armorer villager to Master."
        ))));
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(
                "Right-click Bob with your hand to access these items.\n\n" +
                        "Diamond Destroyer and Bone Basher are very powerful. There is a chance you will get them with Bob; if not, make another Bob and try again."
        ))));
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(
                "Bob needs an Obsidian Blast Furnace to refresh his trades.\n\n" +
                        "You make it by using a Blast Furnace surrounded by Obsidian in the crafting table."
        ))));
        pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(
                "Special ability keys are X, C, V, or B.\n\n" +
                        "Right now, only the Tier One combat classes are ready.\n\n" +
                        "Yes, I know there will be bugs. I am one person who put this together as quickly as I could. I hope you enjoy!"
        ))));
        tag.put("pages", pages);
        book.setTag(tag);
        return book;
    }
}
