package com.wonginnovations.oldresearch.asm.transform;

import com.artur114.bananalib.asm.AbstractASMTransformer;
import com.artur114.bananalib.asm.patterns.InsnPatBuilder;
import com.artur114.bananalib.asm.patterns.InsnPattern;
import com.artur114.bananalib.asm.tree.ClassNodeAdv;
import com.artur114.bananalib.asm.tree.MethodNodeAdv;
import com.artur114.bananalib.asm.util.IASMLogger;
import com.artur114.bananalib.asm.util.InsnBuilder;
import com.wonginnovations.oldresearch.asm.ASMTransformerOldRes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.JumpInsnNode;

public class ItemThaumometerTransformer extends AbstractASMTransformer {

    public ItemThaumometerTransformer() {
        super("thaumcraft.common.items.tools.ItemThaumometer");
    }

    @Override
    protected ClassNodeAdv transform(IASMLogger logger, String className, ClassNodeAdv clazz) {
        boolean def = FMLLaunchHandler.isDeobfuscatedEnvironment();
        for (MethodNodeAdv node : clazz.methods) {
            if (node.name.equals(def ? "getMaxItemUseDuration" : "func_77626_a")) {
                return clazz;
            }
        }
        InsnBuilder insn = new InsnBuilder();

        MethodNodeAdv getMaxItemUseDuration = new MethodNodeAdv(ACC_PUBLIC, def ? "getMaxItemUseDuration" : "func_77626_a", "(Lnet/minecraft/item/ItemStack;)I", null, null);
        insn.intInsn(BIPUSH, 20);
        insn.insn(IRETURN);
        getMaxItemUseDuration.instructions.add(insn.build());
        clazz.methods.add(getMaxItemUseDuration);

        MethodNodeAdv getItemUseAction = new MethodNodeAdv(ACC_PUBLIC, def ? "getItemUseAction" : "func_77661_b", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/EnumAction;", null, null);
        insn.fieldInsn(GETSTATIC, "net/minecraft/item/EnumAction", "NONE", "Lnet/minecraft/item/EnumAction;");
        insn.insn(ARETURN);
        getItemUseAction.instructions.add(insn.build());
        clazz.methods.add(getItemUseAction);

        MethodNodeAdv onUsingTick = new MethodNodeAdv(ACC_PUBLIC, "onUsingTick", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;I)V", null, null);
        insn.loadVars("A:1", "A:2", "I:3");
        insn.invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookThaumometerUsingTick", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;I)V");
        insn.insn(RETURN);
        onUsingTick.instructions.add(insn.build());
        clazz.methods.add(onUsingTick);

        clazz.findMethod(def ? "onItemRightClick" : "func_77659_a").ifPresent(method -> {
            insn.loadVars("A:2", "A:3");
            insn.invokeStatic(ASMTransformerOldRes.HOOK_CLASS, "hookThaumometerRightClick", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHand;)Z");
            insn.thenIf((i) -> {
                i.typeInsn(NEW, "net/minecraft/util/ActionResult");
                i.insn(DUP);
                i.fieldInsn(GETSTATIC, "net/minecraft/util/EnumActionResult", "PASS", "Lnet/minecraft/util/EnumActionResult;");
                insn.loadVars("A:2", "A:3");
                i.invokeVirtual("net/minecraft/entity/player/EntityPlayer", def ? "getHeldItem" : "func_184586_b", "(Lnet/minecraft/util/EnumHand;)Lnet/minecraft/item/ItemStack;", false);
                i.invokeSpecial("net/minecraft/util/ActionResult", "<init>", "(Lnet/minecraft/util/EnumActionResult;Ljava/lang/Object;)V");
                i.insn(ARETURN);
            });
            method.instructions.insert(insn.build());
        });
        
        clazz.findMethod("doScan").ifPresent(method -> {
            InsnPatBuilder builder = InsnPattern.builder();

            builder.thenInsn(FCONST_0);
            builder.thenInsn(ICONST_1);
            builder.thenMethodInsn(INVOKESTATIC, "thaumcraft/common/lib/utils/EntityUtils", "getPointedEntity", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;DDFZ)Lnet/minecraft/entity/Entity;", false);
            builder.thenVarInsn(ASTORE, 3);
            builder.thenVarInsn(ALOAD, 3);
            builder.then(JUMP_INSN.withOpcode(IFNULL));

            method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                insn.varInsn(ALOAD, 2);
                insn.varInsn(ALOAD, 3);
                insn.insn(ICONST_1);
                insn.invokeStatic("com/wonginnovations/oldresearch/common/research/ScanManager", "canScanThing", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/Object;Z)Z");
                insn.jumpInsn(IFEQ, ((JumpInsnNode) interval.end()).label);
                method.instructions.insert(interval.end(), insn.build());
            });

            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/common/items/tools/ItemThaumometer", def ? "rayTrace" : "func_77621_a", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Z)Lnet/minecraft/util/math/RayTraceResult;", false);
            builder.thenVarInsn(ASTORE, 4);
            builder.thenVarInsn(ALOAD, 4);
            builder.then(JUMP_INSN.withOpcode(IFNULL));
            builder.thenVarInsn(ALOAD, 4);
            builder.thenMethodInsn(INVOKEVIRTUAL, "net/minecraft/util/math/RayTraceResult", def ? "getBlockPos" : "func_178782_a", "()Lnet/minecraft/util/math/BlockPos;", false);
            builder.then(JUMP_INSN.withOpcode(IFNULL));

            method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                insn.varInsn(ALOAD, 2);
                insn.varInsn(ALOAD, 4);
                insn.invokeVirtual("net/minecraft/util/math/RayTraceResult", def ? "getBlockPos" : "func_178782_a", "()Lnet/minecraft/util/math/BlockPos;", false);
                insn.insn(ICONST_1);
                insn.invokeStatic("com/wonginnovations/oldresearch/common/research/ScanManager", "canScanThing", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/Object;Z)Z");
                insn.jumpInsn(IFEQ, ((JumpInsnNode) interval.end()).label);
                method.instructions.insert(interval.end(), insn.build());
            });
        });

        return clazz;
    }

    @Override
    public int priority() {
        return 0;
    }
}
