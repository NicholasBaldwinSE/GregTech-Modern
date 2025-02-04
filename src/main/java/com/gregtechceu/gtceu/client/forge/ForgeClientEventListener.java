package com.gregtechceu.gtceu.client.forge;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.client.TooltipHelper;
import com.gregtechceu.gtceu.client.TooltipsHandler;
import com.gregtechceu.gtceu.client.renderer.BlockHighLightRenderer;
import com.gregtechceu.gtceu.client.renderer.MultiblockInWorldPreviewRenderer;
import com.gregtechceu.gtceu.common.data.GTBedrockFluids;
import com.gregtechceu.gtceu.common.data.GTOres;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author KilaBash
 * @date 2022/8/27
 * @implNote ForgeCommonEventListener
 */
@Mod.EventBusSubscriber(modid = GTCEu.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ForgeClientEventListener {

    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            // to render the preview after block entities, before the translucent. so it can be seen through the
            // transparent blocks.
            MultiblockInWorldPreviewRenderer.renderInWorldPreview(event.getPoseStack(), event.getCamera(),
                    event.getPartialTick());
        }
    }

    @SubscribeEvent
    public static void onBlockHighlightEvent(RenderHighlightEvent.Block event) {
        BlockHighLightRenderer.renderBlockHighLight(event.getPoseStack(), event.getCamera(), event.getTarget(),
                event.getMultiBufferSource(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        TooltipsHandler.appendTooltips(event.getItemStack(), event.getFlags(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onClientTickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            TooltipHelper.onClientTick();
            MultiblockInWorldPreviewRenderer.onClientTick();
            GTValues.CLIENT_TIME++;
        }
    }

    @SubscribeEvent
    public static void onClientLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        if (GTRegistries.ORE_VEINS.isFrozen()) {
            GTRegistries.ORE_VEINS.unfreeze();
        }
        GTOres.init();
        if (!GTRegistries.ORE_VEINS.isFrozen()) {
            GTRegistries.ORE_VEINS.freeze();
        }

        if (GTRegistries.BEDROCK_FLUID_DEFINITIONS.isFrozen()) {
            GTRegistries.BEDROCK_FLUID_DEFINITIONS.unfreeze();
        }
        GTBedrockFluids.init();
        if (!GTRegistries.BEDROCK_FLUID_DEFINITIONS.isFrozen()) {
            GTRegistries.BEDROCK_FLUID_DEFINITIONS.freeze();
        }

        if (GTRegistries.BEDROCK_ORE_DEFINITIONS.isFrozen()) {
            GTRegistries.BEDROCK_ORE_DEFINITIONS.unfreeze();
        }
        GTOres.toReRegisterBedrock.forEach(GTRegistries.BEDROCK_ORE_DEFINITIONS::registerOrOverride);
        if (!GTRegistries.BEDROCK_ORE_DEFINITIONS.isFrozen()) {
            GTRegistries.BEDROCK_ORE_DEFINITIONS.freeze();
        }
    }
}
