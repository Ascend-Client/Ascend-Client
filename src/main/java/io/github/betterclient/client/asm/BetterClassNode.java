package io.github.betterclient.client.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.Collections;
import java.util.stream.Stream;

@SuppressWarnings("all")
public class BetterClassNode {
	private ClassNode origin = new ClassNode();
	private List<BetterMethodNode> methods = new ArrayList<>();
	
	public BetterClassNode(byte[] bytes) {
		ClassReader reader = new ClassReader(bytes);
		reader.accept(origin, 0);
		
		for(MethodNode method : getMethodsPriv()) {
			methods.add(new BetterMethodNode(this, method));
		}
	}
	
	public String getName() {
		return origin.name;
	}
	
	public String getDesc() {
		return  "L" + getName().replace('.', '/') + ";";
	}
	
	public List<BetterMethodNode> getMethods() {
		return Collections.unmodifiableList(methods);
	}
	
	public List<AnnotationNode> getAnnotations() {
		return Collections.unmodifiableList(origin.visibleAnnotations);
	}
	
	public List<FieldNode> getFields() {
		return Collections.unmodifiableList(origin.fields);
	}
	
	public List<InnerClassNode> getInnerClasses() {
		return Collections.unmodifiableList(origin.innerClasses);
	}
	
	public String getSuperClass() {
		return origin.superName;
	}
	
	public ClassNode getOrigin() {
		return origin;
	}
	
	public BetterMethodNode getMethod(String name, String desc) {
		for(BetterMethodNode node : getMethods()) {
			if(node.getName().equals(name) && node.getDesc().equals(desc)) {
				return node;
			}
		}
			
		return null;
	}
	
	public byte[] output() {
		ClassWriter writer = new ClassWriter(0);
		origin.accept(writer);

		return writer.toByteArray();
	}

	public BetterMethodNode getMethod(String name) {
		for(BetterMethodNode node : getMethods()) {
			if(node.getName().equals(name)) {
				return node;
			}
		}
		
		return null;
	}
	
	private List<MethodNode> getMethodsPriv() {
		return origin.methods;
	}

	public FieldNode getField(String name) {
		return Stream.of(origin.fields.toArray(new FieldNode[0]))
				.filter(t -> t.name.equalsIgnoreCase(name))
				.findFirst()
				.get();
	}
}
