/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
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
package ca.phon.opgraph.app.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * A transferable that deals with the transfer of an object of a specific class.
 */
public class ObjectSelection implements Transferable {
	/** The object being transferred */
	private Object obj;

	/**
	 * Constructs a transferable with a given object.
	 * 
	 * @param obj  the object
	 */
	public ObjectSelection(Object obj) {
		this.obj = obj;
	}

	//
	// Transferable
	//

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if(isDataFlavorSupported(flavor))
			return obj;
		throw new UnsupportedFlavorException(flavor);
	}

	@Override
	public DataFlavor [] getTransferDataFlavors() {
		final Class<?> clz = (obj == null ? Object.class : obj.getClass());
		return new DataFlavor[]{new DataFlavor(clz, clz.getSimpleName())};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (obj == null || flavor.getRepresentationClass().isAssignableFrom(obj.getClass()));
	}

}
