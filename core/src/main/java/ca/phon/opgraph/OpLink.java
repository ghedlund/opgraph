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

import java.util.Collection;

import ca.phon.opgraph.dag.SimpleDirectedEdge;
import ca.phon.opgraph.exceptions.ItemMissingException;
import ca.phon.opgraph.extensions.Extendable;
import ca.phon.opgraph.extensions.ExtendableSupport;
import ca.phon.opgraph.validators.TypeValidator;

/**
 * A link between nodes in an {@link OpGraph}.
 */
public final class OpLink
	extends SimpleDirectedEdge<OpNode>
	implements Extendable
{
	/** The connected output field in the source node's value map */
	private final OutputField sourceField;

	/** The connected input field in the destination node's value map  */
	private final InputField destinationField;

	/**
	 * Create a link with the given source/destination nodes.
	 * 
	 * @param source  source node
	 * @param sourceFieldKey  the key of the field connected at the source
	 * @param destination  destination node
	 * @param destinationFieldKey  the key of the field connected at the destination
	 * 
	 * @throws ItemMissingException  if the given source field key is not in the
	 *                               source node's output fields, or similarly
	 *                               for the destination field and destination node's input fields.
	 * @throws NullPointerException  if either node is <code>null</code>
	 */
	public OpLink(OpNode source,
	                    String sourceFieldKey,
	                    OpNode destination,
	                    String destinationFieldKey)
		throws ItemMissingException
	{
		super(source, destination);

		sourceField = source.getOutputFieldWithKey(sourceFieldKey);
		if(sourceField == null)
			throw new ItemMissingException(sourceField);

		destinationField = destination.getInputFieldWithKey(destinationFieldKey);
		if(destinationField == null)
			throw new ItemMissingException(destinationField);
	}

	/**
	 * Create an node with the given source/destination nodes.
	 * 
	 * @param source  source node
	 * @param sourceField  the field connected at the source
	 * @param destination  destination node
	 * @param destinationField  the field connected at the destination
	 * 
	 * @throws ItemMissingException  if the given source field is not in the source
	 *                               node's output fields, or similarly for the
	 *                               destination field and destination node's input fields.
	 * @throws NullPointerException  if any parameter is <code>null</code>
	 */
	public OpLink(OpNode source,
	                    OutputField sourceField,
	                    OpNode destination,
	                    InputField destinationField)
		throws ItemMissingException
	{
		super(source, destination);

		if(sourceField == null || destinationField == null)
			throw new NullPointerException("Source/destination fields cannot be null");

		if(!source.getOutputFields().contains(sourceField))
			throw new ItemMissingException(sourceField);

		if(!destination.getInputFields().contains(destinationField))
			throw new ItemMissingException(destinationField);

		this.sourceField = sourceField;
		this.destinationField = destinationField;
	}

	/**
	 * Gets the input field.
	 * 
	 * @return the input field
	 */
	public OutputField getSourceField() {
		return sourceField;
	}

	/**
	 * Gets the output field.
	 * 
	 * @return the output field
	 */
	public InputField getDestinationField() {
		return destinationField;
	}

	/**
	 * Gets whether or not this link is valid. A link is valid whenever the
	 * output type of the source field is accepted by the validator defined
	 * in the input field.
	 * 
	 * @return <code>true</code> if this link is valid, <code>false</code> otherwise
	 */
	public boolean isValid() {
		final TypeValidator validator = destinationField.getValidator();
		return (validator == null || validator.isAcceptable(sourceField.getOutputType()));
	}

	//
	// Overrides
	//

	@Override
	public String toString() {
		return String.format("{%s:%s --> %s:%s}",
		                     source.getName(), sourceField.getKey(),
		                     destination.getName(), destinationField.getKey());
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;

		boolean ret = false;
		if(obj != null && (obj instanceof OpLink)) {
			final OpLink link = (OpLink)obj;
			ret = source.equals(link.source)
			      && destination.equals(link.destination)
			      && sourceField.equals(link.sourceField)
			      && destinationField.equals(link.destinationField);
		}
		return ret;
	}

	@Override
	public int hashCode() {
		return (31 * source.hashCode()
				+ 19 * destination.hashCode()
				+ 61 * sourceField.hashCode()
				+ 67 * destinationField.hashCode());
	}

	//
	// Extendable
	//

	private ExtendableSupport extendableSupport = new ExtendableSupport(OpGraph.class);

	@Override
	public <T> T getExtension(Class<T> type) {
		return extendableSupport.getExtension(type);
	}

	@Override
	public Collection<Class<?>> getExtensionClasses() {
		return extendableSupport.getExtensionClasses();
	}

	@Override
	public <T> T putExtension(Class<T> type, T extension) {
		return extendableSupport.putExtension(type, extension);
	}
}
