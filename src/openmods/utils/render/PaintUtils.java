package openmods.utils.render;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import openmods.Mods;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class PaintUtils {

	private Set<Block> allowed;

	public static final PaintUtils instance = new PaintUtils();

	protected PaintUtils() {
		allowed = new HashSet<Block>();
		allowed.add(Blocks.stone);
		allowed.add(Blocks.cobblestone);
		allowed.add(Blocks.mossy_cobblestone);
		allowed.add(Blocks.sandstone);
		allowed.add(Blocks.iron_block);
		allowed.add(Blocks.stonebrick);
		allowed.add(Blocks.glass);
		allowed.add(Blocks.planks);
		if (Loader.isModLoaded(Mods.TINKERSCONSTRUCT)) {
			addBlocksForMod(Mods.TINKERSCONSTRUCT, new String[] {
					"GlassBlock",
					"decoration.multibrick",
					"decoration.multibrickfancy"
			});
		}
		if (Loader.isModLoaded(Mods.EXTRAUTILITIES)) {
			addBlocksForMod(Mods.EXTRAUTILITIES, new String[] {
					"greenScreen",
					"extrautils:decor"
			});
		}
		if (Loader.isModLoaded(Mods.BIOMESOPLENTY)) {
			addBlocksForMod(Mods.BIOMESOPLENTY, new String[] {
					"bop.planks"
			});
		}
	}

	protected void addBlocksForMod(String modId, String[] blocks) {
		for (String blockName : blocks) {
			Block block = GameRegistry.findBlock(modId, blockName);
			if (block != null) {
				allowed.add(block);
			}
		}
	}

	public boolean isAllowedToReplace(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == null || b.canProvidePower() || b.isAir(world, x, y, z)) return false;
		return allowed.contains(b);
	}
}
