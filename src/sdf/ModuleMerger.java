package sdf;

import java.util.ArrayList;
import java.util.HashMap;

import sdf.model.*;

public class ModuleMerger implements Visitor {
	
	private static final boolean DEBUG = true;

	private SdfDSL dsl;
	
	// state information
	private Module newMod;
	private HashMap<Symbol,Symbol> replacements;
	
	public ModuleMerger(SdfDSL dsl) {
		this.dsl = dsl;
	}
	
	public Module processModule(Module mod) {
		return processModule(mod, null, null);
	}
	
	public Module processModule(Module mod, ArrayList<Symbol> parameters, HashMap<Symbol,Symbol> renamings) {
		
		if (DEBUG) System.out.println("*** ModuleMerger.processModule(" + mod.getName() + ")");
		
		// set up replacement table
		this.replacements = new HashMap<Symbol, Symbol>();
		if (renamings != null) {
			this.replacements.putAll(renamings);
		}
		ArrayList<Symbol> formalParameters = mod.getParameters();
		if (parameters != null) {
			if (parameters.size() != formalParameters.size()) {
				// TODO: error
				System.out.println("=== INVALID NUMBER OF PARAMETERS GIVEN FOR MODULE '" + mod.getName() + "' ===");
			}
			for (int i = 0; i < formalParameters.size(); i++) {
				this.replacements.put(formalParameters.get(i), parameters.get(i));
			}
		} else if (formalParameters.size() > 0) {
			// TODO: error
			System.out.println("=== NO PARAMETERS GIVEN FOR MODULE '" + mod.getName() + "' ===");
		}
		
		
		Module processedModule = (Module)mod.visit(this, null);
//		dsl.setProcessedModule(mod.getName(), processedModule);
		return processedModule;
	}
	
	
	private Symbol getReplacementSymbol(Symbol original) {
		Symbol replacement = replacements.get(original);
		if (DEBUG && replacement != null) {
			System.out.println("*** Replacing " + original + " => " + replacement + " (in " + newMod.getName() + ")");
		}
		return replacement;
	}
	
	private void importModule(Import imp) {
		if (DEBUG) System.out.println("*** Importing module: " + imp + " into " + newMod.getName());
		
		// process module to import recursively
		Module moduleToImport = dsl.getModule(imp.getModuleName());
		if (moduleToImport == null) {
			// TODO: Error!
			System.out.println("=== MODULE '" + imp.getModuleName() + "' NOT FOUND! ===");
		}
		ModuleMerger subMerge = new ModuleMerger(dsl);
		Module importedModule = subMerge.processModule(moduleToImport, imp.getParameters(), imp.getRenamings());
		
		// now copy the exports sections of the processed module into this module
		// TODO: if the import is inside a hiddens section, import all sections from the imported module,
		// but into hiddens sections in the new module! (not 100% clear from docs if hiddens sections are also imported...)
		for (ExportOrHiddenSection sect : importedModule.getExportOrHiddenSections()) {
			if (sect instanceof Exports) {
				newMod.getExportOrHiddenSections().add(sect);
			}
		}
		
		if (DEBUG) System.out.println("*** Module imported: " + imp.getModuleName() + " into " + newMod.getName());
	}
	
	
	
	
	//// VISITOR METHODS ////

