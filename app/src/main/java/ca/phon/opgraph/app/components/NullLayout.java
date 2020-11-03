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
package ca.phon.opgraph.app.components;

import java.awt.*;

/**
 * A special version of a <code>null</code> layout which takes care of
 * resizing children to their preferred size, and computing sizing
 * preferences based on the children.
 */
public class NullLayout implements LayoutManager {
	@Override
	public void layoutContainer(Container parent) {
		for(Component comp : parent.getComponents()) {
			Dimension dim = comp.getPreferredSize();
			if(dim != null) {
				final Dimension min = comp.getMinimumSize();
				if(min != null) {
					dim.width = Math.max(min.width, dim.width);
					dim.height = Math.max(min.height, dim.height);
				}

				final Dimension max = comp.getMaximumSize();
				if(max != null) {
					dim.width = Math.min(max.width, dim.width);
					dim.height = Math.min(max.height, dim.height);
				}
			} else {
				dim = parent.getSize();
			}

			comp.setSize(dim);
		}
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		int maxX = 0;
		int maxY = 0;
		for(Component comp : parent.getComponents()) {
			final Dimension dim = comp.getPreferredSize();
			if(dim != null) {
				final Dimension min = comp.getMinimumSize();
				if(min != null) {
					dim.width = Math.max(min.width, dim.width);
					dim.height = Math.max(min.height, dim.height);
				}

				final Dimension max = comp.getMaximumSize();
				if(max != null) {
					dim.width = Math.min(max.width, dim.width);
					dim.height = Math.min(max.height, dim.height);
				}

				maxX = Math.max(maxX, comp.getX() + dim.width);
				maxY = Math.max(maxY, comp.getY() + dim.height);
			}
		}
		return new Dimension(maxX, maxY);
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {}

	@Override
	public void removeLayoutComponent(Component comp) {}
}
