package com.wonginnovations.oldresearch.common.research;

import com.artur114.bananalib.mc.BananaMC;
import com.artur114.bananalib.util.graphs.BananaGraphs;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wonginnovations.oldresearch.api.OldResearchApi;
import com.wonginnovations.oldresearch.asm.Reflector;
import com.wonginnovations.oldresearch.common.config.OldConfig;
import com.wonginnovations.oldresearch.common.items.ItemResearchNote;
import com.wonginnovations.oldresearch.common.init.InitItems;
import com.wonginnovations.oldresearch.main.OldResearch;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.lang3.ArrayUtils;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.HexUtils;
import thaumcraft.common.lib.utils.InventoryUtils;

public class OldResearchManager {
    protected static final Map<String, ItemStack> NOTES = new LinkedHashMap<>();
    private static final Map<String, List<String>> IMPLICIT_PARENTS = new HashMap<>();
    private static final Map<String, ResearchNotePattern> NOTE_PATTERNS = new HashMap<>();
    public static final Map<Aspect, Integer> ASPECT_COMPLEXITY = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static void registerNotePattern(ResearchNotePattern pattern) {
        NOTE_PATTERNS.put(pattern.oldResKey(), pattern);
    }

    public static void registerImplicitParents(String research, String... parents) {
        IMPLICIT_PARENTS.put(research, Arrays.asList(parents));
    }

    public static void registerNotePatterns(List<ResearchNotePattern> patterns) {
        patterns.forEach(OldResearchManager::registerNotePattern);
    }

    public static void computeAspectComplexity() {
        for (Aspect aspect : Aspect.aspects.values()) {
            ASPECT_COMPLEXITY.put(aspect, computeAspectComplexity(aspect, 0));
        }
    }

    public static void patchResearch() {
        ResearchCategories.getResearchCategory("BASICS").research.remove("KNOWLEDGETYPES");
        ResearchCategories.getResearchCategory("BASICS").research.remove("THEORYRESEARCH");
        ResearchCategories.getResearchCategory("BASICS").research.remove("CELESTIALSCANNING");//TODO: Подумать

        OldResearchManager.loadJsonResearchDirect(OldResearch.loc("research.json"));

        int patchedStages = 0;
        int patchedResearches = 0;
        for (ResearchCategory category : ResearchCategories.researchCategories.values()) {
            for (ResearchEntry entry : category.research.values()) {
                ResearchStage[] stages = entry.getStages();
                patchedResearches++;
                for (int i = 0; i != stages.length; i++) {
                    ResearchStage stage = stages[i];
                    patchedStages++;

                    if (stage == null || stage.getKnow() == null) {
                        continue;
                    }

                    int theoryCount = 0;
                    for (ResearchStage.Knowledge knowledge : stage.getKnow()) {
                        if (knowledge.type == IPlayerKnowledge.EnumKnowledgeType.THEORY) {
                            theoryCount++;
                        }
                    }

                    stage.setKnow(null);

                    if (theoryCount == 0) {
                        continue;
                    }

                    String key = "rn_" + entry.getKey() + "_" + i;
                    stage.setResearch(ArrayUtils.add(stage.getResearch(), key));
                    NOTES.put(key, createNote(key, theoryCount));

                    if (stage.getResearchIcon() == null) {
                        stage.setResearchIcon(new String[] {null});
                    } else {
                        stage.setResearchIcon(ArrayUtils.add(stage.getResearchIcon(), null));
                    }
                }
            }
        }

        //TODO: Увеличить до 4 символов
        OldResearch.LOGGER.info("#################################################");
        OldResearch.LOGGER.info("#         Welcome to Old Research: Reborn!      #");
        OldResearch.LOGGER.info("#       Patched {} stages in {} researches    #", parseInt(patchedStages), parseInt(patchedResearches));
        OldResearch.LOGGER.info("#         And created {} research notes        #", parseInt(NOTES.size()));
        OldResearch.LOGGER.info("#################################################");
    }

    private static String parseInt(int i) {
        String p = i + "";
        switch (p.length()) {
            case 1:
                return " " + p + " ";
            case 2:
                return " " + p;
            default:
                return p;
        }
    }

