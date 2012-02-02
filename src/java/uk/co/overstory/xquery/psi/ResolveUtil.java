package uk.co.overstory.xquery.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 1/31/12
 * Time: 7:08 PM
 */
public abstract class ResolveUtil
{
	public static boolean treeWalkUp (PsiElement place, PsiScopeProcessor processor)
	{
		PsiElement lastParent = null;
		PsiElement run = place;
		while (run != null) {
			if (!run.processDeclarations (processor, ResolveState.initial (), lastParent, place)) {
				return false;
			}

			lastParent = run;
			run = run.getContext (); //same as getParent
		}

		return true;
	}

	public static boolean processChildren (PsiElement element, PsiScopeProcessor processor,
		ResolveState substitutor, PsiElement lastParent, PsiElement place)
	{
		PsiElement run = lastParent == null ? element.getLastChild () : lastParent.getPrevSibling ();
		while (run != null) {
			if (PsiTreeUtil.findCommonParent (place, run) != run && !run.processDeclarations (processor, substitutor, null, place)) {
				return false;
			}

			run = run.getPrevSibling ();
		}

		return true;
	}

	public static boolean processElement (PsiScopeProcessor processor, PsiNamedElement namedElement)
	{
		if (namedElement == null) {
			return true;
		}

		NameHint nameHint = processor.getHint (NameHint.KEY);
		String name = nameHint == null ? null : nameHint.getName (ResolveState.initial());

		if (name == null || name.equals (namedElement.getName ())) {
			return processor.execute (namedElement, ResolveState.initial ());
		}
		return true;
	}

	public static PsiElement[] mapToElements (ResolveResult[] candidates)
	{
		PsiElement[] elements = new PsiElement[candidates.length];

		for (int i = 0; i < elements.length; i++) {
			elements[i] = candidates[i].getElement();
		}

		return elements;
	}
}
