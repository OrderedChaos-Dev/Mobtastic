package mobtastic.init;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import mobtastic.core.Mobtastic;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Mobtastic.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class MobItems {
	
	public static ArrayList<Item> ITEMS = new ArrayList<Item>();
	
	public static Item maw_tongue = registerItem(new Item(new Item.Properties().group(MobItemGroup.MOBTASTIC_ITEMGROUP)) {
		   @OnlyIn(Dist.CLIENT)
		   @Override
		   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
			   tooltip.add(new TranslationTextComponent("item.mobtastic.maw_tongue.description"));
		   }
	}, "maw_tongue");

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		createSpawnEgg(MobEntities.ICE_CUBE, 0x9eb8e8, 0xbad0f9);
		createSpawnEgg(MobEntities.SKELETAL_KNIGHT, 0xa6a6a6, 0x808080);
		createSpawnEgg(MobEntities.WATCHER, 0x9e9a99, 0x262625);
		createSpawnEgg(MobEntities.MAW, 0x909090, 0xFF0000);
		createSpawnEgg(MobEntities.GHOST, 0x959595, 0xffffff);
		
		ITEMS.forEach((item) -> event.getRegistry().registerAll(item));
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
