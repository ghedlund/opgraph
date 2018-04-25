/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * This file is part of the OpGraph project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.phon.opgraph.nodes.reflect;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.phon.opgraph.OpGraph;
import ca.phon.opgraph.OpNode;
import ca.phon.opgraph.app.GraphDocument;
import ca.phon.opgraph.app.extensions.NodeSettings;
import ca.phon.opgraph.util.ReflectUtil;

/**
 * Base node for all reflection based {@link OpGraph} nodes.
 * This class handles the properties for the declared class and
 * (optional) member.
 */
public abstract class AbstractReflectNode extends OpNode implements NodeSettings, ReflectNode {
	
	private static final Logger LOGGER = Logger
			.getLogger(AbstractReflectNode.class.getName());

	/**
	 * Declared class
	 */
	private Class<?> declaredClass;
	
	public final String DECLARED_CLASS_PROP = getClass().getName() + ".declaredClass";
	
	/**
	 * Class member
	 */
	private Member classMember;

	public final String CLASS_MEMBER_PROP = getClass().getName() + ".classMember";
	
	public AbstractReflectNode() {
		super();
		putExtension(NodeSettings.class, this);
	}
	
	public AbstractReflectNode(Class<?> declaredClass, Member classMember) {
		super();
		setDeclaredClass(declaredClass);
		setClassMember(classMember);
		putExtension(NodeSettings.class, this);
	}

	public Class<?> getDeclaredClass() {
		return declaredClass;
	}

	public void setDeclaredClass(Class<?> declaredClass) {
		this.declaredClass = declaredClass;
	}

	public Member getClassMember() {
		return classMember;
	}

	public void setClassMember(Member classMember) {
		this.classMember = classMember;
	}

	/*
	 * Settings
	 */
	@Override
	public Component getComponent(GraphDocument document) {
		return null;
	}

	@Override
	public Properties getSettings() {
		final Properties retVal = new Properties();
		if(getDeclaredClass() != null) {
			retVal.put(DECLARED_CLASS_PROP, getDeclaredClass().getName());
		}
		final Member member = getClassMember();
		if(member != null) {
			String memberSig = "";
			if(member instanceof Constructor<?>) {
				memberSig = ReflectUtil.getSignature((Constructor<?>)member, true);
			} else if(member instanceof Method) {
				memberSig = ReflectUtil.getSignature((Method)member, true);
			} else if(member instanceof Field) {
				memberSig = member.getName();
			}
			retVal.put(CLASS_MEMBER_PROP, memberSig);
		}
		return retVal;
	}

	@Override
	public void loadSettings(Properties properties) {
		if(properties.getProperty(DECLARED_CLASS_PROP) != null) {
			final String className = properties.getProperty(DECLARED_CLASS_PROP);
			try {
				final Class<?> declaredClass = Class.forName(className);
				setDeclaredClass(declaredClass);
			} catch (ClassNotFoundException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
			
			if(properties.getProperty(CLASS_MEMBER_PROP) != null) {
				final String memberSig = properties.getProperty(CLASS_MEMBER_PROP);
				Member member = null;
				if(memberSig.startsWith("<init>")) {
					try {
						member = ReflectUtil.getConstructorFromSignature(getDeclaredClass(), memberSig);
					} catch (SecurityException e) {
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(),
								e);
					} catch (ClassNotFoundException e) {
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(),
								e);
					} catch (NoSuchMethodException e) {
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(),
								e);
					}
				} else if(memberSig.indexOf('(') > 0) {
					try {
						member = ReflectUtil.getMethodFromSignature(getDeclaredClass(), memberSig);
					} catch (SecurityException e) {
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(),
								e);
					} catch (ClassNotFoundException e) {
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(),
								e);
					} catch (NoSuchMethodException e) {
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(),
								e);
					}
				} else {
					try {
						member = getDeclaredClass().getField(memberSig);
					} catch (SecurityException e) {
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(),
								e);
					} catch (NoSuchFieldException e) {
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(),
								e);
					}
				}
				if(member != null) {
					setClassMember(member);
				}
			}
		}
	}
}
