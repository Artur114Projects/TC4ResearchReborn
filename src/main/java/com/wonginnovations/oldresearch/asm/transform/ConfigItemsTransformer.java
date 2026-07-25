package com.wonginnovations.oldresearch.asm.transform;

import com.artur114.bananalib.asm.AbstractASMTransformer;
import com.artur114.bananalib.asm.patterns.InsnPatBuilder;
import com.artur114.bananalib.asm.patterns.InsnPattern;
import com.artur114.bananalib.asm.tree.ClassNodeAdv;
import com.artur114.bananalib.asm.util.IASMLogger;
import com.artur114.bananalib.asm.util.InsnBuilder;

public class ConfigItemsTransformer extends AbstractASMTransformer {

    public ConfigItemsTransformer() {
        super("thaumcraft.common.config.ConfigItems");
    }

    @Override
    protected ClassNodeAdv transform(IASMLogger logger, String className, ClassNodeAdv clazz) {
        clazz.findMethod("initItems").ifPresent(method -> {
            InsnPatBuilder builder = InsnPattern.builder();
            InsnBuilder insn = new InsnBuilder();

            builder.thenTypeInsn(NEW, "thaumcraft/common/items/curios/ItemCurio");
            method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                insn.typeInsn(NEW, "com/wonginnovations/oldresearch/common/items/ItemCurio");
                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                method.instructions.replace(interval, insn.build());
            });

            builder.thenMethodInsn(INVOKESPECIAL, "thaumcraft/common/items/curios/ItemCurio", "<init>", "()V", false);
            method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                insn.invokeSpecial("com/wonginnovations/oldresearch/common/items/ItemCurio", "<init>", "()V");
                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                method.instructions.replace(interval, insn.build());
            });
        });
        return clazz;
    }

    @Override
    public int priority() {
        return 0;
    }
}