    private static int computeAspectComplexity(Aspect aspect, int depth) {
        if (aspect.isPrimal()) return depth;
        ArrayList<Integer> childDepths = new ArrayList<>();
        for (Aspect asp : aspect.getComponents()) {
            childDepths.add(computeAspectComplexity(asp, depth + 1));
        }
        return Collections.max(childDepths);
    }

    public static int getAspectComplexity(Aspect a) {
        return ASPECT_COMPLEXITY.get(a);
    }

    public static List<String> parentsOfResearch(String research) {
        ResearchEntry res = ResearchCategories.getResearch(research);

        if (res != null) {
            String[] parents = res.getParentsClean();

            if (parents != null) {
                List<String> ret = new ArrayList<>(Arrays.asList(parents));
                List<String> implicit = IMPLICIT_PARENTS.get(research);

                if (implicit != null) {
                    ret.addAll(implicit);
                }

                return ret;
            }
        }

        return Collections.emptyList();
    }

    public static AspectList getRandomAspects(Random rand, int maxComplexity, int quantity) {
        List<Aspect> possible = ASPECT_COMPLEXITY.keySet().stream().filter(aspect -> {
            int comp = ASPECT_COMPLEXITY.get(aspect);
            return comp <= maxComplexity && comp >= MathHelper.clamp((maxComplexity / 4) - 2, 0, 2);
        }).collect(Collectors.toList());
        AspectList selected = new AspectList();
        int upto = Math.min(quantity, possible.size());
        for (int i = 0; i < upto; i++) {
            int toadd = rand.nextInt(possible.size());
            selected.add(possible.get(toadd), 1);
            possible.remove(toadd);
        }

        return selected;
    }

    private static ItemStack createNote(String key, int teoriesCount) {
        ItemStack note = new ItemStack(InitItems.RESEARCH_NOTE);
        ResearchNoteData data = new ResearchNoteData();
        data.key = key;
        data.mergedTeories = teoriesCount;
        ResearchNotePattern pattern = NOTE_PATTERNS.get(key);
        if (pattern != null) {
            data.color = pattern.color();
        } else {
            Aspect[] asps = Aspect.aspects.values().toArray(new Aspect[0]);
            RANDOM.setSeed(key.hashCode());
            data.color = asps[RANDOM.nextInt(asps.length)].getColor();
        }
        ItemResearchNote.setNoteData(note, data);
        return note;
    }

    public static List<ItemStack> allNotes() {
        List<ItemStack> list = new ArrayList<>(NOTES.size());
        for (ItemStack stack : NOTES.values()) list.add(stack.copy());
        return list;
    }

    public static ItemStack noteStack(String key) {
        ItemStack stack = NOTES.get(key);
        if (stack == null) return null;
        return stack.copy();
    }

    public static int getResearchComplexity(String key) {
        AtomicInteger ret = new AtomicInteger();
        BananaGraphs.bfs(OldResearchManager.getStrippedKey(key), OldResearchManager::parentsOfResearch, (res) -> {
            ResearchEntry research = ResearchCategories.getResearch(res);
            if (research != null) {
                for (ResearchStage stage : research.getStages()) {
                    if (stage == null || stage.getResearch() == null) {
                        continue;
                    }
                    int comp = 0;
                    for (String s : stage.getResearch()) {
                        if (s.startsWith("rn_")) {
                            comp += OldResearchManager.NOTES.get(s).getTagCompound().getInteger("mergedTeories");
                        }
                    }
                    ret.addAndGet(comp);
                }
            }

            return false;
        });
        return (int) ((ret.get() + 1) * OldConfig.researchDifficultyMultiplier);
    }

    public static void givePlayerResearchNote(World world, EntityPlayer player, String key) {
        if (hasResearchNote(player, key)) {
            return;
        }
        if (!player.isCreative() && (!consumeInkFromPlayer(player, false) || !InventoryUtils.consumePlayerItem(player, new ItemStack(Items.PAPER), false, true))) {
            return;
        }

        ItemStack note = noteStack(key);

        if (note == null) {
            return;
        }

        consumeInkFromPlayer(player, true);
        ItemResearchNote.setNoteData(note, computeNoteData(world, key));

        if (!player.inventory.addItemStackToInventory(note)) {
            ForgeHooks.onPlayerTossEvent(player, note, false);
        }

        player.inventoryContainer.detectAndSendChanges();
        world.playSound(null, player.posX, player.posY, player.posZ, SoundsTC.write, SoundCategory.PLAYERS, 0.75F, 1.0F);
    }

