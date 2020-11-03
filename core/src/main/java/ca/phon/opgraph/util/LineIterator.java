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
package ca.phon.opgraph.util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * Iterates over the lines of an input stream.
 */
public class LineIterator implements Iterator<String> {
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(LineIterator.class.getName());

	/** Buffered reader from which lines will come from */
	private BufferedReader br;

	/** Current line */
	private String line;

	/**
	 * Constructs an iterator that will iterate over the lines in a given
	 * input stream.
	 *  
	 * @param is  the input stream
	 */
	public LineIterator(InputStream is) {
		this.br = new BufferedReader(new InputStreamReader(is));
	}

	/**
	 * Constructs an iterator that will iterate over the lines in a given
	 * buffered reader.
	 *  
	 * @param br  the buffered reader
	 */
	public LineIterator(BufferedReader br) {
		this.br = br;
	}

	/**
	 * Constructs an iterator that will iterate over the lines in a given URL.
	 *  
	 * @param url  the url
	 */
	public LineIterator(URL url) {
		try {
			this.br = new BufferedReader(new InputStreamReader(url.openStream()));
		} catch(IOException exc) {
			LOGGER.warning("Could not open line iterator stream for url: " + url);
		}
	}

	//
	// Iterator
	//

	@Override
	public boolean hasNext() {
		if(br != null && line == null) {
			try {
				line = br.readLine();
			} catch(IOException exc) {
				br = null;
				LOGGER.warning("IOException when attempting to read line. Closing reader...");
			}
		}

		return (line != null);
	}

	@Override
	public String next() {
		final String ret = line;
		line = null;
		return ret;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove not supported");
	}
}
