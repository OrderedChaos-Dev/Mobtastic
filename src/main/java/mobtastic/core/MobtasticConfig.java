package mobtastic.core;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MobtasticConfig {
	private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	
	public static ForgeConfigSpec COMMON_CONFIG;
	public static ForgeConfigSpec.ConfigValue<Boolean> iceCube;
	public static ForgeConfigSpec.ConfigValue<Boolean> skeletalKnight;
	public static ForgeConfigSpec.ConfigValue<Boolean> ghost;
	public static ForgeConfigSpec.ConfigValue<Boolean> maw;
	public static ForgeConfigSpec.ConfigValue<Boolean> watcher;
	
	static {
		iceCube = COMMON_BUILDER.define("iceCube", true);
		skeletalKnight = COMMON_BUILDER.define("skeletalKnight", true);
		ghost = COMMON_BUILDER.define("ghost", true);
		maw = COMMON_BUILDER.define("maw", true);
		watcher = COMMON_BUILDER.define("watcher", true);
		
		COMMON_CONFIG = COMMON_BUILDER.build();
	}
	
	public static void loadConfig(ForgeConfigSpec spec, Path path) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
		
		configData.load();
		spec.setConfig(configData);
	}
	
}
