package com.wonginnovations.oldresearch.common.research;

import java.util.*;
import java.util.stream.Collectors;

import com.artur114.bananalib.math.BananaMath;
import com.artur114.bananalib.mc.BananaMC;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.utils.HexUtils;

public class ResearchNoteData {
    public HashMap<String, OldResearchManager.HexEntry> hexEntries = new HashMap<>();
    public HashMap<String, HexUtils.Hex> hexes = new HashMap<>();
    public AspectList aspects = new AspectList();
    public int mergedTeories;
    public boolean complete;
    public boolean generic;
    public String key;
    public int copies;
    public int color;

    public boolean isComplete() {
        return this.complete;
    }

    public void generateHexesFromComp(Random rand, AspectList aspects, int complexity) {
        int radius = 2 + Math.min(2, complexity / 3);
        int blanks = complexity > 1 ? complexity : 0;
        this.generateHexes(rand, aspects, null, radius, blanks);
    }

    public void generateHexes(Random rand, AspectList aspects, @Nullable ResearchNotePattern meta, int radius, int blanks) {
        ArrayList<HexUtils.Hex> outerRing;

        if (meta != null && meta.hasMeta("SEPARATED")) {
            outerRing = this.distributeRingRandomlySep(radius, aspects.size(), rand);
        } else {
            outerRing = this.distributeRingRandomly(radius, aspects.size(), rand);
        }

        HashMap<String, HexUtils.Hex> hexLocations = HexUtils.generateHexes(radius);
        this.aspects = aspects;

        for (HexUtils.Hex hex : hexLocations.values()) {
            this.hexEntries.put(hex.toString(), new OldResearchManager.HexEntry(null, 0));
            this.hexes.put(hex.toString(), hex);
        }

        for (int i = 0; i != Math.min(outerRing.size(), aspects.size()); i++) {
            HexUtils.Hex hex = outerRing.get(i);
            this.hexEntries.put(hex.toString(), new OldResearchManager.HexEntry(aspects.getAspects()[i], 1));
            this.hexes.put(hex.toString(), hex);
        }

        if (meta != null && (meta.hasMeta("ROUND") || meta.hasMeta("ROUNDEX"))) {
            HexUtils.Hex[] temp = this.hexes.values().toArray(new HexUtils.Hex[0]);
            Set<String> ring = HexUtils.getRing(radius).stream().map(HexUtils.Hex::toString).collect(Collectors.toSet());
            if (meta.hasMeta("ROUND")) {
                ring.addAll(HexUtils.getRing(radius - 1).stream().map(HexUtils.Hex::toString).collect(Collectors.toSet()));
            }
            for (HexUtils.Hex hex : temp) {
                if (!ring.contains(hex.toString())) {
                    this.hexes.remove(hex.toString());
                    this.hexEntries.remove(hex.toString());
                }
            }
        } else {
            if (blanks > 0) {
                List<HexUtils.Hex> hexes = new LinkedList<>(this.hexes.values());
                hexes.removeIf((hex) -> {
                    OldResearchManager.HexEntry entry = this.hexEntries.get(hex.toString());
                    if (entry != null && entry.type == 0) {
                        for (int n = 0; n != 6; n++) {
                            HexUtils.Hex neighbour = hex.getNeighbour(n);
                            OldResearchManager.HexEntry neighbourEntry = this.hexEntries.get(neighbour.toString());
                            if (neighbourEntry != null && neighbourEntry.type == 1) {
                                return true;
                            }
                        }

                        return false;
                    }
                    return true;
                });

                for (int i = 0; i != blanks; i++) {
                    HexUtils.Hex hex = hexes.get(rand.nextInt(hexes.size()));
                    this.hexes.remove(hex.toString());
                    this.hexEntries.remove(hex.toString());
                    hexes.remove(hex);

                    if (hexes.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

    private ArrayList<HexUtils.Hex> distributeRingRandomlySep(int radius, int entries, Random random) {
        ArrayList<HexUtils.Hex> ring = HexUtils.getRing(radius);
        ArrayList<HexUtils.Hex> results = new ArrayList<>();
        float spacing = (float) ring.size() / (float) (entries + 4);
        float pos = (float) random.nextInt(ring.size());

        for(int i = 0; i < entries; i++) {
            int index = Math.round(pos);
            results.add(ring.get(index >= ring.size() ? ring.size() - 1 : index));
            pos += spacing;
            if (i == 0 || i == entries / 2) {
                pos += spacing * 2;
            }
            pos %= ring.size();
        }

        return results;
    }

    private ArrayList<HexUtils.Hex> distributeRingRandomly(int radius, int entries, Random random) {
        ArrayList<HexUtils.Hex> ring = HexUtils.getRing(radius);
        ArrayList<HexUtils.Hex> results = new ArrayList<>();
        float spacing = (float) ring.size() / (float) entries;
        float pos = (float)random.nextInt(ring.size());

        for(int i = 0; i < entries; i++) {
            int index = Math.round(pos);
            results.add(ring.get(index >= ring.size() ? ring.size() - 1 : index));
            pos += spacing;
            if (pos >= (float)ring.size()) {
                pos -= (float)ring.size();
            }
        }

        return results;
    }

}
