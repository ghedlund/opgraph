module ca.phon.opgraph.nodes {
	requires java.logging;
	requires java.scripting;

	requires org.antlr.antlr4.runtime;

	requires transitive ca.phon.opgraph.core;
	requires transitive ca.phon.opgraph.app;
	requires ca.phon.opgraph.xml;

	exports ca.phon.opgraph.nodes.canvas;
	exports ca.phon.opgraph.nodes.general;
	exports ca.phon.opgraph.nodes.general.script;
	exports ca.phon.opgraph.nodes.iteration;
	exports ca.phon.opgraph.nodes.logic;
	exports ca.phon.opgraph.nodes.math;
	exports ca.phon.opgraph.nodes.menu;
	exports ca.phon.opgraph.nodes.menu.edits;
	exports ca.phon.opgraph.nodes.random;
	exports ca.phon.opgraph.nodes.reflect;
	exports ca.phon.opgraph.nodes.xml;

	opens ca.phon.opgraph.nodes.reflect;

	provides ca.phon.opgraph.io.xml.XMLSerializer
		with ca.phon.opgraph.nodes.xml.MacroNodeXMLSerializer,
			ca.phon.opgraph.nodes.xml.PublishedFieldXMLSerializer,
			ca.phon.opgraph.nodes.xml.LinkedMacroNodeOverridesXMLSerializer;

	provides ca.phon.opgraph.app.MenuProvider
		with ca.phon.opgraph.nodes.menu.CommonNodesMenuProvider,
			ca.phon.opgraph.nodes.menu.ReflectNodeMenuProvider;

	provides ca.phon.opgraph.app.components.canvas.AbandonedLinkHandler
		with ca.phon.opgraph.nodes.canvas.ReflectAbandonedLinkHandler;

	provides ca.phon.opgraph.OpNode
		with ca.phon.opgraph.nodes.general.MacroNode,
			ca.phon.opgraph.nodes.general.PassThroughNode,
			ca.phon.opgraph.nodes.general.RangeNode,
			ca.phon.opgraph.nodes.general.ArrayNode,
			ca.phon.opgraph.nodes.general.BooleanNode,
			ca.phon.opgraph.nodes.general.NumberNode,
			ca.phon.opgraph.nodes.general.TextNode,
			ca.phon.opgraph.nodes.general.SetGlobalNode,
			ca.phon.opgraph.nodes.logic.LogicalAndNode,
			ca.phon.opgraph.nodes.logic.LogicalNotNode,
			ca.phon.opgraph.nodes.logic.LogicalOrNode,
			ca.phon.opgraph.nodes.logic.LogicalXorNode,
			ca.phon.opgraph.nodes.math.MathExpressionNode,
			ca.phon.opgraph.nodes.general.ScriptNode;

	provides ca.phon.opgraph.io.xml.SchemaProvider
		with ca.phon.opgraph.nodes.xml.CommonNodesSchemaProvider;
}
