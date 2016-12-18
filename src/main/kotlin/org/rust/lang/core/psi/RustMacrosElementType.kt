package org.rust.lang.core.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.lang.PsiBuilderFactory
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement
import com.intellij.psi.tree.ILazyParseableElementType
import org.rust.lang.RustLanguage
import org.rust.lang.core.psi.impl.RustMacroInvocationElementImpl
import org.rust.lang.core.psi.util.parentOfType

class RustMacrosElementType(s: String) : ILazyParseableElementType(s, RustLanguage) {
    companion object {

        @JvmStatic
        val LAZY_ELEMENT_CONTENT = RustMacrosElementType("LAZY_ELEMENT_CONTENT")
    }

    fun isSimpleMacro(macroDefinition: RustMacroDefinitionElement) : Boolean {
        val macroArgs = macroDefinition.macroDefinitionArg ?: return false
        if (macroArgs.macroDefinitionPatternList.size != 1) {
            return false
        }
        val patternElement = macroArgs.macroDefinitionPatternList.first()
        val simpleMacroPattern = patternElement.simpleMacroPattern ?: return false

        return simpleMacroPattern.simpleMacroPatternType.text == "expr"
    }

    override fun parseContents(chameleon: ASTNode): ASTNode {
        val psi = chameleon.psi
        val macroInvocation = psi.parentOfType<RustLazyElementElement>()?.macroInvocation

        val macroDefinition = (macroInvocation as RustMacroInvocationElementImpl).findMacroDefinition()

        val project = psi.project
        val languageForParser = RustLanguage
        val builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, languageForParser, chameleon.chars)
        val parser = LanguageParserDefinitions.INSTANCE.forLanguage(languageForParser).createParser(project)

        if (macroDefinition != null && isSimpleMacro(macroDefinition)) {
            return parser.parse(RustCompositeElementTypes.EXPR, builder)
        } else {
            return parser.parse(RustCompositeElementTypes.MACRO_ARG, builder)
        }
    }

    override fun createNode(text: CharSequence) : ASTNode {
        return LazyParseablePsiElement(this, text)
    }
}
