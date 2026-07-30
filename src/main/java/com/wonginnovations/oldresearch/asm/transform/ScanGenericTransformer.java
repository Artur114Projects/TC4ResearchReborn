package com.wonginnovations.oldresearch.asm.transform;

import com.artur114.bananalib.asm.AbstractASMTransformer;
import com.artur114.bananalib.asm.patterns.InsnPatBuilder;
import com.artur114.bananalib.asm.patterns.InsnPattern;
import com.artur114.bananalib.asm.tree.ClassNodeAdv;
import com.artur114.bananalib.asm.util.IASMLogger;
import com.artur114.bananalib.asm.util.InsnBuilder;
import com.wonginnovations.oldresearch.asm.ASMTransformerOldRes;

public class ScanGenericTransformer extends AbstractASMTransformer {

    public ScanGenericTransformer() {
        super("thaumcraft.common.lib.research.ScanGeneric");
    }

    @Override
    protected ClassNodeAdv transform(IASMLogger logger, String className, ClassNodeAdv clazz) {
        clazz.findMethod("onSuccess").ifPresent(method -> {
            InsnPatBuilder builder = InsnPattern.builder();
            InsnBuilder insn = new InsnBuilder();

            builder.thenFieldInsn(GETSTATIC, "thaumcraft/api/research/ResearchCategories", "researchCategories", "Ljava/util/LinkedHashMap;");
            builder.thenMethodInsn(INVOKEVIRTUAL, "java/util/LinkedHashMap", "values", "()Ljava/util/Collection;", false);
            builder.thenMethodInsn(INVOKEINTERFACE, "java/util/Collection", "iterator", "()Ljava/util/Iterator;", true);
            builder.thenVarInsn(ASTORE, 4);
            builder.thenVarInsn(ALOAD, 4);
            builder.thenMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
            builder.then(JUMP_INSN.withOpcode(IFEQ));
            builder.thenVarInsn(ALOAD, 4);
            builder.thenMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
            builder.thenTypeInsn(CHECKCAST, "thaumcraft/api/research/ResearchCategory");
            builder.thenVarInsn(ASTORE, 5);
            builder.thenFieldInsn(GETSTATIC, "thaumcraft/api/ThaumcraftApi", "internalMethods", "Lthaumcraft/api/internal/IInternalMethodHandler;");
            builder.thenVarInsn(ALOAD, 1);
            builder.thenFieldInsn(GETSTATIC, "thaumcraft/api/capabilities/IPlayerKnowledge$EnumKnowledgeType", "OBSERVATION", "Lthaumcraft/api/capabilities/IPlayerKnowledge$EnumKnowledgeType;");
            builder.thenVarInsn(ALOAD, 5);
            builder.thenVarInsn(ALOAD, 5);
            builder.thenVarInsn(ALOAD, 3);
            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/api/research/ResearchCategory", "applyFormula", "(Lthaumcraft/api/aspects/AspectList;)I", false);
            builder.thenMethodInsn(INVOKEINTERFACE, "thaumcraft/api/internal/IInternalMethodHandler", "addKnowledge", "(Lnet/minecraft/entity/player/EntityPlayer;Lthaumcraft/api/capabilities/IPlayerKnowledge$EnumKnowledgeType;Lthaumcraft/api/research/ResearchCategory;I)Z", true);
            builder.thenInsn(POP);
            builder.then(JUMP_INSN.withOpcode(GOTO));

            method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                logger.info("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                insn.loadVars("A:1", "A:3");
                insn.invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookScanAspectGeneric", "(Lnet/minecraft/entity/player/EntityPlayer;Lthaumcraft/api/aspects/AspectList;)V");
                logger.debug("Injecting patches into method {}.{}{}", className, method.name, method.desc);
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
