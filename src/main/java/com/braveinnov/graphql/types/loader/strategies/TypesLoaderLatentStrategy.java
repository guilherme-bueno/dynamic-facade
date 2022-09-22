package com.braveinnov.graphql.types.loader.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.braveinnov.bytebuddy.custom.TypeDescrFix;
import com.braveinnov.graphql.FieldScaffold;
import com.braveinnov.graphql.TypeMap;
import com.braveinnov.graphql.TypeScaffold;
import com.braveinnov.graphql.types.loader.TypesLoader;
import com.braveinnov.helpers.FieldNameHelper;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.jar.asm.Opcodes;

@Component
public class TypesLoaderLatentStrategy implements TypesLoader{

    @Override
    public void loadTypes(Map<String, Class> classes, List<TypeScaffold> types) throws Exception {
        final ByteBuddy bb = new ByteBuddy();

        List<Unloaded<Object>> unloadedTypes = new ArrayList<>();
        for (TypeScaffold type : types) {
            Builder<Object> builder = null;
            if (type.isEnum()) {
                builder = buildEnum(classes, bb, type);
            } else {
                builder = buildType(classes, bb, type);
            }
            unloadedTypes.add(builder.make());
        }

        Unloaded<Object> unloaded = unloadedTypes.get(0);
        Map<TypeDescription, Class<?>> allLoaded = unloaded.include(unloadedTypes).load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION).getAllLoaded();
        System.out.println("Loaded types: " + allLoaded.keySet());

        allLoaded.entrySet().forEach(entry -> {
            classes.put(entry.getKey().getName(), entry.getValue());
        });
    }

    private Builder<Object> buildType(Map<String, Class> classes, final ByteBuddy bb, TypeScaffold type) {
        Builder<Object> builder = bb
            .subclass(Object.class)
            .name(type.getName())
            .modifiers(ModifierContributor.Resolver.of(Visibility.PUBLIC, TypeManifestation.FINAL).resolve());

            if (type.getName().equalsIgnoreCase("AngleInputType")) {
                System.out.println("AngleInputType..."+ type.getFields());
            }

        for (FieldScaffold field : type.getFields()) {
            if (TypeMap.UNKNOW.equals(field.getType())) {
                final TypeDescription.Latent latent = new TypeDescrFix(field.getTypeName(), 0, null, null);
                builder = builder.defineField(FieldNameHelper.getFieldName(field.getName()), latent, Opcodes.ACC_PUBLIC);
                System.out.println("UNKNOW >>> Fieldname: " + field.getName());
            } else {
                System.out.println(field.getName() + " " + field.getTypeName() + " " + field.getType());
                Function<Map<String, Class>, Class> typeLoaderFn = field.getType().getType(field.getTypeName());
                builder = builder.defineField(FieldNameHelper.getFieldName(field.getName()), typeLoaderFn.apply(classes), Opcodes.ACC_PUBLIC);
                System.out.println("PRIMITIVE >>> Fieldname: " + field.getName());
            }
        }
        return builder;
    }
    
    private Builder<Object> buildEnum(Map<String, Class> classes, final ByteBuddy bb, TypeScaffold type) {
        List<String> values = type.getFields().stream().map(field -> field.getName()).collect(Collectors.toList());
        Builder<? extends Enum<?>> name = bb.makeEnumeration(values.toArray(new String[]{}))
            .name(type.getName());
        return (Builder) name;
    }
}
