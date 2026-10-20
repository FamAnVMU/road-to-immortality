package com.tutien.client.renderer;

import com.tutien.TuTienMod;
import com.tutien.common.npc.TuTienNpcEntity;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

public class TuTienNpcRenderer extends HumanoidMobRenderer<TuTienNpcEntity, PlayerModel<TuTienNpcEntity>> {

    public TuTienNpcRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(TuTienNpcEntity entity) {
        // Return default player skin (Alex/Steve etc) by UUID
        return DefaultPlayerSkin.get(entity.getUUID()).texture();
    }
}
