package com.tutien.common.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LitematicParser {

    /**
     * Tải và đặt cấu trúc từ file .litematic vào thế giới Minecraft tại vị trí pos.
     */
    public static int placeLitematic(ServerLevel level, File file, BlockPos origin) {
        if (!file.exists()) {
            return 0;
        }
        try (InputStream stream = new FileInputStream(file)) {
            return placeLitematic(level, stream, origin);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Tải và đặt cấu trúc từ InputStream (resource trong JAR hoặc file) vào thế giới Minecraft tại vị trí pos.
     * Trả về số lượng block đã được đặt thành công.
     */
    public static int placeLitematic(ServerLevel level, InputStream stream, BlockPos origin) {
        try {
            CompoundTag root = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
            if (root == null || !root.contains("Regions", Tag.TAG_COMPOUND)) {
                return 0;
            }

            CompoundTag regionsTag = root.getCompound("Regions");
            int placedCount = 0;

            for (String regionName : regionsTag.getAllKeys()) {
                CompoundTag region = regionsTag.getCompound(regionName);

                CompoundTag sizeTag = region.getCompound("Size");
                int sizeX = sizeTag.getInt("x");
                int sizeY = sizeTag.getInt("y");
                int sizeZ = sizeTag.getInt("z");

                CompoundTag posTag = region.getCompound("Position");
                int regPosX = posTag.getInt("x");
                int regPosY = posTag.getInt("y");
                int regPosZ = posTag.getInt("z");

                // Parse Palette
                ListTag paletteTag = region.getList("BlockStatePalette", Tag.TAG_COMPOUND);
                List<BlockState> palette = new ArrayList<>();
                for (int i = 0; i < paletteTag.size(); i++) {
                    CompoundTag stateTag = paletteTag.getCompound(i);
                    palette.add(parseBlockState(stateTag));
                }

                if (palette.isEmpty()) continue;

                // Bit packing
                long[] blockStates = region.getLongArray("BlockStates");
                int paletteSize = palette.size();
                int bits = Math.max(2, 32 - Integer.numberOfLeadingZeros(paletteSize - 1));
                long mask = (1L << bits) - 1;

                int absX = Math.abs(sizeX);
                int absY = Math.abs(sizeY);
                int absZ = Math.abs(sizeZ);
                int startX = sizeX < 0 ? regPosX + sizeX + 1 : regPosX;
                int startY = sizeY < 0 ? regPosY + sizeY + 1 : regPosY;
                int startZ = sizeZ < 0 ? regPosZ + sizeZ + 1 : regPosZ;

                int totalVolume = absX * absY * absZ;

                for (int index = 0; index < totalVolume; index++) {
                    int paletteIndex = 0;
                    if (bits > 0 && blockStates.length > 0) {
                        int startBit = index * bits;
                        int startLong = startBit / 64;
                        int bitOffset = startBit % 64;

                        if (startLong < blockStates.length) {
                            long val = (blockStates[startLong] >>> bitOffset);
                            if (bitOffset + bits > 64 && startLong + 1 < blockStates.length) {
                                val |= (blockStates[startLong + 1] << (64 - bitOffset));
                            }
                            paletteIndex = (int) (val & mask);
                        }
                    }

                    if (paletteIndex >= 0 && paletteIndex < palette.size()) {
                        BlockState stateToPlace = palette.get(paletteIndex);
                        if (!stateToPlace.isAir()) {
                            // Coordinate calculation in region
                            int ry = index / (absX * absZ);
                            int rem = index % (absX * absZ);
                            int rz = rem / absX;
                            int rx = rem % absX;

                            BlockPos worldPos = origin.offset(startX + rx, startY + ry, startZ + rz);
                            level.setBlock(worldPos, stateToPlace, 2);
                            placedCount++;
                        }
                    }
                }
            }

            return placedCount;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState parseBlockState(CompoundTag tag) {
        String name = tag.getString("Name");
        ResourceLocation id = ResourceLocation.tryParse(name);
        if (id == null) return Blocks.AIR.defaultBlockState();

        Block block = BuiltInRegistries.BLOCK.get(id);
        if (block == Blocks.AIR) return Blocks.AIR.defaultBlockState();

        BlockState state = block.defaultBlockState();
        if (tag.contains("Properties", Tag.TAG_COMPOUND)) {
            CompoundTag props = tag.getCompound("Properties");
            for (String key : props.getAllKeys()) {
                Property<?> property = block.getStateDefinition().getProperty(key);
                if (property != null) {
                    String valueStr = props.getString(key);
                    var opt = property.getValue(valueStr);
                    if (opt.isPresent()) {
                        state = state.setValue((Property) property, (Comparable) opt.get());
                    }
                }
            }
        }
        return state;
    }
}
