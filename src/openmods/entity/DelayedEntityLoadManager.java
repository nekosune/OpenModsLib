package openmods.entity;

import java.util.*;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.google.common.base.Supplier;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

public class DelayedEntityLoadManager {
	public static final DelayedEntityLoadManager instance = new DelayedEntityLoadManager();

	private DelayedEntityLoadManager() {}

	private Multimap<Integer, IEntityLoadListener> delayedLoads = Multimaps.newSetMultimap(
			new HashMap<Integer, Collection<IEntityLoadListener>>(),
			new Supplier<Set<IEntityLoadListener>>() {
				@Override
				public Set<IEntityLoadListener> get() {
					return Sets.newSetFromMap(new WeakHashMap<IEntityLoadListener, Boolean>());
				}
			});

	@ForgeSubscribe
	public void onEntityCreate(EntityJoinWorldEvent evt) {
		final Entity entity = evt.entity;
		for (IEntityLoadListener callback : delayedLoads.removeAll(entity.entityId))
			callback.onEntityLoaded(entity);
	}

	public void registerLoadListener(World world, IEntityLoadListener listener, int entityId) {
		Entity entity = world.getEntityByID(entityId);
		if (entity == null) delayedLoads.put(entityId, listener);
		else listener.onEntityLoaded(entity);
	}
}
