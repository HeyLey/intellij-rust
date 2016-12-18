package org.rust.lang.core.psi.impl.mixin

import com.intellij.lang.ASTNode
import org.rust.lang.core.parser.RustPsiTreeUtil
import org.rust.lang.core.psi.RustMacroDefinitionElement
import org.rust.lang.core.psi.RustMacroInvocationElement
import org.rust.lang.core.psi.RustMacroItemElement
import org.rust.lang.core.psi.impl.RustCompositeElementImpl
import org.rust.lang.core.psi.impl.rustMod

abstract class RustMacroInvocationElementImplMixin(node: ASTNode) : RustCompositeElementImpl(node),
                                                                    RustMacroInvocationElement {
    val macroName: String? get() = node.firstChildNode?.text

    fun findMacroDefinition(): RustMacroDefinitionElement? {
        val nameToFind = macroName
        val macroItems = RustPsiTreeUtil.getChildrenOfType(containingFile.rustMod, RustMacroItemElement::class.java)
        if (macroItems != null) {
            val definitions = macroItems.mapNotNull { it.macro as? RustMacroDefinitionElement }
            return definitions.find { it.identifier?.text == nameToFind }
        } else {
            return null
        }
    }
}
