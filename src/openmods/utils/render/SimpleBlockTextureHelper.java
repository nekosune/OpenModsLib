package openmods.utils.render;

import java.util.Map;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Maps;

public class SimpleBlockTextureHelper {

	private IIcon side_up;
	private IIcon side_right;
	private IIcon side_down;
	private IIcon side_left;
	private IIcon top;
	private IIcon bottom;

	private Map<ForgeDirection, IIcon[]> orientations;

	public IIcon setTop(IIcon icon) {
		return top = icon;
	}

	public IIcon setBottom(IIcon icon) {
		return bottom = icon;
	}

	public IIcon setSideLeft(IIcon icon) {
		return side_left = icon;
	}

	public IIcon setSideUp(IIcon icon) {
		return side_up = icon;
	}

	public IIcon setSideRight(IIcon icon) {
		return side_right = icon;
	}

	public IIcon setSideDown(IIcon icon) {
		return side_down = icon;
	}

	private void setup() {
		orientations = Maps.newEnumMap(ForgeDirection.class);
		orientations.put(ForgeDirection.DOWN, new IIcon[] { top, bottom, side_down, side_down, side_down, side_down });
		orientations.put(ForgeDirection.UP, new IIcon[] { bottom, top, side_up, side_up, side_up, side_up });
		orientations.put(ForgeDirection.WEST, new IIcon[] { side_left, side_left, side_right, side_left, top, bottom });
		orientations.put(ForgeDirection.EAST, new IIcon[] { side_right, side_right, side_left, side_right, bottom, top });
		orientations.put(ForgeDirection.SOUTH, new IIcon[] { side_down, side_down, bottom, top, side_right, side_left });
		orientations.put(ForgeDirection.NORTH, new IIcon[] { side_up, side_up, top, bottom, side_left, side_right });
	}

	public IIcon getIconForDirection(ForgeDirection direction, ForgeDirection side) {
		if (orientations == null) {
			setup();
		}
		return orientations.get(direction)[side.ordinal()];
	}

}