    public static boolean playerHasInc(EntityPlayer player, boolean sendMessage) {
        if (player.isCreative()) {
            return true;
        }
        if (!BananaMC.inventoryContains(player, (stack) -> stack.getItem() instanceof IScribeTools)) {
            if (sendMessage) player.sendMessage(new TextComponentString("§c" + I18n.format("tile.researchtable.need.st")));
            return false;
        }
        if (!BananaMC.inventoryContains(player, Items.PAPER)) {
            if (sendMessage) player.sendMessage(new TextComponentString("§c" + I18n.format("tile.researchtable.need.paper")));
            return false;
        }
        if (!OldResearchManager.consumeInkFromPlayer(player, false)) {
            if (sendMessage) player.sendMessage(new TextComponentString("§c" + I18n.format("tile.researchtable.noink.0")));
            if (sendMessage) player.sendMessage(new TextComponentString("§c" + I18n.format("tile.researchtable.noink.1")));
            return false;
        }
        return true;
    }

    public static ResearchNoteData computeNoteData(World world, String key) {
        ResearchNotePattern pattern = NOTE_PATTERNS.get(key);
        ResearchNoteData note;

        if (pattern != null) {
            note = computeNoteDataFromPattern(pattern, key);
        } else {
            note = computeNoteDataFromRandom(world, key);
        }

        return note;
    }

    private static ResearchNoteData computeNoteDataFromPattern(ResearchNotePattern pattern, String key) {
        ResearchNoteData data = ItemResearchNote.noteData(noteStack(key));
        if (data == null) return null;
        Random rand = new Random((31 * pattern.seed()) + key.hashCode());
        data.generateHexes(rand, pattern.aspects(), pattern, pattern.radius(), pattern.blanks());
        return data;
    }

    private static ResearchNoteData computeNoteDataFromRandom(World world, String key) {
        ResearchNoteData data = ItemResearchNote.noteData(noteStack(key));
        if (data == null) return null;
        Random rand = new Random(31 * ((31 * world.getSeed()) + key.hashCode()) + data.color);
        int complexity = getResearchComplexity(key) + data.mergedTeories;
        int complexityClamped = MathHelper.clamp(complexity, 0, 12);
        AspectList aspects = getRandomAspects(rand, complexity, Math.min(11, complexityClamped + 2));
        data.generateHexesFromComp(rand, aspects, complexityClamped);
        return data;
    }

