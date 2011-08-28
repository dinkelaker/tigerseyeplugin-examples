package de.tud.stg.popart.builder.test.dsls.sdf;


import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import de.tud.stg.parlex.core.Category;
import de.tud.stg.parlex.core.Grammar;


/**
 * {@link BnfDSL} is a DSL with support for conditional statements in an alternative if-then-else syntax
 * 
 * -- modified version without annotations + type classes --
 * 
 * @author Kamil Erhard
 * 
 */

public class BnfDSL {

	private final HashMap<Identifier, Expression> mapping = new HashMap<Identifier, Expression>();
	private final HashMap<Object, Identifier> objectToIdentifier = new HashMap<Object, Identifier>();
	
	private boolean connectEveryCategoryToStartRule;
	private String startRuleName;
	
	/**
	 * Instantiate a grammar with default options. 
	 * That means connectEveryCategoryToStartRule is false, startRule is the first rule.
	 */
	public BnfDSL() {
		connectEveryCategoryToStartRule = false;
		startRuleName = null;
	}
	
	public BnfDSL(String startRuleName) {
		connectEveryCategoryToStartRule = false;
		this.startRuleName = startRuleName;
	}
	
	public BnfDSL(boolean connectEveryCategoryToStartRule) {
		this.connectEveryCategoryToStartRule = connectEveryCategoryToStartRule;
		this.startRuleName = null;
	}

	public Syntax syntax(Rule[] rules) {
		Syntax syntax = new Syntax(rules, connectEveryCategoryToStartRule, startRuleName);

//		Category syntaxCategory = new Category("PROGRAM", false);
		// this.grammar.addCategory(syntaxCategory);
		//
		// for (Rule r : rules) {
		// Category rulesCategory = new Category(r.toString(), false);
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(syntaxCategory, rulesCategory);
		// this.grammar.addCategory(rulesCategory);
		// this.grammar.addRule(rule);
		// }

		Grammar g = new Grammar();

		for (Rule r : rules) {
			this.mapping.put(r.lhs, r.rhs);
		}

		syntax.evaluate(g, this.mapping);

		syntax.setGrammar(g);

		return syntax;
	}

	public Rule rule(Identifier identifier, Expression expression) {

		Rule rule = new Rule(identifier, expression);

		//
		// Category identifierCategory = new Category(identifier.toString(), false);
		// Category expressionCategory = new Category(expression.toString(), false);
		//
		// this.grammar.addCategories(identifierCategory, expressionCategory);
		//
		// de.tud.stg.parlex.core.Rule r = new de.tud.stg.parlex.core.Rule(identifierCategory, expressionCategory);
		//
		// this.grammar.addRule(r);

		return rule;
	}

	public Expression expression(Term[] terms) {

		Expression expression = new Expression(terms);

		// Category expressionCategory = new Category(expression.toString(), false);
		// this.grammar.addCategory(expressionCategory);
		//
		// for (Term t : terms) {
		// Category termCategory = new Category(t.toString(), false);
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(expressionCategory, termCategory);
		// this.grammar.addCategory(termCategory);
		// this.grammar.addRule(rule);
		// }

		return expression;
	}


	public Term termFromFactors(Factor[] factors) {

		Term term = new Term(factors);

		// Category termCategory = new Category(term.toString(), false);
		// this.grammar.addCategory(termCategory);
		//
		// for (Factor f : factors) {
		// Category factorCategory = new Category(f.toString(), false);
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(termCategory, factorCategory);
		// this.grammar.addCategory(factorCategory);
		// this.grammar.addRule(rule);
		// }

		return term;
	}


	public Factor factorFromIdentifier(Identifier identifier) {

		Factor factor = new Factor(identifier);

		// Category factorCategory = new Category(factor.toString(), false);
		// Category identifierCategory = new Category(identifier.toString(), false);
		//
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, identifierCategory);
		// this.grammar.addCategories(factorCategory, identifierCategory);
		// this.grammar.addRule(rule);

