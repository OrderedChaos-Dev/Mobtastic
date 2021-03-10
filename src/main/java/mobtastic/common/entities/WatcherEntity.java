package mobtastic.common.entities;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WatcherEntity extends MonsterEntity {
	
	private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(WatcherEntity.class, DataSerializers.VARINT);

	public WatcherEntity(EntityType<? extends MonsterEntity> type, World worldIn) {
		super(type, worldIn);
	}
	
	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(TARGET_ENTITY, 0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new LookAtGoal(this, PlayerEntity.class, 100.0F));
		this.goalSelector.addGoal(1, new WatcherAttackGoal(this));
		this.goalSelector.addGoal(2, new LookRandomlyGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
	}
	
	public static AttributeModifierMap.MutableAttribute setAttributes() {
		return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MAX_HEALTH, 20.0D).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.FOLLOW_RANGE, 100.0D);
	}
	
	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.UNDEFINED;
	}
	
	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize size) {
		return size.height * 0.4F;
	}
	
	@Override
	public void updateFallState(double y, boolean onGround, BlockState state, BlockPos pos) {}
	
	@Override
	public void livingTick() {
		super.livingTick();
		this.setMotion(this.getMotion().mul(1, 0, 1));
		
		if(this.hasTargetedEntity() && this.getTargetedEntity() != null && this.isEntityInRange(this.getAttackTarget(), 30.0D) && this.world.getGameTime() % 10 == 0) {
			BlockPos pos1 = this.getAttackTarget().getPosition().add(0, this.getAttackTarget().getHeight() / 2.0, 0);
			BlockPos pos2 = this.getPosition().add(0, this.getEyeHeight(), 0);

			double xDiff = ((pos1.getX() - pos2.getX()) + ((rand.nextDouble() - rand.nextDouble()) * 0.7)) / 30.0;
			double yDiff = ((pos1.getY() - pos2.getY()) + ((rand.nextDouble() - rand.nextDouble()) * 0.5)) / 30.0 ;
			double zDiff = ((pos1.getZ() - pos2.getZ()) + ((rand.nextDouble() - rand.nextDouble()) * 0.7)) / 30.0;
			for (int i = 0; i <= 30.0; i++) {
				double xCoord = pos2.getX() + (xDiff * i);
				double yCoord = pos2.getY() + (yDiff * i) + 0.5;
				double zCoord = pos2.getZ() + (zDiff * i);
				this.getEntityWorld().addParticle(ParticleTypes.CRIT, true, xCoord, yCoord, zCoord, 0, 0, 0);
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}
	
	public void setTargetedEntity(int entityId) {
		this.dataManager.set(TARGET_ENTITY, entityId);
	}

	public boolean hasTargetedEntity() {
		return this.dataManager.get(TARGET_ENTITY) != 0;
	}
	
	@Nullable
	public LivingEntity getTargetedEntity() {
		if (!this.hasTargetedEntity()) {
			return null;
		} else if (this.world.isRemote) {
			if (this.getAttackTarget() != null) {
				return this.getAttackTarget();
			} else {
				Entity entity = this.world.getEntityByID(this.dataManager.get(TARGET_ENTITY));
				if (entity instanceof LivingEntity) {
					this.setAttackTarget((LivingEntity) entity);
					return this.getAttackTarget();
				} else {
					return null;
				}
			}
		} else {
			return this.getAttackTarget();
		}
	}
	
	@Override
	@Nullable
	public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		if(reason == SpawnReason.NATURAL) {
			BlockPos pos = this.getPosition();
			int height = world.getRandom().nextInt(30) + 1;
			for(int i = height; i > 0; i--) {
				if(world.isAirBlock(pos.up(i))) {
					this.setPosition(pos.getX(), pos.getY() + i, pos.getZ());
					break;
				}
			}
		}
		return super.onInitialSpawn(world, difficultyIn, reason, spawnDataIn, dataTag);
	}
	
	static class WatcherAttackGoal extends Goal {
		
		private final WatcherEntity watcher;
		private int attackCounter;
		
		public WatcherAttackGoal(WatcherEntity watcher) {
			this.watcher = watcher;
		}

		@Override
		public boolean shouldExecute() {
			return this.watcher.getAttackTarget() != null && this.watcher.getAttackTarget().isAlive();
		}
		
		@Override
		public boolean shouldContinueExecuting() {
			return super.shouldContinueExecuting() && this.watcher.getDistanceSq(this.watcher.getAttackTarget()) < 900.0D && this.watcher.canEntityBeSeen(this.watcher.getAttackTarget());
		}
		
		@Override
		public void startExecuting() {
			this.attackCounter = 0;
			this.watcher.getLookController().setLookPositionWithEntity(this.watcher.getAttackTarget(), 90.0F, 90.0F);
		}

		@Override
		public void resetTask() {
			this.attackCounter = 0;
			this.watcher.setTargetedEntity(0);
			this.watcher.setAttackTarget(null);
		}

		@Override
		public void tick() {
			LivingEntity target = this.watcher.getAttackTarget();
			this.watcher.getLookController().setLookPositionWithEntity(target, 90.0F, 90.0F);
			if(this.attackCounter == 40) {
				this.watcher.attackEntityAsMob(target);
				this.attackCounter = 0;
			} else {
				this.watcher.setTargetedEntity(target.getEntityId());
				this.attackCounter++;
			}
			
			super.tick();
		}
	}
}
