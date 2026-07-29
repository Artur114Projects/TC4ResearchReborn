package com.wonginnovations.oldresearch.asm.transform;

import com.artur114.bananalib.asm.AbstractASMTransformer;
import com.artur114.bananalib.asm.patterns.InsnPatBuilder;
import com.artur114.bananalib.asm.patterns.InsnPattern;
import com.artur114.bananalib.asm.tree.ClassNodeAdv;
import com.artur114.bananalib.asm.util.IASMLogger;
import com.artur114.bananalib.asm.util.InsnBuilder;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class HandlerScanSlotTransformer extends AbstractASMTransformer {

    public HandlerScanSlotTransformer() {
        super("net.blay09.mods.tcinventoryscan.net.HandlerScanSlot");
    }

    @Override
    protected ClassNodeAdv transform(IASMLogger logger, String className, ClassNodeAdv clazz) {
        clazz.findMethod("onMessage").ifPresent(method -> {
            InsnPatBuilder builder = InsnPattern.builder();
            InsnBuilder insn = new InsnBuilder();

            builder.thenMethodInsn(INVOKESTATIC, "com/wonginnovations/oldresearch/common/research/ScanManager", "canScanThing", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/Object;Z)Z", false);
            if (!method.instructions.findPattern(builder.build()).isEmpty()) {
                return;
            }

            builder.thenMethodInsn(INVOKESTATIC, "thaumcraft/api/research/ScanningManager", "scanTheThing", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/Object;)V", false);

            method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                insn.insn(ICONST_1);
                insn.invokeStatic("com/wonginnovations/oldresearch/common/research/ScanManager", "canScanThing", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/Object;Z)Z");
                insn.thenIf((i) -> {
                    insn.loadVars("A:3", "A:6");
                    insn.invokeStatic("thaumcraft/api/research/ScanningManager", "scanTheThing", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/Object;)V");
                });
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
