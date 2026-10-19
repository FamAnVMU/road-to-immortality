package com.tutien.common.npc;

import com.tutien.common.registry.ModEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TuTienNpcEntity extends PathfinderMob implements MenuProvider {
    private static final EntityDataAccessor<Integer> ROLE_ID =
            SynchedEntityData.defineId(TuTienNpcEntity.class, EntityDataSerializers.INT);

    public TuTienNpcEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.setCustomNameVisible(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ROLE_ID, 0);
    }

    public TuTienNpcRole getNpcRole() {
        int id = this.entityData.get(ROLE_ID);
        TuTienNpcRole[] values = TuTienNpcRole.values();
        if (id < 0 || id >= values.length) return TuTienNpcRole.DAN_DUOC;
        return values[id];
    }

    public void setNpcRole(TuTienNpcRole role) {
        this.entityData.set(ROLE_ID, role.ordinal());
        this.setCustomName(Component.literal("§6").append(role.getTitleComponent()));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // NPCs are invulnerable peaceful beings
        return false;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide() && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(this, buf -> {
                buf.writeVarInt(this.getId());
                buf.writeVarInt(this.entityData.get(ROLE_ID));
            });
            return InteractionResult.CONSUME;
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("§6").append(getNpcRole().getTitleComponent());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new NpcTradeMenu(containerId, playerInventory, this.getId(), getNpcRole());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("RoleId", this.entityData.get(ROLE_ID));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("RoleId")) {
            setNpcRole(TuTienNpcRole.values()[compound.getInt("RoleId")]);
        }
    }
}
