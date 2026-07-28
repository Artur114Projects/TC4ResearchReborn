package com.wonginnovations.oldresearch.common.research;

import thaumcraft.api.aspects.AspectList;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ResearchNotePattern {
    private final String targetResearch;
    private final int targetStage;
    private final AspectList aspects;
    private final Set<String> meta;
    private final int radius;
    private final int blanks;
    private final int hashDelta;
    private final int color;

    public ResearchNotePattern(String targetResearch, int targetStage, AspectList aspects, int radius, int blanks, int color, int hashDelta, String[] meta) {
        this.targetResearch = targetResearch;
        this.targetStage = targetStage;
        this.aspects = aspects;
        this.hashDelta = hashDelta;
        this.radius = radius;
        this.blanks = blanks;
        this.color = color;
        this.meta = new HashSet<>(Arrays.asList(meta));
    }

    public boolean hasMeta(String meta) {
        return this.meta.contains(meta);
    }

    public int color() {
        return this.color;
    }

    public String oldResKey() {
        return "rn_" + this.targetResearch + "_" + this.targetStage;
    }

    public AspectList aspects() {
        return this.aspects.copy();
    }

    public int radius() {
        return this.radius;
    }

    public int blanks() {
        return this.blanks;
    }

    public long seed() {
        return 31 * (31 * (31 * 2880321 + this.radius) + this.blanks) + this.hashDelta;
    }
}
