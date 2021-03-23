package mobtastic.init;

import java.util.List;
import java.util.Set;

import mobtastic.core.Mobtastic;
import mobtastic.core.MobtasticConfig;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Mobtastic.MOD_ID)
public class MobSpawnEvents {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void setSpawns(BiomeLoadingEvent event) {
		RegistryKey<Biome> biome = RegistryKey.getOrCreateKey(ForgeRegistries.Keys.BIOMES, event.getName());
		Set<BiomeDictionary.Type> biomeTypes = BiomeDictionary.getTypes(biome);
		
		List<Spawners> spawners = event.getSpawns().getSpawner(EntityClassification.MONSTER);
		
		if(MobtasticConfig.iceCube.get() && event.getCategory() == Category.ICY || hasType(biomeTypes, Type.SNOWY)) {
			spawners.add(new MobSpawnInfo.Spawners(MobEntities.ICE_CUBE, 20, 1, 1));
		}
		
		if(MobtasticConfig.skeletalKnight.get() && spawners.stream().anyMatch((s) -> s.type == EntityType.SKELETON)) {
			spawners.add(new MobSpawnInfo.Spawners(MobEntities.SKELETAL_KNIGHT, 10, 1, 2));
		}
		
		if(MobtasticConfig.ghost.get() && spawners.stream().anyMatch((s) -> s.type == EntityType.ZOMBIE)) {
			spawners.add(new MobSpawnInfo.Spawners(MobEntities.GHOST, 10, 1, 1));
		}
		
		if(MobtasticConfig.watcher.get() && event.getCategory() == Category.THEEND && biome != Biomes.THE_END) {
			spawners.add(new MobSpawnInfo.Spawners(MobEntities.WATCHER, 1, 1, 2));
		}
		
		if(MobtasticConfig.maw.get() && biome == Biomes.NETHER_WASTES) {
			spawners.add(new MobSpawnInfo.Spawners(MobEntities.MAW, 1, 1, 1));
		}
	}
	
	private static boolean hasType(Set<BiomeDictionary.Type> list, BiomeDictionary.Type...types) {
		for(BiomeDictionary.Type t : types) {
			if(list.contains(t)) return true;
		}
		return false;
	}
}