	@Override
	public Object visitDefinition(Definition def, Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitModule(Module mod, Object o) {
		this.newMod = new Module(mod.getName());
		
		newMod.setParameters(new ArrayList<Symbol>(mod.getParameters()));
		for (Imports impSect : mod.getImportSections()) {
			impSect.visit(this, null);
		}
		for (ExportOrHiddenSection sect : mod.getExportOrHiddenSections()) {
			ExportOrHiddenSection newSect = (ExportOrHiddenSection)sect.visit(this, null);
			newMod.getExportOrHiddenSections().add(newSect);
		}
		
		return newMod;
	}

	@Override
	public Object visitExports(Exports exp, Object o) {
		ArrayList<GrammarElement> newElements = new ArrayList<GrammarElement>();
		
		for (GrammarElement ge : exp.getGrammarElements()) {
			GrammarElement newGe = (GrammarElement)ge.visit(this, null);
			if (newGe != null) { // imports are removed
				newElements.add(newGe);
			}
		}
		
		return new Exports(newElements);
	}

	@Override
	public Object visitHiddens(Hiddens hid, Object o) {
		ArrayList<GrammarElement> newElements = new ArrayList<GrammarElement>();
		
		for (GrammarElement ge : hid.getGrammarElements()) {
			GrammarElement newGe = (GrammarElement)ge.visit(this, null);
			if (newGe != null) { // imports are removed
				newElements.add(newGe);
			}
		}
		
		return new Hiddens(newElements);
	}

	@Override
	public Object visitImports(Imports imp, Object o) {
		for (Import i : imp.getImportList()) {
			i.visit(this, null);
		}
		return null; // imports are removed
	}

	@Override
	public Object visitSorts(Sorts sor, Object o) {
		ArrayList<SortSymbol> newSymbols = new ArrayList<SortSymbol>(sor.getSymbols().size());
		
		// TODO: replacement hier? problem ist, es könnte durch ein anderes nicht-sort symbol ersetzt werden
		// das darf hier aber nicht aufreten.
		for (SortSymbol s : sor.getSymbols()) {
			newSymbols.add(new SortSymbol(s.getName()));
		}
		
		return new Sorts(newSymbols);
	}

	@Override
	public Object visitContextFreeSyntax(ContextFreeSyntax syn, Object o) {
		ArrayList<Production> newProductions = new ArrayList<Production>(syn.getProductions().size());

		// TODO: muss hier was mit namespaces beachtet werden? offenbar nicht...?
		
		for (Production p : syn.getProductions()) {
			newProductions.add((Production)p.visit(this, null));
		}
	
		return new ContextFreeSyntax(newProductions);
	}

	@Override
	public Object visitLexicalSyntax(LexicalSyntax syn, Object o) {
		ArrayList<Production> newProductions = new ArrayList<Production>(syn.getProductions().size());

		for (Production p : syn.getProductions()) {
			newProductions.add((Production)p.visit(this, null));
		}
	
		return new LexicalSyntax(newProductions);
	}

	@Override
	public Object visitLexicalStartSymbols(LexicalStartSymbols sta, Object o) {
		ArrayList<Symbol> newSymbols = new ArrayList<Symbol>();

		for (Symbol s : sta.getSymbols()) {
			newSymbols.add((Symbol)s.visit(this, null));
		}
		
		return new LexicalStartSymbols(newSymbols);
	}

	@Override
	public Object visitContextFreeStartSymbols(ContextFreeStartSymbols sta,
			Object o) {
		ArrayList<Symbol> newSymbols = new ArrayList<Symbol>();

		for (Symbol s : sta.getSymbols()) {
			newSymbols.add((Symbol)s.visit(this, null));
		}
		
		return new ContextFreeStartSymbols(newSymbols);
	}

	@Override
	public Object visitProduction(Production pro, Object o) {
		ArrayList<Symbol> newLhs = new ArrayList<Symbol>();
		
		for (Symbol s : pro.getLhs()) {
			newLhs.add((Symbol)s.visit(this, null));
		}
		
		Symbol newRhs = (Symbol) pro.getRhs().visit(this, null);
		
		return new Production(newLhs, newRhs);
	}

	@Override
	public Object visitImport(Import imp, Object o) {
		importModule(imp);
		return null; // imports are removed
	}

	@Override
	public Object visitCharacterClassSymbol(CharacterClassSymbol sym, Object o) {
		Symbol replacement = getReplacementSymbol(sym);
		if (replacement != null) return replacement;
		
		return new CharacterClassSymbol(sym.getPattern());
	}

	@Override
	public Object visitCharacterClassComplement(CharacterClassComplement sym,
			Object o) {
		Symbol replacement = getReplacementSymbol(sym);
		if (replacement != null) return replacement;
		
		// TODO hier könnte das innere symbol theoretisch durch was anderes ersetzt werden.
		// evtl einfach ersetzungen für character classes zb nicht erlauben. oder es schlägt hier fehl.
		return new CharacterClassComplement((CharacterClassSymbol)sym.getSymbol().visit(this, null));
	}

	@Override
	public Object visitCharacterClassDifference(CharacterClassDifference sym,
			Object o) {
		Symbol replacement = getReplacementSymbol(sym);
		if (replacement != null) return replacement;
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCharacterClassIntersection(
			CharacterClassIntersection sym, Object o) {
		Symbol replacement = getReplacementSymbol(sym);
		if (replacement != null) return replacement;
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCharacterClassUnion(CharacterClassUnion sym, Object o) {
		Symbol replacement = getReplacementSymbol(sym);
		if (replacement != null) return replacement;
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLiteralSymbol(LiteralSymbol sym, Object o) {
		Symbol replacement = getReplacementSymbol(sym);
		if (replacement != null) return replacement;
		
		return new LiteralSymbol(sym.getText(), sym.isCaseSensitive());
	}

	@Override
	public Object visitOptionalSymbol(OptionalSymbol sym, Object o) {
		Symbol replacement = getReplacementSymbol(sym);
		if (replacement != null) return replacement;

		return new OptionalSymbol((Symbol)sym.getSymbol().visit(this, null));
	}

	@Override
	public Object visitRepetitionSymbol(RepetitionSymbol sym, Object o) {
		Symbol replacement = getReplacementSymbol(sym);
		if (replacement != null) return replacement;
		
		return new RepetitionSymbol((Symbol)sym.getSymbol().visit(this, null), sym.isAtLeastOnce());
	}

	@Override
	public Object visitSortSymbol(SortSymbol sym, Object o) {
		Symbol replacement = getReplacementSymbol(sym);
		if (replacement != null) return replacement;
		
		return new SortSymbol(sym.getName());
	}

	@Override
	public Object visitSequenceSymbol(SequenceSymbol sym, Object o) {
		Symbol replacement = getReplacementSymbol(sym);
		if (replacement != null) return replacement;
		
		ArrayList<Symbol> newSymbols = new ArrayList<Symbol>();
		for (Symbol s : sym.getSymbols()) {
			newSymbols.add((Symbol)s.visit(this, null));
		}
		
		return new SequenceSymbol(newSymbols);
	}

	@Override
	public Object visitListSymbol(ListSymbol sym, Object o) {
		Symbol replacement = getReplacementSymbol(sym);
		if (replacement != null) return replacement;
		
		return new ListSymbol((Symbol)sym.getElement().visit(this, null), (Symbol)sym.getSeperator().visit(this, null), sym.isAtLeastOnce());
	}

	@Override
	public Object visitAlternativeSymbol(AlternativeSymbol sym, Object o) {
		Symbol replacement = getReplacementSymbol(sym);
		if (replacement != null) return replacement;
		
		return new AlternativeSymbol((Symbol)sym.getLeft().visit(this, null), (Symbol)sym.getRight().visit(this, null));
	}
}
