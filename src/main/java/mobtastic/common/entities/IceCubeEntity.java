package mobtastic.common.entities;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class IceCubeEntity extends SlimeEntity {

	public IceCubeEntity(EntityType<? extends SlimeEntity> type, World world) {
		super(type, world);
	}
	
	public static AttributeModifierMap.MutableAttribute setAttributes() {
		return MobEntity.func_233666_p_().createMutableAttribute(Attributes.ATTACK_DAMAGE).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2D);
	}

	public static boolean canSpawn(EntityType<IceCubeEntity> type, IWorld world, SpawnReason reason, BlockPos pos, Random rand) {
		if (world.getLightFor(LightType.SKY, pos) > rand.nextInt(32)) {
			return false;
		} else {
			int i = world.getWorldInfo().isThundering() ? world.getNeighborAwareLightSubtracted(pos, 10) : world.getLight(pos);
			return i <= rand.nextInt(8) && world.getDifficulty() != Difficulty.PEACEFUL;
		}
	}
	
	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1,
				new NearestAttackableTargetGoal<>(this, MagmaCubeEntity.class, 10, true, false, (entity) -> {
					return Math.abs(entity.getPosY() - this.getPosY()) <= 4.0D;
				}));
	}
	
	@Override
	protected void setSlimeSize(int size, boolean resetHealth) {
		super.setSlimeSize(size, resetHealth);
		this.getAttribute(Attributes.ARMOR).setBaseValue((double) (size * 3));
	}
	
	@Override
	public void applyEntityCollision(Entity entityIn) {
		super.applyEntityCollision(entityIn);
		if (entityIn instanceof MagmaCubeEntity && this.canDamagePlayer()) {
			this.dealDamage((LivingEntity) entityIn);
		}
	}
	
	@Override
	protected ResourceLocation getLootTable() {
		return this.isSmallSlime() ? LootTables.EMPTY : this.getType().getLootTable();
	}
	
	@Override
	protected IParticleData getSquishParticle() {
		return ParticleTypes.ITEM_SNOWBALL;
	}
	
	@Override
	protected int getJumpDelay() {
		return super.getJumpDelay() * 2;
	}
	
	@Override
	protected void alterSquishAmount() {
		this.squishAmount *= 0.4F;
	}
	
	@Override
	public void livingTick() {
		super.livingTick();
		if (!this.world.isRemote) {
			//temperature damage
	         int i = MathHelper.floor(this.getPosX());
	         int j = MathHelper.floor(this.getPosY());
	         int k = MathHelper.floor(this.getPosZ());
	         if (this.world.getBiome(new BlockPos(i, 0, k)).getTemperature(new BlockPos(i, j, k)) > 1.0F && world.getGameTime() % 20 == 0) {
	            this.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
	            
	            if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this)) {
	                return;
	             }

	            BlockState blockstate = Blocks.WATER.getDefaultState().with(FlowingFluidBlock.LEVEL, 7);
	             for(int l = 0; l < 4; ++l) {
	                i = MathHelper.floor(this.getPosX() + (double)((float)(l % 2 * 2 - 1) * 0.25F));
	                j = MathHelper.floor(this.getPosY());
	                k = MathHelper.floor(this.getPosZ() + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
	                BlockPos blockpos = new BlockPos(i, j, k);
	                if (this.world.isAirBlock(blockpos) && this.world.getBlockState(blockpos.down()).isSolid() && blockstate.isValidPosition(this.world, blockpos)) {
	                   this.world.setBlockState(blockpos, blockstate);
	                }
	             }
	         }
		}
	}
	
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.BLOCK_GLASS_HIT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.BLOCK_GLASS_BREAK;
	}

	@Override
	protected SoundEvent getSquishSound() {
		return SoundEvents.BLOCK_GLASS_STEP;
	}
}
