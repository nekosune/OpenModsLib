package openmods.liquids;

import java.util.*;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import openmods.Log;
import openmods.integration.Integration;
import openmods.proxy.IOpenModsProxy;
import openmods.sync.SyncableFlags;
import openmods.utils.BlockUtils;

// TODO: Move the getTicks and other helpers to a generic helpers class (:

public class GenericTank extends FluidTank {

	protected List<ForgeDirection> surroundingTanks = new ArrayList<ForgeDirection>();

	protected FluidStack[] acceptableFluids;

	public GenericTank(int capacity, FluidStack... acceptableFluids) {
		super(capacity);
		this.acceptableFluids = acceptableFluids;
	}

	public void refreshSurroundingTanks(TileEntity currentTile, SyncableFlags sides) {
		HashSet<ForgeDirection> checkSides = new HashSet<ForgeDirection>();
		if (sides == null) {
			checkSides.addAll(Arrays.asList(ForgeDirection.VALID_DIRECTIONS));
		} else {
			for (Integer s : sides.getActiveSlots()) {
				checkSides.add(ForgeDirection.getOrientation(s));
			}
		}
		surroundingTanks = new ArrayList<ForgeDirection>();
		for (ForgeDirection side : checkSides) {
			TileEntity tile = BlockUtils.getTileInDirection(currentTile, side);
			if (tile instanceof IFluidHandler) {
				surroundingTanks.add(side);
			}
		}
	}

	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if (resource == null) { return null; }
		if (this.fluid == null) { return null; }
		if (!this.fluid.isFluidEqual(resource)) { return null; }
		return this.drain(resource.amount, doDrain);
	}

	public int getSpace() {
		return getCapacity() - getFluidAmount();
	}

	public double getPercentFull() {
		return (double)getFluidAmount() / (double)getCapacity();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		if (resource == null) { return 0; }
		if (acceptableFluids.length == 0) { return super.fill(resource, doFill); }
		for (FluidStack acceptableFluid : acceptableFluids) {
			if (acceptableFluid.isFluidEqual(resource)) { return super.fill(resource, doFill); }
		}
		return 0;
	}

	public void autoOutputToSides(IOpenModsProxy proxy, int amountPerTick, TileEntity currentTile, SyncableFlags sides) {

		if (currentTile.getWorldObj() == null) return;
		// every 10 ticks refresh the surrounding tanks
		if (proxy.getTicks(currentTile.getWorldObj()) % 10 == 0) {
			refreshSurroundingTanks(currentTile, sides);
		}

		if (getFluidAmount() > 0 && surroundingTanks.size() > 0) {
			FluidStack drainedFluid = drain(Math.min(getFluidAmount(), amountPerTick), true);
			if (drainedFluid != null) {
				Collections.shuffle(surroundingTanks);
				// for each surrounding tank
				for (ForgeDirection side : surroundingTanks) {
					TileEntity otherTank = BlockUtils.getTileInDirection(currentTile, side);
					if (drainedFluid.amount > 0) {
						drainedFluid = drainedFluid.copy();
						if (otherTank instanceof IFluidHandler) {
							drainedFluid.amount -= ((IFluidHandler)otherTank).fill(side.getOpposite(), drainedFluid, true);
						} else {
							drainedFluid.amount -= Integration.modBuildCraft().tryAcceptIntoPipe(otherTank, drainedFluid, side.getOpposite());
						}
					}
				}
				// fill any remainder
				if (drainedFluid.amount > 0) {
					fill(drainedFluid, true);
				}
			}
		}

	}

	public void autoFillFromSides(IOpenModsProxy proxy, int amountPerTick, TileEntity currentTile) {
		autoFillFromSides(proxy, amountPerTick, currentTile, null);
	}

	public void autoFillFromSides(IOpenModsProxy proxy, int amountPerTick, TileEntity currentTile, SyncableFlags sides) {

		// every 10 ticks refresh the surrounding tanks
		if (proxy.getTicks(currentTile.getWorldObj()) % 10 == 0) {
			refreshSurroundingTanks(currentTile, sides);
		}

		// if we've got space in the tank, and we've got at least 1 surrounding
		// tank
		if (getSpace() > 0 && surroundingTanks.size() > 0) {
			// shuffle them up
			Collections.shuffle(surroundingTanks);

			// for each surrounding tank
			for (ForgeDirection side : surroundingTanks) {
				TileEntity otherTank = BlockUtils.getTileInDirection(currentTile, side);
				if (otherTank instanceof IFluidHandler) {

					IFluidHandler handler = (IFluidHandler)otherTank;

					// get the fluid inside that tank. If the fluid is one of
					// our acceptable fluids
					// (or we dont have any acceptable fluids), and it matches
					// what we have in the tank
					// or the tank is currently empty...
					FluidStack currentFluid = getFluid();
					if (currentFluid == null) {
						FluidTankInfo[] infos = handler.getTankInfo(side.getOpposite());

						if (infos == null) {
							Log.info("Tank %s @ (%d,%d,%d) returned null tank info. Nasty.",
									otherTank.getClass(), otherTank.xCoord, otherTank.yCoord, otherTank.zCoord);
							continue;
						}

						for (FluidTankInfo info : infos) {
							if (acceptableFluids.length == 0
									&& info.fluid != null) {
								currentFluid = info.fluid;
							} else {
								for (FluidStack acceptFluid : acceptableFluids) {
									if (info.fluid != null
											&& info.fluid.isFluidEqual(acceptFluid)) {
										currentFluid = info.fluid;
										break;
									}
								}
							}
							if (currentFluid != null) {
								break;
							}
						}
					}
					if (currentFluid != null) {
						// copy the fluid and set the amount to the amount we
						// want to drain
						FluidStack drainStack = currentFluid.copy();
						drainStack.amount = Math.min(amountPerTick, getSpace());
						// drain it out and fill our own tank
						FluidStack drained = handler.drain(side.getOpposite(), drainStack, true);
						fill(drained, true);
						// if it's full, chillax.
						if (getCapacity() == getFluidAmount()) {
							break;
						}
					}
				}
			}
		}
	}

}
