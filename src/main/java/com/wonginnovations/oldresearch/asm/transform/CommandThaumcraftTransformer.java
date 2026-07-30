package com.wonginnovations.oldresearch.asm.transform;

import com.artur114.bananalib.asm.AbstractASMTransformer;
import com.artur114.bananalib.asm.patterns.InsnPatBuilder;
import com.artur114.bananalib.asm.patterns.InsnPattern;
import com.artur114.bananalib.asm.tree.ClassNodeAdv;
import com.artur114.bananalib.asm.util.IASMLogger;
import com.artur114.bananalib.asm.util.InsnBuilder;
import org.objectweb.asm.tree.*;

public class CommandThaumcraftTransformer extends AbstractASMTransformer {

    public CommandThaumcraftTransformer() {
        super("thaumcraft.common.lib.CommandThaumcraft");
    }

    @Override
    protected ClassNodeAdv transform(IASMLogger logger, String className, ClassNodeAdv clazz) {
        clazz.findMethod("revokeResearch").ifPresent(method -> {
            InsnPatBuilder builder = InsnPattern.builder();
            InsnBuilder insn = new InsnBuilder();

            builder.thenVarInsn(ALOAD, 3);
            builder.thenMethodInsn(INVOKESTATIC, "thaumcraft/api/research/ResearchCategories", "getResearch", "(Ljava/lang/String;)Lthaumcraft/api/research/ResearchEntry;", false);
            builder.then(JUMP_INSN.withOpcode(IFNULL));

            method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                JumpInsnNode ifnullInsn = (JumpInsnNode) interval.end();
                LabelNode errorLabel = ifnullInsn.label;
                AbstractInsnNode successStart = ifnullInsn.getNext();
                LabelNode successLabel = new LabelNode();

                method.instructions.insertBefore(successStart, successLabel);

                ifnullInsn.setOpcode(IFNONNULL);
                ifnullInsn.label = successLabel;

                insn.varInsn(ALOAD, 3);
                insn.ldcInsn("rn_");
                insn.invokeVirtual("java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
                insn.jumpInsn(IFNE, successLabel);
                insn.jumpInsn(GOTO, errorLabel);

                logger.debug("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                method.instructions.insert(ifnullInsn, insn.build());
            });
        });
        return clazz;
    }

    @Override
    public int priority() {
        return 0;
    }
}
