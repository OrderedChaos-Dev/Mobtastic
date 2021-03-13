package mobtastic.common.entities;

import java.util.Random;

import javax.annotation.Nullable;

import mobtastic.init.MobItems;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class MawEntity extends MonsterEntity {

	public static final DataParameter<Direction> ATTACHED_FACE = EntityDataManager.createKey(MawEntity.class,
			DataSerializers.DIRECTION);
	public static final DataParameter<Boolean> FRIENDLY = EntityDataManager.createKey(MawEntity.class,
			DataSerializers.BOOLEAN);

	public MawEntity(EntityType<? extends MawEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(2, new MawAttackGoal(this));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 4, true, true, (entity) -> {
			return !(entity instanceof CreeperEntity) && !(entity instanceof PlayerEntity) && !(entity instanceof MawEntity);
		}));
	}
	
	public static AttributeModifierMap.MutableAttribute setAttributes() {
		return MonsterEntity.func_234295_eP_()
				.createMutableAttribute(Attributes.MAX_HEALTH, 40.0D)
				.createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0D)
				.createMutableAttribute(Attributes.FOLLOW_RANGE, 2.0D)
				.createMutableAttribute(Attributes.MOVEMENT_SPEED, 0)
				.createMutableAttribute(Attributes.ARMOR, 1.5D);
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.dataManager.register(ATTACHED_FACE, Direction.DOWN);
		this.dataManager.register(FRIENDLY, false);
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.dataManager.set(ATTACHED_FACE, Direction.byIndex(compound.getByte("AttachFace")));
		this.dataManager.set(FRIENDLY, compound.getBoolean("Friendly"));
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("AttachFace", (byte) this.dataManager.get(ATTACHED_FACE).getIndex());
		compound.putBoolean("Friendly", this.dataManager.get(FRIENDLY));
	}
	
	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
		if(this.getAttachmentFace() == Direction.DOWN)
			return 0.5F;
		else
			return this.getHeight() - 0.5F;
	}

	@Nullable
	@Override
	public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData spawnData, @Nullable CompoundNBT dataTag) {
		if(reason == SpawnReason.NATURAL) {
			setAttachmentFace(world.getRandom().nextBoolean() ? Direction.UP : Direction.DOWN);

			if (getAttachmentFace() == Direction.UP) {
				BlockPos pos = this.getPosition();
				while (pos.up(5).getY() < 255 && world.isAirBlock(pos.up(5))) {
					pos = pos.up();
				}
				this.setPosition(pos.getX(), pos.getY(), pos.getZ());
			} else {
				BlockPos pos = this.getPosition();
				while (pos.down(5).getY() > 0 && world.isAirBlock(pos.down(5))) {
					pos = pos.down();
				}
				this.setPosition(pos.getX(), pos.getY(), pos.getZ());
			}
		}
		if(reason != SpawnReason.BREEDING) {
			this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
		}
		return super.onInitialSpawn(world, difficulty, reason, spawnData, dataTag);
	}

	public Direction getAttachmentFace() {
		return this.dataManager.get(ATTACHED_FACE);
	}

	public void setAttachmentFace(Direction d) {
		this.dataManager.set(ATTACHED_FACE, d);
	}
	
	@Override
	public boolean canDespawn(double distanceToClosestPlayer) {
		return !this.dataManager.get(FRIENDLY);
	}

	@Override
	public boolean preventDespawn() {
		return this.dataManager.get(FRIENDLY);
	}
	
	@Override
	protected boolean isDespawnPeaceful() {
		return !this.dataManager.get(FRIENDLY);
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entity) {
		float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
		if (entity instanceof LivingEntity) {
			f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(),
					((LivingEntity) entity).getCreatureAttribute());
			
			double i = 0.1;
			if(this.getAttachmentFace() == Direction.DOWN)
				i *= -1;
			Vector3d vec3d = new Vector3d(0, i, 0);
			entity.setMotion(vec3d.normalize());
		}

		boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), f);
		if (flag) {
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerentity = (PlayerEntity) entity;
				this.func_233655_a_(playerentity, this.getHeldItemMainhand(),
						playerentity.isHandActive() ? playerentity.getActiveItemStack() : ItemStack.EMPTY);
			}

			this.applyEnchantments(this, entity);
			this.setLastAttackedEntity(entity);
		}

		return flag;
	}
	
	private void func_233655_a_(PlayerEntity player, ItemStack itemstack, ItemStack itemstack2) {
		if (!itemstack.isEmpty() && !itemstack2.isEmpty() && itemstack.getItem() instanceof AxeItem
				&& itemstack2.getItem() == Items.SHIELD) {
			float f = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
			if (this.rand.nextFloat() < f) {
				player.getCooldownTracker().setCooldown(Items.SHIELD, 100);
				this.world.setEntityState(player, (byte) 30);
			}
		}

	}
	
	@Override
	public void func_241847_a(ServerWorld world, LivingEntity entityLivingIn) {
		this.addPotionEffect(new EffectInstance(Effects.REGENERATION, 200, 2));
	}

	@Override
	public void livingTick() {
		super.livingTick();
		this.setMotion(Vector3d.ZERO);

		Direction direction = getAttachmentFace();
		BlockPos pos = this.getPosition().offset(direction.getOpposite());
		if (world.isAirBlock(pos)) {
			if (direction == Direction.DOWN)
				this.setMotion(0, -0.1F, 0);
			else
				this.setMotion(0, 0.1F, 0);
		}
		
		if(this.getDataManager().get(FRIENDLY)) {
			if(this.rand.nextFloat() < 0.2F) {
				if(this.rand.nextFloat() < 0.1F) {
					this.world.addParticle(ParticleTypes.HEART, this.getPosXRandom(0.5D), this.getPosYRandom(), this.getPosZRandom(0.5D), 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}
	
	@Override
	public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
		Item item = player.getHeldItem(hand).getItem();
		if(this.dataManager.get(FRIENDLY)) {
			if(item == Items.GOLD_NUGGET) {
				if(!this.world.isRemote) {
					ItemStack stack = new ItemStack(MobItems.maw_tongue);
					if(this.hasCustomName()) {
						stack.setDisplayName(this.getCustomName());
					}
		            ItemEntity itementity = new ItemEntity(world, this.getPosX(), this.getPosY(), this.getPosZ(), stack);
		            itementity.setDefaultPickupDelay();
		            world.addEntity(itementity);
		            this.remove();
		            return ActionResultType.SUCCESS;
				}
			}
			if(item == Items.ROTTEN_FLESH) {
				this.heal(5);
				player.getHeldItem(hand).shrink(1);
				
				for(int i = 0; i < 5; i++) {
					double offset = rand.nextFloat() * 0.5;
					this.world.addParticle(ParticleTypes.HEART, this.getPosXRandom(offset), this.getPosYRandom(), this.getPosZRandom(offset), 0.0D, 0.0D, 0.0D);
				}
				return ActionResultType.SUCCESS;
			}
		}

		return super.func_230254_b_(player, hand);
	}

	public static boolean canSpawn(EntityType<MawEntity> entity, IWorld world, SpawnReason reason, BlockPos pos, Random rand) {
		return world.getDifficulty() != Difficulty.PEACEFUL;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void applyEntityCollision(Entity entity) {}

	@Override
	public void applyKnockback(float strength, double xRatio, double zRatio) {}

	@Override
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.UNDEFINED;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}
	
	static class MawAttackGoal extends Goal {
		private final MawEntity maw;
		
		public MawAttackGoal(MawEntity maw) {
			this.maw = maw;
		}

		@Override
		public boolean shouldExecute() {
			return this.maw.getAttackTarget() != null && this.maw.getAttackTarget().isAlive();
		}
		
		@Override
		public void startExecuting() {

		}

		@Override
		public void resetTask() {

		}

		@Override
		public void tick() {
			LivingEntity target = this.maw.getAttackTarget();
			double i = this.maw.getHeight();
			BlockPos pos = this.maw.getPosition().add(0, i / 2.0, 0);
			target.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
			target.setMotion(0, 0, 0);
			target.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 20, 10));
			if(this.maw.getAttachmentFace() == Direction.UP) {
				target.addPotionEffect(new EffectInstance(Effects.SLOW_FALLING, 20, 10));
				target.addPotionEffect(new EffectInstance(Effects.LEVITATION, 10, 1));
			}
			super.tick();
		}
	}
}