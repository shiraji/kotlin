/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.lexer.KtTokens;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import java.util.List;

public class KtTypeParameterList extends KtElementImplStub<KotlinPlaceHolderStub<KtTypeParameterList>> {
    public KtTypeParameterList(@NotNull ASTNode node) {
        super(node);
    }

    public KtTypeParameterList(@NotNull KotlinPlaceHolderStub<KtTypeParameterList> stub) {
        super(stub, KtStubElementTypes.TYPE_PARAMETER_LIST);
    }

    @NotNull
    public List<KtTypeParameter> getParameters() {
        return getStubOrPsiChildrenAsList(KtStubElementTypes.TYPE_PARAMETER);
    }

    @NotNull
    public KtTypeParameter addParameter(@NotNull KtTypeParameter typeParameter) {
        return EditCommaSeparatedListHelper.INSTANCE.addItem(this, getParameters(), typeParameter, KtTokens.LT);
    }

    @NotNull
    public void removeParameter(@NotNull KtTypeParameter typeParameter) {
        EditCommaSeparatedListHelper.INSTANCE.removeItem(typeParameter);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitTypeParameterList(this, data);
    }
}
