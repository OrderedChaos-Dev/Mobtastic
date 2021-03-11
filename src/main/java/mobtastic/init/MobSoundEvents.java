package mobtastic.init;

import java.util.ArrayList;

import mobtastic.core.Mobtastic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Mobtastic.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class MobSoundEvents {

	public static ArrayList<SoundEvent> SOUNDS = new ArrayList<SoundEvent>();
	
	public static SoundEvent entity_ghost_ambient = register("entity.ghost.ambient");
	public static SoundEvent entity_ghost_hurt = register("entity.ghost.hurt");
	public static SoundEvent entity_ghost_death = register("entity.ghost.death");
	
	public static SoundEvent register(String name) {
		ResourceLocation resource = new ResourceLocation(Mobtastic.MOD_ID, name);
		SoundEvent sound = new SoundEvent(resource).setRegistryName(resource);
		return sound;
	}
	
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		SOUNDS.forEach((sound) -> event.getRegistry().registerAll(sound));
	}
	
}
