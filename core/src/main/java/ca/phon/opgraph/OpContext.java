/*
 * Copyright (C) 2012-2020 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.phon.opgraph;

import java.util.*;

/**
 * A working context for {@link OpGraph}s. A context can have a parent
 * from which it can find values.
 */
public final class OpContext extends HashMap<String, Object> {
	/** The parent context */
	private OpContext parent;

	/** The child contexts */
	private WeakHashMap<OpNode, OpContext> childContexts;
		
	private boolean debug = false;
	
	/** When executing a node, which outputs are actually used */
	private Set<OutputField> activeOutputs = new HashSet<>();

	/**
	 * Constructs a global context (i.e., no parent context).
	 */
	public OpContext() {
		this(null);
	}

	/**
	 * Constructs a context with the given parent context.
	 * 
	 * @param parent  parent context
	 */
	public OpContext(OpContext parent) {
		this.parent = parent;
	}
	
	/**
	 * Gets the parent context for this context.
	 * 
	 * @return the parent context
	 */
	public OpContext getParent() {
		return parent;
	}
	
	public boolean isDebug() {
		boolean retVal = this.debug;
		if(getParent() != null) {
			retVal |= getParent().isDebug();
		}
		return retVal;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Finds a context for the specified node. This is a deep operation which
	 * will recursively search through all child contexts to find one for the
	 * given node. 
	 * 
	 * @param node  the node to get a context for
	 * 
	 * @return the context for the specified node, or <code>null</code> if
	 *         no context could be found for the given node.
	 */
	public OpContext findChildContext(OpNode node) {
		OpContext context = null;
		if(childContexts != null) {
			// First do a shallow search
			for(Map.Entry<OpNode, OpContext> entry : childContexts.entrySet()) {
				if(entry.getKey() == node) {
					context = entry.getValue();
					break;
				}
			}

			// Didn't find one? Do a deep search
			if(context == null) {
				for(OpContext childContext : childContexts.values()) {
					context = childContext.findChildContext(node);
					if(context != null)
						break;
				}
			}
		}

		return context;
	}

	/**
	 * Gets all the child contexts of this context.
	 * 
	 * @return the mapping of node to context
	 */
	public Map<OpNode, OpContext> getChildContexts() {
		return Collections.unmodifiableMap(childContexts);
	}

	/**
	 * Gets a context for the specified node. If no child context currently 
	 * exists for the given node, a new context will be constructed. The 
	 * returned context will be parented to this context.
	 * 
	 * @param node  the node
	 * 
	 * @return the context
	 */
	public OpContext getChildContext(OpNode node) {
		if(childContexts == null)
			childContexts = new WeakHashMap<OpNode, OpContext>();

		if(!childContexts.containsKey(node))
			childContexts.put(node, new OpContext(this));

		return childContexts.get(node);
	}

	/**
	 * Collects values of a given field from all child contexts.
	 * 
	 * @param field  the field to search for
	 * 
	 * @return a {@link Map} of all the values, and their associated nodes
	 *         as keys (with this context having a <code>null</code> key)
	 */
	@SuppressWarnings("unchecked")
	public <T> Map<OpNode, T> collectValues(ContextualItem field) {
		Map<OpNode, T> results = new HashMap<OpNode, T>();

		if(containsKey(field)) {
			final Object val = get(field);
			try {
				results.put(null, (T)val);
			} catch(ClassCastException exc) { }
		}

		for(Map.Entry<OpNode, OpContext> entry: childContexts.entrySet()) {
			final OpContext context = entry.getValue();
			if(context.containsKey(field)) {
				final Object val = context.get(field);
				try {
					results.put(entry.getKey(), (T)val);
				} catch(ClassCastException exc) { }
			}
		}

		return results;
	}

	/**
	 * Removes all child contexts in this context.
	 */
	public void clearChildContexts() {
		if(childContexts != null)
			childContexts.clear();
	}

	//
	// Sort-of overrides
	//

	/**
	 * Maps the key of a given contextual item to an object.
	 * 
	 * @param item  the {@link ContextualItem} whose key will be used for mapping
	 * @param value  the value to store
	 * 
	 * @return same as {@link HashMap#put(Object, Object)}
	 */
	public Object put(ContextualItem item, Object value) {
		return (item == null ? null : put(item.getKey(), value));
	}

	/**
	 * Removes the value associated with the key of a given contextual item.
	 * 
	 * @param item  the {@link ContextualItem} whose key will be used for mapping 
	 * 
	 * @return same as {@link #remove(Object)}
	 */
	public Object remove(ContextualItem item) {
		return (item == null ? null : remove(item.getKey()));
	}

	/**
	 * Gets whether or not this context contains the key associated with a
	 * given contextual item. 
	 * 
	 * @param item  the {@link ContextualItem} whose key will be used for mapping
	 * 
	 * @return same as {@link #containsKey(Object)} 
	 */
	public boolean containsKey(ContextualItem item) {
		return (item == null ? false : containsKey(item.getKey()));
	}

	/**
	 * Gets the object associated with the key of a specified contextual item.
	 * 
	 * @param item  the {@link ContextualItem} whose key will be used for mapping
	 * 
	 * @return same as {@link #get(Object)}
	 */
	public Object get(ContextualItem item) {
		return (item == null ? null : get(item.getKey()));
	}
	
	public boolean isLocal(ContextualItem item) {
		return (item == null ? false : isLocal(item.getKey()));
	}
	
	public boolean isLocal(String key) {
		return super.containsKey(key);
	}
	
	public void setActiveOutputs(Set<OutputField> activeOutputs) {
		this.activeOutputs = activeOutputs;
	}
	
	public Set<OutputField> getActiveOutputs() {
		return this.activeOutputs;
	}
	
	public void clearActiveOutputs() {
		this.activeOutputs = new HashSet<>();
	}
	
	public boolean isActive(OutputField field) {
		return this.activeOutputs.contains(field);
	}

	//
	// Overrides
	//

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		final Set<java.util.Map.Entry<String, Object>> entries = super.entrySet(); 
		if(parent != null)
			entries.addAll(parent.entrySet());
		return entries;
	}

	@Override
	public Collection<Object> values() {
		final Collection<Object> values = new ArrayList<Object>(super.values()); 
		if(parent != null)
			values.addAll(parent.values());
		return values;
	}

	@Override
	public Set<String> keySet() {
		final Set<String> keys = new LinkedHashSet<String>(super.keySet()); 
		if(parent != null)
			keys.addAll(parent.keySet());
		return keys;
	}

	@Override
	public void clear() {
		super.clear();
		if(childContexts != null)
			childContexts.clear();
	}

	@Override
	public Object put(String key, Object value) {
	    return super.put(key, value);
	}

	@Override
	public Object remove(Object key) {
	    return super.remove(key);
	}

	@Override
	public Object get(Object key) {
		if(super.containsKey(key))
			return super.get(key);
		return (parent == null ? null : parent.get(key));
	}

	@Override
	public boolean containsKey(Object key) {
		boolean ret = super.containsKey(key);
		if(!ret && parent != null)
			ret = parent.containsKey(key);
		return ret;
	}

	@Override
	public boolean containsValue(Object value) {
		boolean ret = super.containsValue(value);
		if(!ret && parent != null)
			ret = parent.containsValue(value);
		return ret;
	}
	
}
