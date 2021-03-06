package org.rust.lang.core.types

import com.intellij.openapi.util.Computable
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import org.rust.ide.utils.recursionGuard
import org.rust.lang.core.psi.*
import org.rust.lang.core.psi.RsElementTypes.COMMA
import org.rust.lang.core.psi.impl.isMut
import org.rust.lang.core.psi.impl.isRef
import org.rust.lang.core.psi.impl.mixin.asRustPath
import org.rust.lang.core.psi.util.elementType
import org.rust.lang.core.psi.util.getPrevNonCommentSibling
import org.rust.lang.core.resolve.Namespace
import org.rust.lang.core.resolve.ResolveEngine
import org.rust.lang.core.symbols.RustPath
import org.rust.lang.core.types.types.*

val RsExpr.type: RustType
    get() = CachedValuesManager.getCachedValue(this, CachedValueProvider {
        CachedValueProvider.Result.create(RustTypificationEngine.typifyExpr(this), PsiModificationTracker.MODIFICATION_COUNT)
    })

val RsTypeReference.type: RustType
    get() = recursionGuard(this, Computable { typeReferenceToType(this) })
        ?: RustUnknownType

val RsTypeBearingItemElement.type: RustType
    get() = CachedValuesManager.getCachedValue(this, CachedValueProvider {
        CachedValueProvider.Result.create(RustTypificationEngine.typify(this), PsiModificationTracker.MODIFICATION_COUNT)
    })


private fun typeReferenceToType(ref: RsTypeReference): RustType {
    return when (ref) {
        is RsTupleType -> {
            val single = ref.typeReferenceList.singleOrNull()
            if (single != null && ref.rparen.getPrevNonCommentSibling()?.elementType != COMMA) {
                return typeReferenceToType(single)
            }
            if (ref.typeReferenceList.isEmpty()) {
                return RustUnitType
            }
            RustTupleType(ref.typeReferenceList.map(::typeReferenceToType))
        }

        is RsBaseType -> {
            val path = ref.path?.asRustPath ?: return RustUnknownType
            if (path is RustPath.Named && path.segments.isEmpty()) {
                val primitiveType = RustPrimitiveType.fromTypeName(path.head.name)
                if (primitiveType != null) return primitiveType
            }
            val target = ResolveEngine.resolve(path, ref, Namespace.Types)
                .filterIsInstance<RsNamedElement>()
                .firstOrNull() ?: return RustUnknownType
            val typeArguments = (path as? RustPath.Named)?.head?.typeArguments.orEmpty()
            RustTypificationEngine.typify(target)
                .withTypeArguments(typeArguments.map { it.type })

        }

        is RsRefLikeType -> {
            if (!ref.isRef) return RustUnknownType
            val base = ref.typeReference ?: return RustUnknownType
            RustReferenceType(typeReferenceToType(base), ref.isMut)
        }

        else ->
            RustUnknownType
    }
}
