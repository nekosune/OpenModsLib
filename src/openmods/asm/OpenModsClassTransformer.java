package openmods.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class OpenModsClassTransformer implements IClassTransformer {

	public static interface TransformProvider {
		public ClassVisitor createVisitor(ClassVisitor cv);
	}

	private final static TransformProvider INCLUDING_CV = new TransformProvider() {

		@Override
		public ClassVisitor createVisitor(ClassVisitor cv) {
			return new IncludingClassVisitor(cv);
		}
	};

	public static byte[] applyVisitor(byte[] bytes, TransformProvider context) {
		ClassReader cr = new ClassReader(bytes);
		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		ClassVisitor mod = context.createVisitor(cw);
		try {
			cr.accept(mod, 0);
			return cw.toByteArray();
		} catch (StopTransforming e) {
			return bytes;
		}
	}

	@Override
	public byte[] transform(final String name, String transformedName, byte[] bytes) {
		if (name.startsWith("openmods.asm")) return bytes;
		return applyVisitor(bytes, INCLUDING_CV);
	}
}