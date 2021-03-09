package mobtastic.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class MobItemGroup extends ItemGroup {
	
	public static final MobItemGroup MOBTASTIC_ITEMGROUP = new MobItemGroup("mobtastic");
	public MobItemGroup(String label) {
		super(label);
	}

	@Override
	public ItemStack createIcon() {
		return new ItemStack(Items.APPLE);
	}
}
