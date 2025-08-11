    package net.tacoman.stnmod.blocks;

    import net.minecraft.core.BlockPos;
    import net.minecraft.core.particles.ParticleTypes;
    import net.minecraft.nbt.CompoundTag;
    import net.minecraft.nbt.ListTag;
    import net.minecraft.nbt.StringTag;
    import net.minecraft.network.chat.Component;
    import net.minecraft.server.level.ServerPlayer;
    import net.minecraft.util.RandomSource;
    import net.minecraft.world.InteractionHand;
    import net.minecraft.world.InteractionResult;
    import net.minecraft.world.entity.ai.attributes.AttributeInstance;
    import net.minecraft.world.entity.ai.attributes.AttributeModifier;
    import net.minecraft.world.entity.ai.attributes.Attributes;
    import net.minecraft.world.entity.player.Player;
    import net.minecraft.world.effect.MobEffectInstance;
    import net.minecraft.world.effect.MobEffects;
    import net.minecraft.world.item.Item;
    import net.minecraft.world.item.ItemStack;
    import net.minecraft.world.item.Items;
    import net.minecraft.world.level.BlockGetter;
    import net.minecraft.world.level.Level;
    import net.minecraft.world.level.block.Block;
    import net.minecraft.world.level.block.state.BlockState;
    import net.minecraft.world.phys.BlockHitResult;
    import net.minecraft.world.phys.shapes.CollisionContext;
    import net.minecraft.world.phys.shapes.Shapes;
    import net.minecraft.world.phys.shapes.VoxelShape;
    import net.tacoman.stnmod.client.ClassChangeEffectHandler;
    import net.tacoman.stnmod.init.EntityRegistry;
    import net.tacoman.stnmod.init.PotionEffectRegistry;
    import net.tacoman.stnmod.entities.NinjaCloneEntity;
    import net.tacoman.stnmod.init.ItemRegistry;
    import net.tacoman.stnmod.utils.PlayerDataUtils;

    import java.util.UUID;

    public class UpgradeTableBlock extends Block {
        private static final VoxelShape SHAPE = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
        private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
        private static final UUID BERSERKER_SPEED_MODIFIER_UUID = UUID.fromString("d9b6bc9e-717d-4c5a-9c58-dafc70a6c8c5");
        private static final UUID ASSASSIN_SPEED_MODIFIER_UUID = UUID.fromString("d9b6bc9e-717d-4c5a-9c58-dafc70a6c8c5");
        private static final UUID SHOGUN_SPEED_MODIFIER_UUID = UUID.fromString("a4f7e3d1-bc3d-4f89-a909-23c4d6f3d6c9");
        private static final UUID MARKSMAN_SPEED_MODIFIER_UUID = UUID.fromString("d4e3f6b7-a8c2-4911-a123-56e9d4a7f9b3");
        private static final UUID NIGHTWING_SPEED_MODIFIER_UUID = UUID.fromString("8b9a1c2f-3d47-4f8c-b9e2-7ad4fcb2d4e7");





        public UpgradeTableBlock(Properties properties) {
            super(properties);
        }

        @Override
        public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
            return 15; // Full light level
        }

        @Override
        public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
            super.animateTick(state, world, pos, random);

            // Add more particle effects
            for (int i = 0; i < 5; i++) {
                double d0 = (double) pos.getX() + 0.5 + (random.nextDouble() - 0.5);
                double d1 = (double) pos.getY() + 1.0 + (random.nextDouble() - 0.5);
                double d2 = (double) pos.getZ() + 0.5 + (random.nextDouble() - 0.5);
                double speedX = (random.nextDouble() - 0.5) * 0.2;
                double speedY = (random.nextDouble() - 0.5) * 0.2;
                double speedZ = (random.nextDouble() - 0.5) * 0.2;
                world.addParticle(ParticleTypes.ENCHANT, d0, d1, d2, speedX, speedY, speedZ);
            }
        }

        @Override
        public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
            return SHAPE;
        }

        @Override
        public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
                ItemStack itemInHand = serverPlayer.getItemInHand(hand);

                // -------- Tier 2 UPGRADE (only if the player CURRENTLY HAS a Tier-1 class effect) --------
                if (hasAnyTier1Effect(serverPlayer)) {
                    // Need 40 levels + 40 lapis + 40 diamonds AND the correct T2 token in hand
                    if (serverPlayer.experienceLevel >= 40
                            && countItems(serverPlayer, Items.LAPIS_LAZULI) >= 40
                            && countItems(serverPlayer, Items.DIAMOND) >= 40) {

                        if (serverPlayer.hasEffect(PotionEffectRegistry.THIEF_STRENGTH.get())) {
                            if (itemInHand.getItem() == ItemRegistry.ASSASSIN_CLASS.get()) {
                                setClassAssassin(serverPlayer);
                            } else {
                                serverPlayer.sendSystemMessage(Component.literal("You are a Thief. You can only upgrade to Assassin."));
                            }

                        } else if (serverPlayer.hasEffect(PotionEffectRegistry.KNIGHT_STRENGTH.get())) {
                            if (itemInHand.getItem() == ItemRegistry.PALADIN_CLASS.get()) {
                                setClassPaladin(serverPlayer);
                            } else {
                                serverPlayer.sendSystemMessage(Component.literal("You are a Knight. You can only upgrade to Paladin."));
                            }

                        } else if (serverPlayer.hasEffect(PotionEffectRegistry.GLADIATOR_STRENGTH.get())) {
                            if (itemInHand.getItem() == ItemRegistry.BERSERKER_CLASS.get()) {
                                setClassBerserker(serverPlayer);
                            } else {
                                serverPlayer.sendSystemMessage(Component.literal("You are a Gladiator. You can only upgrade to Berserker."));
                            }

                        } else if (serverPlayer.hasEffect(PotionEffectRegistry.SAMURAI_STRENGTH.get())) {
                            if (itemInHand.getItem() == ItemRegistry.SHOGUN_CLASS.get()) {
                                setClassShogun(serverPlayer);
                            } else {
                                serverPlayer.sendSystemMessage(Component.literal("You are a Samurai. You can only upgrade to Shogun."));
                            }

                        } else if (serverPlayer.hasEffect(PotionEffectRegistry.RANGER_STRENGTH.get())) {
                            if (itemInHand.getItem() == ItemRegistry.ELEMENTAL_RANGER_CLASS.get()) {
                                setClassELementalRanger(serverPlayer);
                            } else {
                                serverPlayer.sendSystemMessage(Component.literal("You are a Ranger. You can only upgrade to Elemental Ranger."));
                            }

                        } else if (serverPlayer.hasEffect(PotionEffectRegistry.SNIPER_STRENGTH.get())) {
                            if (itemInHand.getItem() == ItemRegistry.MARKSMAN_CLASS.get()) {
                                setClassMarksman(serverPlayer);
                            } else {
                                serverPlayer.sendSystemMessage(Component.literal("You are a Sniper. You can only upgrade to Marksman."));
                            }

                        } else if (serverPlayer.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get())) {
                            if (itemInHand.getItem() == ItemRegistry.NIGHTWING_CLASS.get()) {
                                setClassNightwing(serverPlayer); // ✅ fixed mapping
                            } else {
                                serverPlayer.sendSystemMessage(Component.literal("You are a Ninja. You can only upgrade to Nightwing.")); // ✅ fixed message
                            }
                        }

                    } else {
                        serverPlayer.sendSystemMessage(Component.literal("You need 40 levels, 40 lapis, and 40 diamonds to choose a tier 2 class."));
                    }
                    return InteractionResult.SUCCESS;
                }

                // -------- Tier 1 CHOOSE / GIVE TOKENS (player has NO class yet) --------
                if (serverPlayer.experienceLevel >= 40 && countItems(serverPlayer, Items.LAPIS_LAZULI) >= 40) {
                    boolean classItemUsed = false;

                    if (itemInHand.getItem() == ItemRegistry.GLADIATOR_CLASS.get()) {
                        setClassGladiator(serverPlayer);
                        classItemUsed = true;

                    } else if (itemInHand.getItem() == ItemRegistry.KNIGHT_CLASS.get()) {
                        setClassKnight(serverPlayer);
                        classItemUsed = true;

                    } else if (itemInHand.getItem() == ItemRegistry.RANGER_CLASS.get()) {
                        setClassRanger(serverPlayer);
                        classItemUsed = true;

                    } else if (itemInHand.getItem() == ItemRegistry.SNIPER_CLASS.get()) {
                        setClassSniper(serverPlayer);
                        classItemUsed = true;

                    } else if (itemInHand.getItem() == ItemRegistry.THIEF_CLASS.get()) {
                        setClassThief(serverPlayer);
                        classItemUsed = true;

                    } else if (itemInHand.getItem() == ItemRegistry.NINJA_CLASS.get()) {
                        setClassNinja(serverPlayer);
                        classItemUsed = true;

                    } else if (itemInHand.getItem() == ItemRegistry.SAMURAI_CLASS.get()) {
                        setClassSamurai(serverPlayer);
                        classItemUsed = true;
                    }

                    if (classItemUsed) {
                        // record the chosen class in PlayerPersisted
                        CompoundTag root = serverPlayer.getPersistentData();
                        CompoundTag persisted = root.getCompound(Player.PERSISTED_NBT_TAG);
                        persisted.putString("ChosenClass", itemInHand.getItem().toString());
                        root.put(Player.PERSISTED_NBT_TAG, persisted);
                    } else {
                        // Give info + all Tier-1 tokens if not holding a token
                        giveClassDescriptionBook(serverPlayer);
                        serverPlayer.sendSystemMessage(Component.literal("Right-click with a class token to choose your class."));
                        serverPlayer.addItem(new ItemStack(ItemRegistry.GLADIATOR_CLASS.get()));
                        serverPlayer.addItem(new ItemStack(ItemRegistry.KNIGHT_CLASS.get()));
                        serverPlayer.addItem(new ItemStack(ItemRegistry.RANGER_CLASS.get()));
                        serverPlayer.addItem(new ItemStack(ItemRegistry.SNIPER_CLASS.get()));
                        serverPlayer.addItem(new ItemStack(ItemRegistry.THIEF_CLASS.get()));
                        serverPlayer.addItem(new ItemStack(ItemRegistry.NINJA_CLASS.get()));
                        serverPlayer.addItem(new ItemStack(ItemRegistry.SAMURAI_CLASS.get()));
                    }
                } else {
                    serverPlayer.sendSystemMessage(Component.literal("You need 40 levels and 40 lapis to choose a class."));
                }
            }
            return InteractionResult.SUCCESS;
        }

        /** Treat the player as “has a class” only if they actually have a Tier-1 class effect active. */
        private boolean hasAnyTier1Effect(ServerPlayer p) {
            return p.hasEffect(PotionEffectRegistry.THIEF_STRENGTH.get())
                    || p.hasEffect(PotionEffectRegistry.KNIGHT_STRENGTH.get())
                    || p.hasEffect(PotionEffectRegistry.GLADIATOR_STRENGTH.get())
                    || p.hasEffect(PotionEffectRegistry.SAMURAI_STRENGTH.get())
                    || p.hasEffect(PotionEffectRegistry.RANGER_STRENGTH.get())
                    || p.hasEffect(PotionEffectRegistry.SNIPER_STRENGTH.get())
                    || p.hasEffect(PotionEffectRegistry.NINJA_STRENGTH.get());
        }

        private void giveTierTwoClassItems(ServerPlayer player) {
            // Remove required items
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);
            removeItems(player, Items.DIAMOND, 40);

            // Give items for choosing a tier 2 class
            player.addItem(new ItemStack(ItemRegistry.NIGHTWING_CLASS.get()));
            player.addItem(new ItemStack(ItemRegistry.ASSASSIN_CLASS.get()));
            player.addItem(new ItemStack(ItemRegistry.BERSERKER_CLASS.get()));
            player.addItem(new ItemStack(ItemRegistry.PALADIN_CLASS.get()));
            player.addItem(new ItemStack(ItemRegistry.SHOGUN_CLASS.get()));
            player.addItem(new ItemStack(ItemRegistry.ELEMENTAL_RANGER_CLASS.get()));
            player.addItem(new ItemStack(ItemRegistry.MARKSMAN_CLASS.get()));
        }

        private void setClassAssassin(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0); // 20 hearts

            // Increase walking speed
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(ASSASSIN_SPEED_MODIFIER_UUID);
                movementSpeed.addPermanentModifier(new AttributeModifier(ASSASSIN_SPEED_MODIFIER_UUID, "Assassin speed boost", 0.045, AttributeModifier.Operation.ADDITION));
            }

            // Apply custom Assassin effect for increased damage
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.ASSASSIN_STRENGTH.get(), Integer.MAX_VALUE, 1, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Assassin class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);
            removeItems(player, Items.DIAMOND, 40);

            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Assassin");
        }

        private void setClassNightwing(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(44.0); // 20 hearts

            // Increase walking speed
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(NIGHTWING_SPEED_MODIFIER_UUID);
                movementSpeed.addPermanentModifier(new AttributeModifier(NIGHTWING_SPEED_MODIFIER_UUID, "Nightwing speed boost", 0.040, AttributeModifier.Operation.ADDITION));
            }

            // Apply custom Assassin effect for increased damage
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.NIGHTWING_STRENGTH.get(), Integer.MAX_VALUE, 1, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Nightwing class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);
            removeItems(player, Items.DIAMOND, 40);

            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Nightwing");
        }

        private void setClassPaladin(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(80.0); // 40 hearts


            // Apply custom Assassin effect for increased damage
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.PALADIN_STRENGTH.get(), Integer.MAX_VALUE, 1, false, false));

            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Paladin class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);
            removeItems(player, Items.DIAMOND, 40);

            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Paladin");
        }

        private void setClassBerserker(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0); // 15 hearts

            // Increase walking speed
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(BERSERKER_SPEED_MODIFIER_UUID);
                movementSpeed.addPermanentModifier(new AttributeModifier(BERSERKER_SPEED_MODIFIER_UUID, "Berserker speed boost", 0.015, AttributeModifier.Operation.ADDITION));
            }

            // Apply custom Gladiator effect for increased damage
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.BERSERKER_STRENGTH.get(), Integer.MAX_VALUE, 1, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Berserker class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);
            removeItems(player, Items.DIAMOND, 40);

            PlayerDataUtils.setBloodLustLevel(player, 0);  // Set initial Blood Lust level to 0

            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Berserker");
        }

        private void setClassShogun(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(60.0); // 15 hearts

            // Increase walking speed
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(SHOGUN_SPEED_MODIFIER_UUID);
                movementSpeed.addPermanentModifier(new AttributeModifier(SHOGUN_SPEED_MODIFIER_UUID, "Shogun speed boost", 0.010, AttributeModifier.Operation.ADDITION));

                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
            }

            // Apply custom Gladiator effect for increased damage
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.SHOGUN_STRENGTH.get(), Integer.MAX_VALUE, 1, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Shogun class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);
            removeItems(player, Items.DIAMOND, 40);


            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Shogun");
        }

        private void setClassELementalRanger(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(46.0); // 15 hearts

            // Increase walking speed
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(SHOGUN_SPEED_MODIFIER_UUID);
                movementSpeed.addPermanentModifier(new AttributeModifier(SHOGUN_SPEED_MODIFIER_UUID, "Shogun speed boost", 0.025, AttributeModifier.Operation.ADDITION));

            }

            // Apply custom Gladiator effect for increased damage
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.ELEMENTAL_RANGER_STRENGTH.get(), Integer.MAX_VALUE, 1, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Elemental Ranger class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);
            removeItems(player, Items.DIAMOND, 40);


            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Elemental_Ranger");
        }

        private void setClassMarksman(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0); // 15 hearts

            // Increase walking speed
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(MARKSMAN_SPEED_MODIFIER_UUID);
                movementSpeed.addPermanentModifier(new AttributeModifier(MARKSMAN_SPEED_MODIFIER_UUID, "Marksman speed boost", .01, AttributeModifier.Operation.ADDITION));
            }

            // Apply bow damage multiplier
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.MARKSMAN_STRENGTH.get(), Integer.MAX_VALUE, 0, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Marksman class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);
            removeItems(player, Items.DIAMOND, 40);

            // Give longbow blueprint
            giveLongbowBlueprint(player);

            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Marksman");
        }


        private void setClassNinja(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(28.0); // 14 hearts

            // Increase walking speed
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
                movementSpeed.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_UUID, "Ninja speed boost", 0.03, AttributeModifier.Operation.ADDITION));
            }

            // Apply Ninja effect for increased digging speed and sneak speed boost
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.NINJA_STRENGTH.get(), Integer.MAX_VALUE, 0, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Ninja class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);

            // Give the Ninja recipe book
            giveNinjaRecipeBook(player);

            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect

            // Summon Ninja clones

            PlayerDataUtils.setPlayerClass(player, "Ninja");
        }

        private void summonNinjaClones(ServerPlayer player) {
            Level level = player.getCommandSenderWorld(); // Correct method to get the player's level
            for (int i = 0; i < 2; i++) {
                NinjaCloneEntity clone = new NinjaCloneEntity(EntityRegistry.NINJA_CLONE.get(), level);
                clone.setPos(player.getX() + (i * 2), player.getY(), player.getZ() + (i * 2));
                clone.setOwner(player);
                level.addFreshEntity(clone);
                player.sendSystemMessage(Component.literal("Ninja clone summoned at: " + clone.getX() + ", " + clone.getY() + ", " + clone.getZ()));
            }
        }

        private void setClassGladiator(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(30.0); // 15 hearts

            // Increase walking speed
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
                movementSpeed.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_UUID, "Gladiator speed boost", 0.010, AttributeModifier.Operation.ADDITION));
            }

            // Apply custom Gladiator effect for increased damage
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.GLADIATOR_STRENGTH.get(), Integer.MAX_VALUE, 1, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Gladiator class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);

            PlayerDataUtils.setBloodLustLevel(player, 0);  // Set initial Blood Lust level to 0

            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Gladiator");
        }

        private void setClassSamurai(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(32.0); // 15 hearts

            // Apply resistance effect
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));

            // Increase walking speed
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
                movementSpeed.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_UUID, "Samurai speed boost", 0.005, AttributeModifier.Operation.ADDITION));
            }

            // Apply custom Gladiator effect for increased damage
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.SAMURAI_STRENGTH.get(), Integer.MAX_VALUE, 1, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Samurai class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);

            giveSamuraiRecipeBook(player);

            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Samurai");
        }

        private void setClassKnight(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0); // 20 hearts

            // Apply custom Knight effect for increased damage
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.KNIGHT_STRENGTH.get(), Integer.MAX_VALUE, 1, false, false));

            // Apply resistance effect
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Knight class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);

            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Knight");
        }

        private void setClassRanger(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(28.0); // 15 hearts

            // Apply night vision effect
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));

            // Increase walking speed
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
                movementSpeed.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_UUID, "Ranger speed boost", 0.015, AttributeModifier.Operation.ADDITION));
            }

            // Apply the custom potion effect permanently
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.RANGER_STRENGTH.get(), Integer.MAX_VALUE, 0, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Ranger class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);

            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Ranger");
        }

        private void setClassSniper(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(26.0); // 15 hearts

            // Increase walking speed
            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
                movementSpeed.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_UUID, "Sniper speed boost", 0, AttributeModifier.Operation.ADDITION));
            }

            // Apply bow damage multiplier
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.SNIPER_STRENGTH.get(), Integer.MAX_VALUE, 0, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Sniper class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);

            // Give longbow blueprint
            giveLongbowBlueprint(player);

            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Sniper");
        }

        private void setClassThief(ServerPlayer player) {
            player.getAbilities().invulnerable = false;
            player.getAbilities().mayfly = false;

            // Add extra hearts
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(24.0); // 14 hearts

            AttributeInstance movementSpeed = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.removeModifier(SPEED_MODIFIER_UUID);
                movementSpeed.addPermanentModifier(new AttributeModifier(SPEED_MODIFIER_UUID, "Thief speed boost", 0.035, AttributeModifier.Operation.ADDITION));
            }

            // Apply Haste effect for faster digging
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, Integer.MAX_VALUE, 1, false, false));

            // Apply invisibility while sneaking
            player.addEffect(new MobEffectInstance(PotionEffectRegistry.THIEF_STRENGTH.get(), Integer.MAX_VALUE, 0, false, false));

            player.sendSystemMessage(Component.literal("You have chosen the Thief class!"));
            player.giveExperienceLevels(-40);
            removeItems(player, Items.LAPIS_LAZULI, 40);

            giveThiefRecipeBook(player);


            ClassChangeEffectHandler.triggerFadeEffect(); // Trigger the class change effect
            PlayerDataUtils.setPlayerClass(player, "Thief");
        }

        private void giveLongbowBlueprint(ServerPlayer player) {
            ItemStack blueprint = new ItemStack(Items.WRITTEN_BOOK);
            CompoundTag tag = blueprint.getOrCreateTag();
            tag.putString("author", "Server");
            tag.putString("title", "Longbow Blueprint");

            ListTag pages = new ListTag();
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Longbow\n\nTo craft a longbow, use the following materials:\n\n- 3 Sticks\n- 3 Strings\n- 1 Iron Ingot in the middle"))));
            tag.put("pages", pages);
            blueprint.setTag(tag);
            player.addItem(blueprint);
        }
        private void giveNinjaRecipeBook(ServerPlayer player) {
            ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
            CompoundTag tag = book.getOrCreateTag();
            tag.putString("author", "Server");
            tag.putString("title", "Ninja Recipes");

            ListTag pages = new ListTag();
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Ninja Sword Recipe:\n\n Place a stick in the bottom right an ore in the middle and an ore in the top left"))));
            // Add more recipes if needed

            tag.put("pages", pages);
            book.setTag(tag);
            player.addItem(book);
        }
        private void giveThiefRecipeBook(ServerPlayer player) {
            ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
            CompoundTag tag = book.getOrCreateTag();
            tag.putString("author", "Server");
            tag.putString("title", "Thief Recipes");

            ListTag pages = new ListTag();
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Iron Dagger Recipe:\n\n Place a stick in the bottom left and an ore in the middle"))));
            // Add more recipes if needed

            tag.put("pages", pages);
            book.setTag(tag);
            player.addItem(book);
        }
        private void giveSamuraiRecipeBook(ServerPlayer player) {
            ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
            CompoundTag tag = book.getOrCreateTag();
            tag.putString("author", "Server");
            tag.putString("title", "Samurai Recipes");

            ListTag pages = new ListTag();
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Katana Recipe:\n\n Place a stick in the bottom left an ore in the middle and an ore in the top right"))));
            // Add more recipes if needed

            tag.put("pages", pages);
            book.setTag(tag);
            player.addItem(book);
        }



        private int countItems(ServerPlayer player, Item item) {
            int count = 0;
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() == item) {
                    count += stack.getCount();
                }
            }
            return count;
        }

        private void removeItems(ServerPlayer player, Item item, int count) {
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() == item) {
                    int stackCount = stack.getCount();
                    if (stackCount > count) {
                        stack.shrink(count);
                        break;
                    } else {
                        player.getInventory().removeItem(stack);
                        count -= stackCount;
                        if (count <= 0) {
                            break;
                        }
                    }
                }
            }
        }

        private void giveClassDescriptionBook(ServerPlayer player) {
            ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
            CompoundTag tag = book.getOrCreateTag();
            tag.putString("author", "Server");
            tag.putString("title", "Class Descriptions");

            ListTag pages = new ListTag();
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Knight\n\nTier 1\n\nThe knight specializes in defensive abilities but can still attack. Derived from the warrior and precedes the paladin.\n\nSkills:\n- Melee Weapon Proficiency\n- Shield Proficiency\n- Enhanced Durability\n- Enhanced Stamina\n- Enhanced Reflexes"))));
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Gladiator\n\nTier 1\n\nThe gladiator is based on the warrior class, trading defense for offense. Passive ability raises strength, speed, and stamina after killing. Can only wear light to medium armor.\n\nSkills:\n- Melee Weapon Proficiency\n- Heavy Weapon Proficiency\n- Enhanced Strength\n- Enhanced Speed and Reflexes"))));
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Ranger\n\nTier 1\n\nRangers excel in archery and camouflage.\n\nSkills:\n- Archery\n- Enhanced Vision\n- Camouflage\n- Marking\n- Trap Creation"))));
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Sniper\n\nTier 1\n\nSnipers specialize in long-range attacks and precision.\n\nSkills:\n- Long-range Archery\n- Enhanced Vision\n- Camouflage"))));
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Ninja\n\nTier 1\n\nNinjas excel in speed and stealth.\n\nSkills:\n- Enhanced Speed\n- Enhanced Digging\n- Stealth\n- Cloning Techniques"))));
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Samurai\n\nTier 1\n\nSamurai is a warrior class with the perfect mix of offense and defense and uses a special weapon called katana. The Samurai has the ability to use Battle Stance and Defensive Stance as abilities.\n\nSkills:\n- Katana Proficiency\n- Balanced Offense and Defense\n- Battle Stance\n- Defensive Stance"))));
            pages.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal("Thief\n\nTier 1\n\nA thief is a tier 1 class which excels at subterfuge and stealing, able to swipe objects without others noticing. Compared to rogues, they can steal larger items more efficiently.\n\nSkills:\n- Enhanced Speed and Reflexes\n- Burrowing\n- Subterfuge\n- Thievery"))));
            tag.put("pages", pages);
            book.setTag(tag);
            player.addItem(book);
        }
    }
