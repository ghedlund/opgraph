/*
 * Copyright (C) 2012-2026 Gregory Hedlund <https://www.phon.ca>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.phon.opgraph.app.theme;

import java.awt.Color;

import javax.swing.UIManager;

/**
 * Theme-aware colour lookups for the OpGraph editor UI.
 *
 * <p>Painters call {@link #colour(String, Color)} with a UIManager key and a
 * fallback. When the key is registered (typically by a host application's
 * look-and-feel defaults) its value is used; otherwise the fallback applies,
 * preserving legacy hard-coded behaviour for consumers that do not register
 * theme defaults.</p>
 */
public final class UIColors {

	private UIColors() {
		// utility class
	}

	/**
	 * Returns the colour registered under {@code key} in {@link UIManager}, or
	 * {@code fallback} if no value is registered.
	 *
	 * @param key       UIManager key, or {@code null}
	 * @param fallback  value to return when the key is missing
	 * @return the resolved colour
	 */
	public static Color colour(String key, Color fallback) {
		if (key == null) {
			return fallback;
		}
		final Color c = UIManager.getColor(key);
		return (c != null) ? c : fallback;
	}

	// Node painting
	public static final String NODE_BACKGROUND        = "OpGraph.Node.background";
	public static final String NODE_BORDER            = "OpGraph.Node.border";
	public static final String NODE_FOCUS_RING        = "OpGraph.Node.focusRing";
	public static final String NODE_TITLE_TOP         = "OpGraph.Node.titleTop";
	public static final String NODE_TITLE_BOTTOM      = "OpGraph.Node.titleBottom";
	public static final String NODE_TITLE_TEXT        = "OpGraph.Node.titleText";
	public static final String NODE_TITLE_TEXT_SHADOW = "OpGraph.Node.titleTextShadow";
	public static final String NODE_FIELD_TEXT        = "OpGraph.Node.fieldText";

	// Anchors (input/output port fills)
	public static final String ANCHOR_LINK      = "OpGraph.Anchor.link";
	public static final String ANCHOR_DEFAULT   = "OpGraph.Anchor.default";
	public static final String ANCHOR_PUBLISHED = "OpGraph.Anchor.published";

	// Canvas background + grid
	public static final String CANVAS_BACKGROUND = "OpGraph.Canvas.background";
	public static final String CANVAS_GRID_LINE  = "OpGraph.Canvas.gridLine";

	// Links (edges between nodes)
	public static final String LINK_REGULAR  = "OpGraph.Link.regular";
	public static final String LINK_SELECTED = "OpGraph.Link.selected";
	public static final String LINK_STROKE   = "OpGraph.Link.stroke";

	// Selection + drag overlays
	public static final String OVERLAY_SELECTION_FILL   = "OpGraph.Overlay.selectionFill";
	public static final String OVERLAY_SELECTION_STROKE = "OpGraph.Overlay.selectionStroke";
	public static final String OVERLAY_DRAG_VALID       = "OpGraph.Overlay.dragValid";
	public static final String OVERLAY_DRAG_INVALID     = "OpGraph.Overlay.dragInvalid";

	// Node library panel
	public static final String LIBRARY_BACKGROUND     = "OpGraph.Library.background";
	public static final String LIBRARY_FOREGROUND     = "OpGraph.Library.foreground";
	public static final String LIBRARY_DRAG_HIGHLIGHT = "OpGraph.Library.dragHighlight";
	public static final String LIBRARY_DRAG_TEXT      = "OpGraph.Library.dragText";

	// Sticky notes
	public static final String NOTE_BACKGROUND  = "OpGraph.Note.background";
	public static final String NOTE_FOREGROUND  = "OpGraph.Note.foreground";
	public static final String NOTE_RESIZE_GRIP = "OpGraph.Note.resizeGrip";

	// Note swatch picker (NotesMenuProvider)
	public static final String NOTE_SWATCH_RED     = "OpGraph.Note.swatch.red";
	public static final String NOTE_SWATCH_GREEN   = "OpGraph.Note.swatch.green";
	public static final String NOTE_SWATCH_BLUE    = "OpGraph.Note.swatch.blue";
	public static final String NOTE_SWATCH_YELLOW  = "OpGraph.Note.swatch.yellow";
	public static final String NOTE_SWATCH_MAGENTA = "OpGraph.Note.swatch.magenta";
	public static final String NOTE_SWATCH_ORANGE  = "OpGraph.Note.swatch.orange";
	public static final String NOTE_SWATCH_GRAY    = "OpGraph.Note.swatch.gray";

}
