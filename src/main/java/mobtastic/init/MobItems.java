package mobtastic.init;

import java.util.ArrayList;

import mobtastic.core.Mobtastic;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Mobtastic.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class MobItems {
	
	public static ArrayList<Item> ITEMS = new ArrayList<Item>();

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		createSpawnEgg(MobEntities.ICE_CUBE, 0x9eb8e8, 0xbad0f9);
		event.getRegistry().registerAll(ITEMS.toArray(new Item[0]));
	}
	
	public static Item registerItem(Item item, String name) {
		item.setRegistryName(new ResourceLocation(Mobtastic.MOD_ID, name));
		ITEMS.add(item);
		
		return item;
	}
	
	public static Item createSpawnEgg(EntityType<?> entity, int color1, int color2) {
		return registerItem(new SpawnEggItem(entity, color2, color2, new Item.Properties().group(MobItemGroup.MOBTASTIC_ITEMGROUP)), entity.getRegistryName().getPath() + "_spawn_egg");
	}
}
