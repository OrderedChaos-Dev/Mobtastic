package mobtastic.init;

import java.util.List;
import java.util.Objects;

import mobtastic.common.entities.IceCubeEntity;
import mobtastic.common.entities.MawEntity;
import mobtastic.core.Mobtastic;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Mobtastic.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
public class MobEvents {
	
	@SubscribeEvent
	public static void updateAI(LivingSpawnEvent event) {
		if(event.getEntityLiving() instanceof MagmaCubeEntity) {
			MagmaCubeEntity magmaCube = (MagmaCubeEntity)event.getEntityLiving();
			magmaCube.targetSelector.addGoal(1,
					new NearestAttackableTargetGoal<>(magmaCube, IceCubeEntity.class, 10, true, false, (entity) -> {
						return Math.abs(entity.getPosY() - event.getEntityLiving().getPosY()) <= 4.0D;
					}));
		}
	}
	
	@SubscribeEvent
	public static void attack(LivingUpdateEvent event) {
		if(event.getEntityLiving() instanceof MagmaCubeEntity) {
			MagmaCubeEntity magmaCube = (MagmaCubeEntity)event.getEntityLiving();
			World world = event.getEntityLiving().getEntityWorld();
			List<Entity> neighbors = world.getEntitiesWithinAABBExcludingEntity(magmaCube, magmaCube.getBoundingBox());
			for(Entity entity : neighbors) {
				if(entity instanceof IceCubeEntity) {
					if(magmaCube.getAttackTarget() == entity) {
						magmaCube.attackEntityAsMob(entity);
						break;
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void interact(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();
		Item item = stack.getItem();
		World world = event.getWorld();
		Direction direction = event.getFace();
		BlockPos pos = event.getPos();
		BlockPos posWithOffset = pos.offset(direction);
		PlayerEntity player = event.getPlayer();
		
		Block block = world.getBlockState(pos).getBlock();
		if(!event.getWorld().isRemote) {
			if(block == Blocks.BONE_BLOCK) {
				if(item == MobItems.maw_tongue) {
					EntityType<MawEntity> mawType = MobEntities.MAW;
					
					if(direction == Direction.UP || direction == Direction.DOWN) {
						MawEntity maw = (MawEntity) mawType.spawn((ServerWorld) world, stack, player, direction == Direction.DOWN ? pos.down(4) : pos, SpawnReason.BREEDING, true, !Objects.equals(pos, posWithOffset));
						if (maw != null) {
							stack.shrink(1);
							maw.getDataManager().set(MawEntity.FRIENDLY, true);
							maw.getDataManager().set(MawEntity.ATTACHED_FACE, direction.getOpposite());
							maw.targetSelector.removeGoal(new NearestAttackableTargetGoal<>(maw, PlayerEntity.class, true));
							if(stack.hasDisplayName()) {
								maw.setCustomName(stack.getDisplayName());
							}
							world.removeBlock(pos, false);
						}
					}
				}
			}
		}
	}

}
