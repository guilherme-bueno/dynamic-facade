package com.braveinnov.bytebuddy.custom;

import java.util.List;

import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;

public class TypeDescrFix extends TypeDescription.Latent {
    public TypeDescrFix(final String name, final int modifiers, final Generic superClass, final List<? extends Generic> interfaces) {
        super(name, modifiers, superClass, interfaces);
    }

    @Override
    public TypeList getDeclaredTypes() {
        return new TypeList.Empty();
    }

    @Override
    public AnnotationList getDeclaredAnnotations() {
        return new AnnotationList.Empty();
    }

    @Override
    public TypeDescription getDeclaringType() {
        return TypeDescription.CLASS;
    }
}