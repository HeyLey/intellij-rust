package org.rust.ide.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import org.rust.ide.formatter.blocks.RsFmtBlock
import org.rust.ide.formatter.blocks.RsMacroArgFmtBlock
import org.rust.ide.formatter.blocks.RsMultilineStringLiteralBlock
import org.rust.lang.core.psi.RS_RAW_LITERALS
import org.rust.lang.core.psi.RS_STRING_LITERALS
import org.rust.lang.core.psi.RsElementTypes.MACRO_ARG
import org.rust.ide.formatter.blocks.RustFmtBlock
import org.rust.ide.formatter.blocks.RustMacroArgFmtBlock
import org.rust.lang.core.psi.RustCompositeElementTypes.*

class RsFormattingModelBuilder : FormattingModelBuilder {
    override fun getRangeAffectingIndent(file: PsiFile?, offset: Int, elementAtOffset: ASTNode?): TextRange? = null

    override fun createModel(element: PsiElement, settings: CodeStyleSettings): FormattingModel {
        val ctx = RsFmtContext.create(settings)
        val block = createBlock(element.node, null, Indent.getNoneIndent(), null, ctx)
        /** / com.intellij.formatting.FormattingModelDumper.dumpFormattingModel(block, 2, System.err) // */
        return FormattingModelProvider.createFormattingModelForPsiFile(element.containingFile, block, settings)
    }

   /* companion object {
        fun createBlock(
            node: ASTNode,
            alignment: Alignment?,
            indent: Indent?,
            wrap: Wrap?,

            ctx: RsFmtContext
        ): ASTBlock {
            val type = node.elementType

            if (type == MACRO_ARG) {
                return RsMacroArgFmtBlock(node, alignment, indent, wrap, ctx)
            }

            if ((type in RS_STRING_LITERALS || type in RS_RAW_LITERALS) && node.textContains('\n')) {
                return RsMultilineStringLiteralBlock(node, alignment, indent, wrap)
            }

            return RsFmtBlock(node, alignment, indent, wrap, ctx)
            ctx: RustFmtContext
        ): ASTBlock = when (node.elementType) {
            MACRO_ARG -> RustMacroArgFmtBlock(node, alignment, indent, wrap, ctx)
            MACRO_DEFINITION_ARG -> RustMacroArgFmtBlock(node, alignment, indent, wrap, ctx)
            else -> RustFmtBlock(node, alignment, indent, wrap, ctx) }
    } */
}
