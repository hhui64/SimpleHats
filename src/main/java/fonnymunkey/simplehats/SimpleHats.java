package fonnymunkey.simplehats;

import fonnymunkey.simplehats.common.init.HatJson;
import fonnymunkey.simplehats.common.init.ModConfig;
import fonnymunkey.simplehats.common.init.ModRegistry;
import fonnymunkey.simplehats.common.item.HatItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@Mod(SimpleHats.modId)
public class SimpleHats {
    public static final String modId = "simplehats";
    public static Logger logger = LogManager.getLogger();

    public SimpleHats() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.CLIENT, ModConfig.CLIENT_SPEC);
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);

        HatJson.registerHatJson();
        /*
        if(ModConfig.manualAllowUpdateCheck()) {//Resources don't load properly if loaded after configs are actually loaded, so manually do it early
            UUIDHandler.setupUUIDMap();
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> UUIDHandler::checkResourceUpdates);//Only need to download resources on client
        }
        */
        ModRegistry.ITEM_REG.register(eventBus);
        ModRegistry.TAB_REG.register(eventBus);
        ModRegistry.ENTITY_REG.register(eventBus);
        ModRegistry.RECIPE_REG.register(eventBus);
        ModRegistry.LOOT_REG.register(eventBus);
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        CuriosApi.registerCurioPredicate(new ResourceLocation(SimpleHats.modId, "validator"), slotResult -> {
            if (slotResult.stack().getItem() instanceof HatItem) {
                return true;
            }

            // TODO: fallback to other predicates, not just the default
            return slotResult.stack().is(TagKey.create(Registries.ITEM, new ResourceLocation("curios", "head")));
        });
    }

    public void clientSetup(final FMLClientSetupEvent event) {
        for(Item hat : ModRegistry.hatList) {
            if(hat instanceof ICurioRenderer renderer) {
                CuriosRendererRegistry.register(hat, () -> renderer);
            }
        }
        CuriosRendererRegistry.register((Item)ModRegistry.HATSPECIAL.get(), () -> (ICurioRenderer)ModRegistry.HATSPECIAL.get());
    }
}