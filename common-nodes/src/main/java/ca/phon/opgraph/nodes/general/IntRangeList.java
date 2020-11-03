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
package ca.phon.opgraph.nodes.general;

import java.util.*;

/**
 * An integer range, as a list.
 */
class IntRangeList extends AbstractList<Integer> {
	/** Start of the range, inclusive */
	private int start;

	/** End of the range, inclusive */
	private int end;

	/**
	 * Constructs an integer range.
	 * 
	 * @param start  start value of the range, inclusive
	 * @param end  end value of the range, inclusive
	 */
	public IntRangeList(int start, int end) {
		if(start <= end) {
			this.start = start;
			this.end = end;
		} else {
			this.start = end;
			this.end = start;
		}
	}

	//
	// AbstractList<Integer>
	//

	@Override
	public Integer get(int index) {
		return (start + index);
	}

	@Override
	public int size() {
		return (end - start + 1);
	}
}
