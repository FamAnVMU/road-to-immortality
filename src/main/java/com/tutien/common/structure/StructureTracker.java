package com.tutien.common.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureTracker extends SavedData {
    private static final String DATA_NAME = "tutien_structures";
    private final Map<String, List<BlockPos>> structureLocations = new HashMap<>();

    public static StructureTracker get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                new Factory<>(StructureTracker::new, StructureTracker::load),
                DATA_NAME
        );
    }

    public static StructureTracker load(CompoundTag tag, HolderLookup.Provider provider) {
        StructureTracker tracker = new StructureTracker();
        if (tag.contains("Structures", Tag.TAG_COMPOUND)) {
            CompoundTag structMap = tag.getCompound("Structures");
            for (String key : structMap.getAllKeys()) {
                ListTag list = structMap.getList(key, Tag.TAG_COMPOUND);
                List<BlockPos> positions = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag pTag = list.getCompound(i);
                    positions.add(new BlockPos(pTag.getInt("x"), pTag.getInt("y"), pTag.getInt("z")));
                }
                tracker.structureLocations.put(key.toLowerCase(), positions);
            }
        }
        return tracker;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag structMap = new CompoundTag();
        for (Map.Entry<String, List<BlockPos>> entry : structureLocations.entrySet()) {
            ListTag list = new ListTag();
            for (BlockPos pos : entry.getValue()) {
                CompoundTag pTag = new CompoundTag();
                pTag.putInt("x", pos.getX());
                pTag.putInt("y", pos.getY());
                pTag.putInt("z", pos.getZ());
                list.add(pTag);
            }
            structMap.put(entry.getKey().toLowerCase(), list);
        }
        tag.put("Structures", structMap);
        return tag;
    }

    public void recordStructure(String name, BlockPos pos) {
        String key = name.toLowerCase();
        List<BlockPos> list = structureLocations.computeIfAbsent(key, k -> new ArrayList<>());
        if (!list.contains(pos)) {
            list.add(pos);
            setDirty();
        }
    }

    public BlockPos findNearest(String name, BlockPos currentPos) {
        String key = name.toLowerCase();
        List<BlockPos> list = structureLocations.get(key);
        if (list == null || list.isEmpty()) return null;

        BlockPos nearest = null;
        double minDst = Double.MAX_VALUE;
        for (BlockPos p : list) {
            double dst = p.distSqr(currentPos);
            if (dst < minDst) {
                minDst = dst;
                nearest = p;
            }
        }
        return nearest;
    }

    public boolean hasAnyStructureNearby(BlockPos pos, double minDistance) {
        double minDstSqr = minDistance * minDistance;
        for (List<BlockPos> list : structureLocations.values()) {
            for (BlockPos p : list) {
                if (p.distSqr(pos) < minDstSqr) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getTotalStructureCount() {
        int count = 0;
        for (List<BlockPos> list : structureLocations.values()) {
            count += list.size();
        }
        return count;
    }
}
