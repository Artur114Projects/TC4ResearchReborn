package com.wonginnovations.oldresearch.asm;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thaumcraft.common.items.tools.ItemThaumometer;

import java.lang.reflect.Method;

public class ReflectMethods {
    public static final Method IT_DRAW_FX;
    public static final Method IT_DO_SCAN;


    static {
        IT_DRAW_FX = Reflector.findMethod(ItemThaumometer.class, "drawFX", World.class, EntityPlayer.class);
        IT_DO_SCAN = Reflector.findMethod(ItemThaumometer.class, "doScan", World.class, EntityPlayer.class);
    }
}
