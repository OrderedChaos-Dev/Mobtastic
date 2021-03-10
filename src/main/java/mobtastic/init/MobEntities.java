package mobtastic.init;

import java.util.ArrayList;
import java.util.List;

import mobtastic.common.entities.IceCubeEntity;
import mobtastic.common.entities.SkeletalKnightEntity;
import mobtastic.common.entities.WatcherEntity;
import mobtastic.core.Mobtastic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Mobtastic.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class MobEntities {
	public static final List<EntityType<?>> ENTITIES = new ArrayList<EntityType<?>>();
	public static final EntityType<IceCubeEntity> ICE_CUBE = registerEntity(EntityType.Builder.create(IceCubeEntity::new, EntityClassification.MONSTER).size(2.0F, 2.0F), "ice_cube");
	public static final EntityType<SkeletalKnightEntity> SKELETAL_KNIGHT = registerEntity(EntityType.Builder.create(SkeletalKnightEntity::new, EntityClassification.MONSTER).size(0.6F, 1.99F), "skeletal_knight");
	public static final EntityType<WatcherEntity> WATCHER = registerEntity(EntityType.Builder.create(WatcherEntity::new, EntityClassification.MONSTER).size(1.0F, 1.0F), "watcher");
	
	public static <T extends Entity> EntityType<T> registerEntity(EntityType.Builder<?> builder, String name) {
		EntityType<T> entity = (EntityType<T>) builder.build(name).setRegistryName(new ResourceLocation(Mobtastic.MOD_ID, name));
		ENTITIES.add(entity);
		return entity;
	}
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		ENTITIES.forEach((e) -> event.getRegistry().register(e));
		registerDungeonEntities();
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(ICE_CUBE, IceCubeEntity.setAttributes().create());
		event.put(SKELETAL_KNIGHT, AbstractSkeletonEntity.registerAttributes().create());
		event.put(WATCHER, WatcherEntity.setAttributes().create());
	}
	
	public static void registerDungeonEntities() {
		DungeonHooks.addDungeonMob(SKELETAL_KNIGHT, 50);
	}
}
