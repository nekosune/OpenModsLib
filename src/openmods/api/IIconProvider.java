package openmods.api;

import javax.swing.Icon;

import net.minecraftforge.common.util.ForgeDirection;

public interface IIconProvider {
	public Icon getIcon(ForgeDirection rotatedDir);
}