		return factor;
	}


	public Factor factorFromQuotedSymbol(QuotedSymbol quotedSymbol) {

		Factor factor = new Factor(quotedSymbol);
		// Category factorCategory = new Category(factor.toString(), false);
		// Category identifierCategory = new Category(quotedSymbol.toString(), false);
		//
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, identifierCategory);
		// this.grammar.addCategories(factorCategory, identifierCategory);
		// this.grammar.addRule(rule);

		return factor;
	}

	// @DSLMethod(production = "( p0 )", topLevel = false)
	// 
	// public Factor factorFromExpressionInParanthesis(Expression expression) {
	// Factor factor = new Factor(expression);
	// Category factorCategory = new Category(factor.toString(), false);
	// Category leftP = new Category("(", true);
	// Category expressionCategory = new Category(expression.toString(), false);
	// Category rightP = new Category(")", true);
	//
	// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, leftP, expressionCategory,
	// rightP);
	// this.grammar.addCategories(factorCategory, leftP, expressionCategory, rightP);
	// this.grammar.addRule(rule);
	//
	// return factor;
	// }
	//
	// @DSLMethod(production = "[ p0 ]", topLevel = false)
	// 
	// public Factor factorFromExpressionInBrackets(Expression expression) {
	// Factor factor = new Factor(expression);
	// Category factorCategory = new Category(factor.toString(), false);
	// Category leftP = new Category("[", true);
	// Category expressionCategory = new Category(expression.toString(), false);
	// Category rightP = new Category("]", true);
	//
	// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, leftP, expressionCategory,
	// rightP);
	// this.grammar.addCategories(factorCategory, leftP, expressionCategory, rightP);
	// this.grammar.addRule(rule);
	//
	// return factor;
	// }


	public Factor factorFromExpressionInBraces(Expression expression) {

		// TODO: BnfDSLGrammarTest - EXPRESSION --> EXPRESSION überflüssige regel wird erzeugt
		
		Factor factor = new Factor(expression);
		// Category factorCategory = new Category(factor.toString(), false);
		// Category leftP = new Category("{", true);
		// Category expressionCategory = new Category(expression.toString(), false);
		// Category rightP = new Category("}", true);
		//
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(factorCategory, leftP, expressionCategory,
		// rightP);
		// this.grammar.addCategories(factorCategory, leftP, expressionCategory, rightP);
		// this.grammar.addRule(rule);

		return factor;
	}

	// @DSLMethod(production = "p0")
	// 
	// public Identifier identifierFromLetter(Letter letter) {
	// return new Identifier(letter);
	// }


	public Identifier identifierFromLetters(LetterOrDigit[] letterOrDigit) {

		Identifier identifier = new Identifier(letterOrDigit);
		// Category identifierCategory = new Category(identifier.toString(), false);
		//
		// Category letterCategory = new Category(letterOrDigit.toString(), false);
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(identifierCategory, letterCategory);
		// this.grammar.addCategories(identifierCategory, letterCategory);
		// this.grammar.addRule(rule);

		return identifier;
	}

	public QuotedSymbol quotedSymbolFromAnyCharacters(AnyCharacter[] ac) {
		QuotedSymbol quotedSymol = new QuotedSymbol(ac);
		// Category quotedSymbolCategory = new Category(quotedSymol.toString(), false);
		//
		// Category acCategory = new Category(ac.toString(), false);
		// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(quotedSymbolCategory, acCategory);
		// this.grammar.addCategories(quotedSymbolCategory, acCategory);
		// this.grammar.addRule(rule);

		return quotedSymol;
	}

	private static AtomicInteger uuidCounter = new AtomicInteger();

	public static class Syntax implements Evaluable {

		private Grammar grammar;
		private final de.tud.stg.popart.builder.test.dsls.sdf.BnfDSL.Rule[] rules;
		private boolean connectEveryCategoryToStartRule;
		private String startRuleName;

		public Syntax(Rule[] rules, boolean connectEveryCategoryToStartRule, String startRuleName) {
			this.rules = rules;
			this.connectEveryCategoryToStartRule = connectEveryCategoryToStartRule;
			this.startRuleName = startRuleName;
		}

		public void setGrammar(Grammar grammar) {
			this.grammar = grammar;
		}

		public Grammar getGrammar() {
			return this.grammar;
		}

		@Override
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {

			Category cat = new Category("PROGRAM", false);
			Category rules = new Category("RULES", false);
			
			grammar.addCategory(cat);
			grammar.addCategory(rules);

			Category startCat = null;

			de.tud.stg.parlex.core.Rule startRule;
			if (connectEveryCategoryToStartRule) {
				for (Rule r : this.rules) {
					Category evaluate = r.evaluate(grammar, mapping);
					de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(rules, evaluate);
					grammar.addRule(rule);
				}
				startRule = new de.tud.stg.parlex.core.Rule(cat, rules);
			} else {
				if (startRuleName != null) {
					// find start rule by name
					for (Rule r : this.rules) {
						Category evaluate = r.evaluate(grammar, mapping);
						if (evaluate.getName().equals(startRuleName)) {
							startCat = evaluate;
						}
					}
				} else {
					// if no start rule selected take the first one
					startCat = this.rules[0].evaluate(grammar, mapping);
					for (Rule r : this.rules) {
						Category evaluate = r.evaluate(grammar, mapping);
					}
				}
				startRule = new de.tud.stg.parlex.core.Rule(cat, startCat);
			}
			grammar.addRule(startRule);
			grammar.setStartRule(startRule);

			return cat;
		}
	}

	public static class Rule implements Evaluable {

		private final Identifier lhs;
		private final Expression rhs;

		public Rule(Identifier lhs, Expression rhs) {
			this.lhs = lhs;
			this.rhs = rhs;
		}

		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {

			Category identifierC = this.lhs.evaluate(grammar, mapping);
			Category expressionC = this.rhs.evaluate(grammar, mapping);

			de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(identifierC, expressionC);
			grammar.addRule(rule);

			// Category cat = new Category("RULE" + uuidCounter.getAndIncrement(), false);
			return identifierC;
		}

		@Override
		public String toString() {
			return this.lhs.toString();
		}
	}

	/**
	 * An Expression is a RHS that consists of a List of Terms
	 */
	public static class Expression implements Evaluable {

		private final Term[] terms;

		public Expression(Term[] terms) {
			this.terms = terms;
		}

		@Override
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {

			Category[] children = new Category[this.terms.length];
			StringBuilder sb = new StringBuilder();
			int i = 0;

			for (Term t : this.terms) {
				children[i] = t.evaluate(grammar, mapping);
				sb.append(children[i].toString()).append('|');
				i++;
			}

			Category cat = new Category(sb.deleteCharAt(sb.length() - 1).toString(), false);
			grammar.addCategory(cat);

			if (this.terms.length > 1) {
				for (Category c : children) {
					de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(cat, c);
					grammar.addRule(rule);
				}
			}

			return cat;
		}
	}

	/**
	 * A Term consists of several Factors.
	 */
	public static class Term implements Evaluable {

		private final Factor[] factors;

		public Term(Factor[] factors) {
			this.factors = factors;
		}

		@Override
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {

			Category[] c = new Category[this.factors.length];

			int i = 0;
			StringBuilder sb = new StringBuilder();
			for (Factor t : this.factors) {
				c[i] = t.evaluate(grammar, mapping);
				sb.append(c[i].getName()).append('&');
				i++;
			}

			Category cat = new Category(sb.deleteCharAt(sb.length() - 1).toString().toUpperCase(), false);
			grammar.addCategory(cat);

			de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(cat, c);
			grammar.addRule(rule);

			return cat;
		}
	}

	private static interface Evaluable {
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping);
	}

	/**
	 * A Factor is a single Category on the RHS, or an Expression in a Bracket.
	 */
	public static class Factor implements Evaluable {

		private Evaluable e;

		public Factor(Expression expression) {
			this.e = expression;
		}

		public void setTerm(Term term) {
			this.e = term;
		}

		public Factor(QuotedSymbol quotedSymbol) {
			this.e = quotedSymbol;
		}

		public Factor(Identifier identifier) {
			this.e = identifier;
		}

		@Override
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {

			// Category cat = new Category("FACTOR" + uuidCounter.getAndIncrement(), false);

			Category to = this.e.evaluate(grammar, mapping);
			//
			// de.tud.stg.parlex.core.Rule rule = new de.tud.stg.parlex.core.Rule(cat, to);
			// grammar.addRule(rule);

			return to;
		}
	}

	public static class Identifier implements Evaluable {

		private String str = "";

		public Identifier(Letter letter, LetterOrDigit[] letterOrDigit) {

		}

		public Identifier(Letter letter) {
		}

		public Identifier(LetterOrDigit[] letterOrDigit) {
			for (LetterOrDigit l : letterOrDigit) {
				this.str += l.getRepresentation();
			}
		}

		@Override
		public String toString() {
			return this.str;
		}

		@Override
		public int hashCode() {
			return this.str.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Identifier) {
				Identifier i = (Identifier) obj;
				return this.str.equals(i.str);
			}
			return false;
		}

		@Override
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {
			Category cat = new Category(this.str, false);
			grammar.addCategory(cat);
			return cat;
		}
	}

	public static class Letter implements LetterOrDigit {

		private final String str;

		public Letter(String str) {
			this.str = str;
		}

		@Override
		public String getRepresentation() {
			return this.str;
		}

	}

	public static class Digit implements LetterOrDigit {

		private final String str;

		public Digit(String str) {
			this.str = str;
		}

		@Override
		public String getRepresentation() {
			return this.str;
		}

	}

	public static interface LetterOrDigit {
		String getRepresentation();
	}

	public static class QuotedSymbol implements Evaluable {

		private String str = "";

		public QuotedSymbol(AnyCharacter[] ac) {
			for (AnyCharacter a : ac) {
				this.str += a.getRepresentation();
			}
		}

		@Override
		public Category evaluate(Grammar grammar, HashMap<Identifier, Expression> mapping) {
			Category cat = new Category(this.str, true);
			grammar.addCategory(cat);
			return cat;
		}
	}

	public static class AnyCharacter {

		private final String str;

		public AnyCharacter(String str) {
			this.str = str;
		}

		public String getRepresentation() {
			return this.str;
		}
	}
}
