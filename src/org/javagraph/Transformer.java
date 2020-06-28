package org.javagraph;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Vector;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class Transformer implements ClassFileTransformer {

	public Transformer() {
	}

	@Override
	public
	byte[] transform(ClassLoader loader,
			String className,
			Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain,
			byte[] classfileBuffer) {

		//Do not attempt to transform the following classes.
		if ( ! className.startsWith("org/javagraph")) {
			return classfileBuffer;
		}
		System.out.println("Transforming class: "+className);

		ClassNode cn = new ClassNode();
		ClassReader cr = new ClassReader(classfileBuffer);
		cr.accept(cn, 0);
		for(MethodNode mn: cn.methods) {
			//Static method calls

			
			InsnList il = mn.instructions;
			Vector<AbstractInsnNode> monitorInstructions = new Vector<>();
			for(AbstractInsnNode insn: il.toArray()) {
				if(insn.getOpcode() == Opcodes.MONITORENTER || insn.getOpcode() == Opcodes.MONITOREXIT) {
					monitorInstructions.add(insn);
				}
			}
			for(AbstractInsnNode insn: monitorInstructions) {

				switch(insn.getOpcode()) {
				case Opcodes.MONITORENTER:
					InsnList premonitor = new InsnList();			
					premonitor.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/javagraph/hooks/MonitorHooks", "premonitorenter", "(Ljava/lang/Object;)Ljava/lang/Object;"));
					il.insertBefore(insn, premonitor);
					break;
				case Opcodes.MONITOREXIT:
					InsnList premonitorexit = new InsnList();			
					premonitorexit.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/javagraph/hooks/MonitorHooks", "premonitorexit", "(Ljava/lang/Object;)Ljava/lang/Object;"));
					il.insertBefore(insn,  premonitorexit);
					//System.out.println("MonitorExit");
					break;
				}
			}
		}
		ClassWriter cw = new ClassWriter(0);
		cn.accept(cw);
	
		return cw.toByteArray();
	}
}
