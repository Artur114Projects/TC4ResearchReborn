package com.wonginnovations.oldresearch.asm.transform;

import com.artur114.bananalib.asm.AbstractASMTransformer;
import com.artur114.bananalib.asm.BananaASM;
import com.artur114.bananalib.asm.patterns.InsnPatBuilder;
import com.artur114.bananalib.asm.patterns.InsnPattern;
import com.artur114.bananalib.asm.patterns.MethodPattern;
import com.artur114.bananalib.asm.tree.ClassNodeAdv;
import com.artur114.bananalib.asm.util.IASMLogger;
import com.artur114.bananalib.asm.util.InsnBuilder;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

public class RenderEventHandlerTransformer extends AbstractASMTransformer {

    public RenderEventHandlerTransformer() {
        super("thaumcraft.client.lib.events.RenderEventHandler");
    }

    @Override
    public byte[] transform(IASMLogger logger, String className, byte[] bytecode) {
        ClassReader reader = new ClassReader(bytecode);
        ClassNodeAdv clazz = BananaASM.createClassNode(reader);
        this.transform(logger, className, clazz);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES) {
            @Override
            protected String getCommonSuperClass(String type1, String type2) {
                return "java/lang/Object";
            }
        };
        clazz.accept(writer);
        return writer.toByteArray();
    }

    @Override
    protected ClassNodeAdv transform(IASMLogger logger, String className, ClassNodeAdv clazz) {
        boolean def = FMLLaunchHandler.isDeobfuscatedEnvironment();

        clazz.findMethod("renderLast").ifPresent(method -> {
            InsnPatBuilder builder = InsnPattern.builder();
            InsnBuilder insn = new InsnBuilder();

            builder.thenVarInsn(ALOAD, 4);
            builder.thenMethodInsn(INVOKEVIRTUAL, "thaumcraft/client/lib/events/WandRenderingHandler", "handleArchitectOverlay", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;FILnet/minecraft/util/math/RayTraceResult;)Z", false);
            builder.thenInsn(POP);
            builder.thenFieldInsn(GETSTATIC, "thaumcraft/client/lib/events/RenderEventHandler", "thaumTarget", "Lnet/minecraft/entity/Entity;");
            builder.then(JUMP_INSN.withOpcode(IFNULL));

            method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                if (interval.end().getNext() instanceof VarInsnNode) {
                    return;
                }
                insn.varInsn(ALOAD, 3);
                insn.fieldInsn(GETSTATIC, "thaumcraft/client/lib/events/RenderEventHandler", "thaumTarget", "Lnet/minecraft/entity/Entity;");
                insn.invokeStatic("thaumcraft/api/research/ScanningManager", "isThingStillScannable", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/Object;)Z");
                insn.jumpInsn(IFNE, ((JumpInsnNode) interval.end()).label);
                logger.debug("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                method.instructions.insert(interval.end(), insn.build());
            });
        });
        
        clazz.findMethod(MethodPattern.from("tooltipEvent", "(Lnet/minecraftforge/event/entity/player/ItemTooltipEvent;)V")).ifPresent(method -> {
            InsnPatBuilder builder = InsnPattern.builder();
            InsnBuilder insn = new InsnBuilder();

            builder.then(JUMP_INSN.withOpcode(IFEQ));
            builder.thenVarInsn(ALOAD, 2);
            builder.thenInsn(POP);
            builder.thenMethodInsn(INVOKESTATIC, "net/minecraft/client/gui/GuiScreen", def ? "isShiftKeyDown" : "func_146272_n", "()Z", false);
            builder.thenFieldInsn(GETSTATIC, "thaumcraft/common/config/ModConfig$CONFIG_GRAPHICS", "showTags", "Z");
            builder.then(JUMP_INSN.withOpcode(IF_ICMPEQ));
            builder.thenMethodInsn(INVOKESTATIC, "org/lwjgl/input/Mouse", "isGrabbed", "()Z", false);
            builder.then(JUMP_INSN.withOpcode(IFNE));
            builder.thenVarInsn(ALOAD, 0);
            builder.thenMethodInsn(INVOKEVIRTUAL, "net/minecraftforge/event/entity/player/ItemTooltipEvent", "getItemStack", "()Lnet/minecraft/item/ItemStack;", false);
            builder.then(JUMP_INSN.withOpcode(IFNULL));

            method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                AbstractInsnNode node = interval.end().getNext();
                if (node instanceof VarInsnNode) {
                    return;
                }
                insn.varInsn(ALOAD, 0);
                insn.invokeVirtual("net/minecraftforge/event/entity/player/ItemTooltipEvent", "getEntityPlayer", "()Lnet/minecraft/entity/player/EntityPlayer;", false);
                insn.varInsn(ALOAD, 0);
                insn.invokeVirtual("net/minecraftforge/event/entity/player/ItemTooltipEvent", "getItemStack", "()Lnet/minecraft/item/ItemStack;", false);
                insn.invokeStatic("thaumcraft/api/research/ScanningManager", "isThingStillScannable", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/Object;)Z");
                insn.jumpInsn(IFNE, ((JumpInsnNode) interval.end()).label);
                logger.debug("Injecting patches into method {}.{}{}", className, method.name, method.desc);
                method.instructions.insert(interval.end(), insn.build());
            });
        });

        clazz.findMethod(MethodPattern.from("tooltipEvent", "(Lnet/minecraftforge/client/event/RenderTooltipEvent$PostBackground;)V")).ifPresent(method -> {
            InsnPatBuilder builder = InsnPattern.builder();
            InsnBuilder insn = new InsnBuilder();

            builder.then(JUMP_INSN.withOpcode(IFEQ));
            builder.thenVarInsn(ALOAD, 2);
            builder.thenInsn(POP);
            builder.thenMethodInsn(INVOKESTATIC, "net/minecraft/client/gui/GuiScreen", def ? "isShiftKeyDown" : "func_146272_n", "()Z", false);
            builder.thenFieldInsn(GETSTATIC, "thaumcraft/common/config/ModConfig$CONFIG_GRAPHICS", "showTags", "Z");
            builder.then(JUMP_INSN.withOpcode(IF_ICMPEQ));
            builder.thenMethodInsn(INVOKESTATIC, "org/lwjgl/input/Mouse", "isGrabbed", "()Z", false);
            builder.then(JUMP_INSN.withOpcode(IFNE));

            method.instructions.findPattern(builder.build(), 0).ifPresent(interval -> {
                AbstractInsnNode node = interval.end().getNext();
                if (node instanceof VarInsnNode) {
                    return;
                }
                insn.varInsn(ALOAD, 1);
                insn.fieldInsn(GETFIELD, "net/minecraft/client/Minecraft", def ? "player" : "field_71439_g", "Lnet/minecraft/client/entity/EntityPlayerSP;");
                insn.varInsn(ALOAD, 0);
                insn.invokeVirtual("net/minecraftforge/client/event/RenderTooltipEvent$PostBackground", "getStack", "()Lnet/minecraft/item/ItemStack;", false);
                insn.invokeStatic("thaumcraft/api/research/ScanningManager", "isThingStillScannable", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/lang/Object;)Z");
                insn.jumpInsn(IFNE, ((JumpInsnNode) interval.end()).label);
                logger.debug("Injecting patches into method {}.{}{}", className, method.name, method.desc);
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
