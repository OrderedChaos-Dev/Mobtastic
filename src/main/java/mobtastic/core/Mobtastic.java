package mobtastic.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import mobtastic.client.EntityRendering;
import mobtastic.init.MobEntities;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(Mobtastic.MOD_ID)
public class Mobtastic
{
	public static final String MOD_ID = "mobtastic";
    private static final Logger LOGGER = LogManager.getLogger();

    public Mobtastic() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        MobtasticConfig.loadConfig(MobtasticConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("mobtastic-common.toml"));
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    	EntityRendering.registerRenderers();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
    	event.enqueueWork(() -> {
    		MobEntities.registerDungeonEntities();
    	});
    }

    private void processIMC(final InterModProcessEvent event) {

    }
}