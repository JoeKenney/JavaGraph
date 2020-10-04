package org.javagraph;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Vector;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * This class transforms the bytecode instructions of a class to add a hook
 * before the MONITORENTER and MONITOREXIT opcodes. These hooks allow the
 * instrumentation class MonitorHooks track different threads as they enter and
 * exit monitors.
 * 
 * @author joe
 *
 */
public class Transformer implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) {

		if (!className.startsWith("org/javagraph")) {
			return classfileBuffer;
		}
		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(classfileBuffer);
		cr.accept(cn, 0);
		for (MethodNode mn : cn.methods) {
			InsnList il = mn.instructions;
			Vector<AbstractInsnNode> monitorInstructions = new Vector<>();
			for (AbstractInsnNode insn : il.toArray()) {
				if (insn.getOpcode() == Opcodes.MONITORENTER || insn.getOpcode() == Opcodes.MONITOREXIT) {
					monitorInstructions.add(insn);
				}
			}
			for (AbstractInsnNode insn : monitorInstructions) {

				switch (insn.getOpcode()) {
				case Opcodes.MONITORENTER:
					InsnList premonitor = new InsnList();
					premonitor.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/javagraph/hooks/MonitorHooks",
							"premonitorenter", "(Ljava/lang/Object;)Ljava/lang/Object;"));
					il.insertBefore(insn, premonitor);
					break;
				case Opcodes.MONITOREXIT:
					InsnList premonitorexit = new InsnList();
					premonitorexit.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/javagraph/hooks/MonitorHooks",
							"premonitorexit", "(Ljava/lang/Object;)Ljava/lang/Object;"));
					il.insertBefore(insn, premonitorexit);
					break;
				}
			}
		}
		ClassWriter cw = new ClassWriter(0);
		cn.accept(cw);

		return cw.toByteArray();
	}
}
