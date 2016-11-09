package org.rust.lang.core.psi

import com.intellij.lang.ASTNode
import com.intellij.lang.LanguageParserDefinitions
import com.intellij.lang.PsiBuilderFactory
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement
import com.intellij.psi.tree.ILazyParseableElementType
import org.rust.lang.RustLanguage

class RustMacrosElementType(s: String) : ILazyParseableElementType(s, RustLanguage) {
    companion object {

        @JvmStatic
        val LAZY_ELEMENT_CONTENT = RustMacrosElementType("LAZY_ELEMENT_CONTENT")
    }

    override fun parseContents(chameleon: ASTNode): ASTNode {
        val project = chameleon.psi.project
        val languageForParser = RustLanguage
        val builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, languageForParser, chameleon.chars)
        val parser = LanguageParserDefinitions.INSTANCE.forLanguage(languageForParser).createParser(project)
        return parser.parse(RustCompositeElementTypes.EXPR, builder)
    }

    override fun createNode(text: CharSequence) : ASTNode {
        return LazyParseablePsiElement(this, text)
    }
}