    public static boolean consumeInkFromPlayer(EntityPlayer player, boolean doIt) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() instanceof IScribeTools && stack.getItemDamage() < stack.getMaxDamage()) {
                if (doIt) stack.damageItem(1, player);
                return true;
            }
        }
        return false;
    }

    public static boolean hasResearchNote(EntityPlayer player, String key) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == InitItems.RESEARCH_NOTE && stack.getTagCompound() != null && stack.getTagCompound().getString("key").equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static String getStrippedKey(ItemStack stack) {
        return (stack.getTagCompound() != null)? getStrippedKey(stack.getTagCompound().getString("key")) : null;
    }

    public static String getStrippedKey(String key) {
        return key.substring(key.indexOf('_') + 1, key.lastIndexOf('_'));
    }

    public static boolean checkResearchCompletion(ItemStack contents, ResearchNoteData note, EntityPlayer player) {
        ArrayList<String> checked = new ArrayList<>();
        ArrayList<String> main = new ArrayList<>();
        ArrayList<String> remains = new ArrayList<>();

        for (HexUtils.Hex hex : note.hexes.values()) {
            if(note.hexEntries.get(hex.toString()).type == 1) {
                main.add(hex.toString());
            }
        }

        for (HexUtils.Hex hex : note.hexes.values()) {
            if(note.hexEntries.get(hex.toString()).type == 1) {
                main.remove(hex.toString());
                checkConnections(note, hex, checked, main, remains, player);
                break;
            }
        }

        if (!main.isEmpty()) {
            return false;
        } else {
            ArrayList<String> remove = new ArrayList<>();

            for (HexUtils.Hex hex : note.hexes.values()) {
                if(note.hexEntries.get(hex.toString()).type != 1 && !remains.contains(hex.toString())) {
                    remove.add(hex.toString());
                }
            }

            for (String s : remove) {
                note.hexEntries.remove(s);
                note.hexes.remove(s);
            }

            note.complete = true;
            ItemResearchNote.setNoteData(contents, note);
            return true;
        }
    }

    private static void checkConnections(ResearchNoteData note, HexUtils.Hex hex, ArrayList<String> checked, ArrayList<String> main, ArrayList<String> remains, EntityPlayer player) {
        checked.add(hex.toString());

        for (int a = 0; a < 6; ++a) {
            HexUtils.Hex target = hex.getNeighbour(a);
            if (!checked.contains(target.toString()) && note.hexEntries.containsKey(target.toString()) && note.hexEntries.get(target.toString()).type >= 1) {
                Aspect aspect1 = note.hexEntries.get(hex.toString()).aspect;
                Aspect aspect2 = note.hexEntries.get(target.toString()).aspect;
                if (OldResearchApi.oldResStorage(player).isKnowAspect(aspect1) && OldResearchApi.oldResStorage(player).isKnowAspect(aspect2) && (!aspect1.isPrimal() && (aspect1.getComponents()[0] == aspect2 || aspect1.getComponents()[1] == aspect2) || !aspect2.isPrimal() && (aspect2.getComponents()[0] == aspect1 || aspect2.getComponents()[1] == aspect1))) {
                    remains.add(target.toString());
                    if (note.hexEntries.get(target.toString()).type == 1) {
                        main.remove(target.toString());
                    }

                    checkConnections(note, target, checked, main, remains, player);
                }
            }
        }

    }

    public static Aspect getCombinationResult(Aspect aspect1, Aspect aspect2) {
        for (Aspect aspect : Aspect.aspects.values()) {
            if (aspect.getComponents() != null && (aspect.getComponents()[0] == aspect1 && aspect.getComponents()[1] == aspect2 || aspect.getComponents()[0] == aspect2 && aspect.getComponents()[1] == aspect1)) {
                return aspect;
            }
        }

        return null;
    }

    public static void loadJsonResearchDirect(ResourceLocation loc) {
        JsonParser parser = new JsonParser();
        String s = "/assets/" + loc.getNamespace() + "/" + loc.getPath();
        InputStream stream = OldResearchManager.class.getResourceAsStream(s);
        if (stream != null) {
            try {
                InputStreamReader reader = new InputStreamReader(stream);
                JsonObject obj = parser.parse(reader).getAsJsonObject();
                JsonArray entries = obj.get("entries").getAsJsonArray();
                int a = 0;

                for (JsonElement element : entries) {
                    try {
                        JsonObject entry = element.getAsJsonObject();
                        ResearchEntry researchEntry = Reflector.invokeMethod(ResearchManager.class, null, "parseResearchJson", new Class<?>[] {JsonObject.class}, new Object[] {entry});
                        if (researchEntry != null && ResearchCategories.getResearchCategory(researchEntry.getCategory()) != null) {
                            Reflector.invokeMethod(ResearchManager.class, null, "addResearchToCategory", new Class<?>[] {ResearchEntry.class}, new Object[] {researchEntry});
                        }
                        a++;
                    } catch (Exception var13) {
                        Thaumcraft.log.warn("Invalid research entry [{}] found in {}", a, loc, var13);
                    }
                }

                Thaumcraft.log.info("Loaded {} research entries from {}", a, loc);
            } catch (Exception var14) {
                Thaumcraft.log.warn("Invalid research file: {}", loc);
            }
        } else {
            Thaumcraft.log.warn("Research file not found: {}", loc);
        }
    }

    public static class HexEntry {
        public Aspect aspect;
        public int type;

        public HexEntry(Aspect aspect, int type) {
            this.aspect = aspect;
            this.type = type;
        }
    }
}