package sdf;

import java.util.List;

import sdf.ruleannotations.CustomATermAnnotation;
import aterm.ATerm;
import aterm.ATermList;
import de.tud.stg.parlex.ast.IAbstractNode;
import de.tud.stg.parlex.core.IRuleAnnotation;
import de.tud.stg.parlex.core.Rule;

/**
 * Builds an ATerm tree out of a parse tree.
 * 
 * <p>This class is similar to {@link ATermConstructor} but differs in how productions <em>without</em> a
 * cons attribute are handled:
 * <ul>
 * <li>If the node has a skip attribute:
 * 	<ul>
 * 		<li>If the node does not have any children, it is removed from the AST</li>
 * 		<li>If the node has exactly one child, the intermediate node is removed</li>
 * 		<li>If the node has more than one child, a list of the children is added to the AST</li>
 * 	</ul>
 * </li>
 * <li>Otherwise (i.e. if the production has neither a cons nor a skip attribute), a list node is created.</li>
 * </ul>
 * 
 * <p>A skip attribute simply looks like this: <code>skip</code>
 * 
 * @author Pablo Hoch
 * @see ATermConstructor
 *
 */
public class ATermConstructorWithSkip extends ATermConstructor {

	ATerm annSkip;
	
	public ATermConstructorWithSkip(GeneratedGrammar grammar, IAbstractNode parseTree) {
		super(grammar, parseTree);
		
		this.annSkip = factory.parse("skip");
	}
	
	@Override
	protected ATerm createNode(String consName, List<ATerm> childTerms, Rule rule) {
		if (consName != null) {
			// rule has cons attribute -> create appl node
			return createApplNode(consName, childTerms, rule);
		} else {
			// rule has NO cons attribute
			boolean skip = hasSkipAttribute(rule);
			ProductionMapping prodMapping = grammar.getProductionMapping(rule);
			// if the production mapping is null, the rule was generated by sdf, and does not
			// appear in the sdf grammar (e.g. a namespace mapping rule or <START> -> startsymbol)
			if (skip || prodMapping == null) {
				// skip this node if at all possible
				return skipNodeIfPossible(childTerms, rule);
			} else {
				// create a list node
				ATermList list = flattenList(childTerms, getLhsAnnotation(rule), getRhsAnnotation(rule));
				return addProductionAnnotation(list, prodMapping);
			}
		}
	}
	
	protected boolean hasSkipAttribute(Rule rule) {
		List<IRuleAnnotation> annotations = rule.getAnnotations();
		for (IRuleAnnotation ann : annotations) {
			if (ann instanceof CustomATermAnnotation) {
				CustomATermAnnotation atermAnn = (CustomATermAnnotation)ann;
				ATerm term = atermAnn.getAterm();
				if (term.equals(annSkip)) {
					return true;
				}
			}
		}
		
		return false;
	}

}
