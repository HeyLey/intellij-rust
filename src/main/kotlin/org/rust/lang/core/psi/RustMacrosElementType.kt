package org.rust.lang.core.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.impl.source.tree.LazyParseablePsiElement
import com.intellij.psi.tree.ILazyParseableElementType
import org.rust.lang.RustLanguage

class RustMacrosElementType(s: String) : ILazyParseableElementType(s, RustLanguage) {
    companion object {
        @JvmStatic
        fun factory(str: String) : RustMacrosElementType {
            return RustMacrosElementType(str)
        }
    }

    override fun parseContents(chameleon: ASTNode?): ASTNode {
        return super.parseContents(chameleon)
    }

    override fun createNode(text: CharSequence) : ASTNode {
        return LazyParseablePsiElement(this, text)
    }
}
