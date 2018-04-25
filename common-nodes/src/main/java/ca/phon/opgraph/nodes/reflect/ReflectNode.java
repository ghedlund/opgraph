package ca.phon.opgraph.nodes.reflect;

import java.lang.reflect.Member;

/**
 * Interface implemented by reflection based nodes.
 *
 */
public interface ReflectNode {
	
	/**
	 * Get the declared class
	 * 
	 * @return declard class
	 */
	public Class<?> getDeclaredClass();
	
	/**
	 * Get the class member, may be <code>null</code>
	 * 
	 * @return class member
	 */
	public Member getClassMember();

}
