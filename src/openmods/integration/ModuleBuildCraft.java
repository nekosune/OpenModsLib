package openmods.integration;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import buildcraft.api.transport.IPipeTile;

public class ModuleBuildCraft {

	public int tryAcceptIntoPipe(TileEntity possiblePipe, ItemStack nextStack, boolean doInsert, ForgeDirection direction) {
		return 0;
	}

	public int tryAcceptIntoPipe(TileEntity possiblePipe, FluidStack nextStack, ForgeDirection direction) {
		return 0;
	}

	public boolean isPipe(TileEntity tile) {
		return false;
	}

	static class ModuleBuildCraftLive extends ModuleBuildCraft {

		@Override
		public int tryAcceptIntoPipe(TileEntity possiblePipe, ItemStack nextStack, boolean doInsert, ForgeDirection direction) {
			if (possiblePipe instanceof IPipeTile) { return ((IPipeTile)possiblePipe).injectItem(nextStack, doInsert, direction.getOpposite()); }
			return 0;
		}

		@Override
		public int tryAcceptIntoPipe(TileEntity possiblePipe, FluidStack nextStack, ForgeDirection direction) {
			if (possiblePipe instanceof IPipeTile) { return ((IPipeTile)possiblePipe).fill(direction.getOpposite(), nextStack, true); }
			return 0;
		}

		@Override
		public boolean isPipe(TileEntity tile) {
			return tile instanceof IPipeTile;
		}
	}
}
