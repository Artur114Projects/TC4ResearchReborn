package com.wonginnovations.oldresearch.common.util;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class AspectUtils {
    public static AspectList reduceToPrimals(AspectList al) {
        return reduceToPrimals(al, false);
    }

    public static AspectList reduceToPrimals(AspectList al, boolean merge) {
        AspectList out = new AspectList();

        for(Aspect aspect : al.getAspects()) {
            if (aspect != null) {
                if (aspect.isPrimal()) {
                    if (merge) {
                        out.merge(aspect, al.getAmount(aspect));
                    } else {
                        out.add(aspect, al.getAmount(aspect));
                    }
                } else {
                    AspectList send = new AspectList();
                    send.add(aspect.getComponents()[0], al.getAmount(aspect));
                    send.add(aspect.getComponents()[1], al.getAmount(aspect));
                    send = reduceToPrimals(send, merge);

                    for(Aspect a : send.getAspects()) {
                        if (merge) {
                            out.merge(a, send.getAmount(a));
                        } else {
                            out.add(a, send.getAmount(a));
                        }
                    }
                }
            }
        }

        return out;
    }
}
