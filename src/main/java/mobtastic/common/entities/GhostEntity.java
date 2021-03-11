package mobtastic.common.entities;

import javax.annotation.Nullable;

import mobtastic.init.MobSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.FleeSunGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class GhostEntity extends MonsterEntity {
	
	private static final DataParameter<Integer> VANISHING = EntityDataManager.createKey(GhostEntity.class, DataSerializers.VARINT);
	private int vanishTime;

	public GhostEntity(EntityType<? extends GhostEntity> entityType, World world) {
		super(entityType, world);
	}
	
	@Override
	public void registerGoals() {
		this.goalSelector.addGoal(2, new RestrictSunGoal(this));
		this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(4, new LightAvoidingRandomWalkingGoal(this, 1.0D));
		this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
		this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
	}
	
	public static AttributeModifierMap.MutableAttribute setAttributes() {
		return MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.23F).createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0D).createMutableAttribute(Attributes.ARMOR, 1.0D);
	}
	
	@Override
	public void livingTick() {
		if (this.isAlive()) {
			if (this.isInDaylight()) {
				this.setFire(8);
			}
		}
		
		Vector3d vec3d = this.getMotion();
		if (!this.onGround && vec3d.y < 0.0D) {
			this.setMotion(vec3d.mul(1.0D, 0.6D, 1.0D));
		}
		
		int light = this.getEntityWorld().getLight(this.getPosition());
		if(light > 9) {
			vanishTime++;
		} else {
			if(vanishTime > 0)
				vanishTime--;
		}
		
		if(vanishTime > 400) {
			for(int i = 0; i < 20; i++) {
				this.world.addParticle(ParticleTypes.SMOKE, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), 0.0D, 0.0D, 0.0D);
			}
			this.remove();
		}
		
		if(this.world.isRemote && light > 9) {
			if(vanishTime > 0) {
				if(this.rand.nextFloat() < vanishTime / 400.0F) {
					this.world.addParticle(ParticleTypes.SMOKE, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), 0.0D, 0.0D, 0.0D);
				}
			}
		}

		super.livingTick();
	}
	
	@Override
	public void updateFallState(double y, boolean onGround, BlockState state, BlockPos pos) {}
	
	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.UNDEAD;
	}
	
	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(VANISHING, 0);
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putInt("vanishing", this.getVanishTime());
	}
	
	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.setVanishTime(compound.getInt("vanishing"));
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return MobSoundEvents.entity_ghost_ambient;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return MobSoundEvents.entity_ghost_hurt;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MobSoundEvents.entity_ghost_death;
	}
	
	public int getVanishTime() {
		return vanishTime;
	}
	
	public void setVanishTime(int i) {
		vanishTime = i;
	}
	
	static class LightAvoidingRandomWalkingGoal extends RandomWalkingGoal {
		protected final float probability;

		public LightAvoidingRandomWalkingGoal(CreatureEntity creature, double speedIn) {
			this(creature, speedIn, 0.001F);
		}

		public LightAvoidingRandomWalkingGoal(CreatureEntity creature, double speedIn, float probabilityIn) {
			super(creature, speedIn);
			this.probability = probabilityIn;
		}

		@Nullable
		@Override
		protected Vector3d getPosition() {
			int light = this.creature.getEntityWorld().getLight(this.creature.getPosition());
			if(light > 6) {
				Vector3d vec3d = RandomPositionGenerator.getLandPos(this.creature, 15, 7);
				return vec3d == null ? super.getPosition() : vec3d;
			}else if (this.creature.isInWaterOrBubbleColumn()) {
				Vector3d vec3d = RandomPositionGenerator.getLandPos(this.creature, 15, 7);
				return vec3d == null ? super.getPosition() : vec3d;
			} else {
				return this.creature.getRNG().nextFloat() >= this.probability
						? RandomPositionGenerator.getLandPos(this.creature, 10, 7)
						: super.getPosition();
			}
		}
		
		@Override
		public boolean shouldExecute() {
			int light = this.creature.getEntityWorld().getLight(this.creature.getPosition());
			return light > 6 || super.shouldExecute();
		}
	}
}
