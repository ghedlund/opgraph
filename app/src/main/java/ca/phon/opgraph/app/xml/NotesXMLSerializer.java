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
/**
 *
 */
package ca.phon.opgraph.app.xml;

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.xml.*;
import javax.xml.namespace.*;

import org.w3c.dom.*;

import ca.phon.opgraph.*;
import ca.phon.opgraph.app.extensions.*;
import ca.phon.opgraph.extensions.*;
import ca.phon.opgraph.io.xml.*;

/**
 */
public class NotesXMLSerializer implements XMLSerializer {
	static final String NAMESPACE = "https://www.phon.ca/ns/opgraph-app";
	static final String PREFIX = "oga";

	static final QName NOTES_QNAME = new QName(NAMESPACE, "notes", PREFIX);
	static final QName NOTE_QNAME = new QName(NAMESPACE, "note", PREFIX);

	@Override
	public void write(XMLSerializerFactory serializerFactory, Document doc, Element parentElem, Object obj)
		throws IOException
	{
		if(obj == null)
			throw new IOException("Null object given to serializer");

		// setup namespace for document
		final Element rootEle = doc.getDocumentElement();
		rootEle.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
				XMLConstants.XMLNS_ATTRIBUTE + ":" + PREFIX, NAMESPACE);

		if(obj instanceof Notes) {
			// Create notes element, if any exist
			final Notes notes = (Notes)obj;
			if(notes.size() > 0) {
				final Element notesElem = doc.createElementNS(NAMESPACE, PREFIX + ":notes");

				// Create elements for each note
				for(Note note : notes)
					write(serializerFactory, doc, notesElem, note);

				parentElem.appendChild(notesElem);
			}
		} else if(obj instanceof Note) {
			final Note note = (Note)obj;
			final Element noteElem = doc.createElementNS(NAMESPACE, PREFIX + ":note");
			noteElem.setAttribute("title", note.getTitle());
			noteElem.setTextContent(note.getBody());

			final JComponent noteComp = note.getExtension(JComponent.class);
			if(noteComp != null) {
				final String colorString = Integer.toHexString(noteComp.getBackground().getRGB() & 0xFFFFFF);
				
				int width = (int)(noteComp.getWidth() > 0 ? noteComp.getWidth() : noteComp.getPreferredSize().getWidth());
				int height = (int)(noteComp.getHeight() > 0 ? noteComp.getHeight() : noteComp.getPreferredSize().getHeight());
					
				noteElem.setAttribute("x", "" + noteComp.getX());
				noteElem.setAttribute("y", "" + noteComp.getY());
				noteElem.setAttribute("width", "" + width);
				noteElem.setAttribute("height", "" + height);
				noteElem.setAttribute("color", "0x" + colorString);
			}

			parentElem.appendChild(noteElem);
		} else {
			throw new IOException(NotesXMLSerializer.class.getName() + " cannot write objects of type " + obj.getClass().getName());
		}
	}

	@Override
	public Object read(XMLSerializerFactory serializerFactory, OpGraph graph, Object parent, Document doc, Element elem)
		throws IOException
		{
		Object ret = null;
		if(NOTES_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			// Try to get the parent object
			if(parent == null || !(parent instanceof Extendable))
				throw new IOException("Notes extension requires parent to be extendable");

			final Extendable extendable = (Extendable)parent;
			final Notes notes = new Notes();

			// Read in each note
			final NodeList children = elem.getChildNodes();
			for(int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
				final Node noteNode = children.item(childIndex);
				if(noteNode instanceof Element) {
					final Element noteElem = (Element) noteNode;
					read(serializerFactory, graph, notes, doc, noteElem);
				}
			}

			extendable.putExtension(Notes.class, notes);
			ret = notes;
		} else if(NOTE_QNAME.equals(XMLSerializerFactory.getQName(elem))) {
			if(parent == null || !(parent instanceof Notes))
				throw new IOException("Notes extension requires parent to be extendable");

			final Notes notes = (Notes)parent;
			final String title = elem.getAttribute("title");
			final String body = (elem.getTextContent() == null ? "" : elem.getTextContent().trim());
			final Note note = new Note(title, body);

			final JComponent noteComp = note.getExtension(JComponent.class);
			if(noteComp != null) {
				if(elem.hasAttribute("x") && elem.hasAttribute("y")) {
					final int x = Integer.parseInt(elem.getAttribute("x"));
					final int y = Integer.parseInt(elem.getAttribute("y"));
					noteComp.setLocation(x, y);
				}

				if(elem.hasAttribute("width") && elem.hasAttribute("height")) {
					final int w = Integer.parseInt(elem.getAttribute("width"));
					final int h = Integer.parseInt(elem.getAttribute("height"));
					noteComp.setPreferredSize(new Dimension(w, h));
				}

				if(elem.hasAttribute("color")) {
					noteComp.setBackground(Color.decode(elem.getAttribute("color")));
				}
			}

			notes.add(note);
			ret = note;
		}

		return ret;
	}

	@Override
	public boolean handles(Class<?> cls) {
		return (cls == Notes.class || cls == Note.class);
	}

	@Override
	public boolean handles(QName name) {
		return (NOTES_QNAME.equals(name) || NOTE_QNAME.equals(name));
	}
}
