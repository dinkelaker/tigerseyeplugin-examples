package sdf.test;

import org.junit.Before;
import org.junit.Test;

import sdf.SdfDSL;
import sdf.model.ExportOrHiddenSection;
import sdf.model.Exports;
import sdf.model.GrammarElement;
import sdf.model.Imports;
import sdf.model.Module;
import sdf.model.Production;
import sdf.model.SortSymbol;
import sdf.model.Sorts;
import sdf.model.StartSymbols;
import sdf.model.Symbol;
import sdf.model.Syntax;
import de.tud.stg.parlex.core.Grammar;
import de.tud.stg.parlex.parser.earley.Chart;
import de.tud.stg.parlex.parser.earley.EarleyParser;
import junit.framework.TestCase;

/**
 * 
 * SDF:
 * <code>
 * module SimpleArithExpr
 * exports
 * context-free start-symbols Expr
 * sorts Expr Number
 * 
 * lexical syntax
 * [0-9]+				-> Number
 * [ ]+					-> LAYOUT
 * 
 * context-free syntax
 * Expr "+" Number		-> Expr
 * Expr "-" Number		-> Expr
 * Number				-> Expr
 * 
 * 
 * </code>
 * 
 * 
 * @author Pablo Hoch
 *
 */
public class SimpleArithExprSdfTest extends TestCase {
	SdfDSL sdf;
	Grammar grammar;

	@Before
	public void setUp() {
		sdf = new SdfDSL();

		Sorts sorts = sdf.sorts(new SortSymbol[]{
				sdf.sortSymbol("Expr"),
				sdf.sortSymbol("Number"),
		});
		
		Syntax lexSyntax = sdf.lexicalSyntax(new Production[]{
				sdf.production(new Symbol[]{ sdf.repetitionSymbolAtLeastOnce(sdf.characterClassSymbol("0-9")) }, sdf.sortSymbol("Number")),
				//sdf.production(new Symbol[]{ sdf.repetitionSymbolAtLeastOnce(sdf.characterClassSymbol(" ")) }, sdf.sortSymbol("LAYOUT")),
		});
		
		Syntax cfSyntax = sdf.contextFreeSyntax(new Production[]{
				sdf.production(new Symbol[]{
						sdf.sortSymbol("Expr"), sdf.caseSensitiveliteralSymbol("+"), sdf.sortSymbol("Number")
				}, sdf.sortSymbol("Expr")),
				sdf.production(new Symbol[]{
						sdf.sortSymbol("Expr"), sdf.caseSensitiveliteralSymbol("-"), sdf.sortSymbol("Number")
				}, sdf.sortSymbol("Expr")),
				sdf.production(new Symbol[]{
						sdf.sortSymbol("Number")
				}, sdf.sortSymbol("Expr")),
		});
		
		StartSymbols startSymbols = sdf.contextFreeStartSymbols(new Symbol[]{
				sdf.sortSymbol("Expr")
		});
		
		Exports exports = sdf.exports(new GrammarElement[]{
			startSymbols,
			sorts,
			lexSyntax,
			cfSyntax
		});
		
		Module module = sdf.moduleWithoutParameters("SimpleArithExpr", new Imports[]{}, new ExportOrHiddenSection[]{ exports });
		
		grammar = sdf.getGrammar("SimpleArithExpr");
	}
	
	@Test
	public void testGrammar() {
		System.out.println("== SimpleArithExprSdfTest: Grammar ==");
		System.out.println(grammar.toString());
		System.out.println();
	}
	
	@Test
	public void testEarleyParserWithExpr1() {
		System.out.println("== SimpleArithExprSdfTest: Parser ==");
		EarleyParser parser = new EarleyParser(grammar);
		Chart chart = (Chart) parser.parse("2+3-5");
		chart.rparse((de.tud.stg.parlex.core.Rule)grammar.getStartRule());
		System.out.println(chart.toString());
		assertTrue(chart.isValidParse());
		
		System.out.println("AST:");
		System.out.println(chart.getAST().toString());
		System.out.println();
	}
}
