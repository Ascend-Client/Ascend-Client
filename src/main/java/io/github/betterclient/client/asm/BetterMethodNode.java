package io.github.betterclient.client.asm;

import org.objectweb.asm.tree.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("all")
public class BetterMethodNode {
	private BetterClassNode declaringClass;
	private MethodNode origin;
	
	public BetterMethodNode(BetterClassNode node, MethodNode origin) {
		this.declaringClass = node;
		this.origin = origin;
	}
	
	public String getName() {
		return origin.name;
	}
	
	public String getDesc() {
		return origin.desc;
	}
	
	public BetterClassNode getDeclaringClass() {
		return declaringClass;
	}
	
	public MethodNode getOrigin() {
		return origin;
	}
	
	public List<ParameterNode> getParameters() {
		return origin.parameters;
	}
	
	public List<AbstractInsnNode> getInstructions() {
		List<AbstractInsnNode> list = Arrays.asList((AbstractInsnNode) null);
		list.remove((AbstractInsnNode) null);
		
		for (Iterator<AbstractInsnNode> iterator = origin.instructions.iterator(); iterator.hasNext();) {
			list.add((AbstractInsnNode) iterator.next());
		}
		
		return list;
	}

	public Iterable<AbstractInsnNode> getInstructionsIterable() {
		return new Iterable<AbstractInsnNode>() {
            @Override
            public Iterator<AbstractInsnNode> iterator()
            {
                return origin.instructions.iterator();
            }
        };
	}
}
