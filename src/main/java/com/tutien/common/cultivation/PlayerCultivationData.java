package com.tutien.common.cultivation;

import com.tutien.common.config.TuTienConfig;
import com.tutien.common.skill.SkillInstance;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;

public class PlayerCultivationData implements INBTSerializable<CompoundTag> {
    private static final ResourceLocation HEALTH_MOD_ID = ResourceLocation.fromNamespaceAndPath("tutien", "realm_health");
    private static final ResourceLocation DAMAGE_MOD_ID = ResourceLocation.fromNamespaceAndPath("tutien", "realm_damage");
    private static final ResourceLocation ARMOR_MOD_ID = ResourceLocation.fromNamespaceAndPath("tutien", "realm_armor");

    private int realm = 0; // 0 = Luyện Khí
    private int layer = 1; // 1 to 9
    private double tuVi = 0.0;
    private double foundationConsolidation = 1.0; // 0.0 to 1.0
    private int highestRealm = 0;
    private int highestLayer = 1;

    private float lingQi = 100.0f;
    private boolean isMeditating = false;

    // Pill Buffs
    private int pillBuffTicks = 0;
    private double pillSpeedMultiplier = 1.0;
    private float pillDamageBonus = 0.0f;
    private float pillDefenseBonus = 0.0f;

    // Crafting Levels
    private int alchemyLevel = 1;
    private int alchemyExp = 0;
    private int refiningLevel = 1;
    private int refiningExp = 0;

    // Sect & Points
    private long congHuan = 0; // Merit points
    private long congHien = 0; // Sect contribution
    private String sectId = "none"; // "none", "thuong_van", "con_luan"

    // Skills
    private final Map<String, SkillInstance> learnedSkills = new HashMap<>();
    private final String[] equippedSkills = new String[]{"", "", "", ""};
    private final Map<String, Integer> cooldowns = new HashMap<>();

    public PlayerCultivationData() {
        this.lingQi = getMaxLingQi();
    }

    public Realm getRealm() {
        return Realm.fromId(this.realm);
    }

    public int getRealmId() {
        return realm;
    }

    public void setRealmId(int realm) {
        this.realm = Math.max(0, Math.min(8, realm));
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = Math.max(1, Math.min(9, layer));
    }

    public double getTuVi() {
        return tuVi;
    }

    public void setTuVi(double tuVi) {
        this.tuVi = Math.max(0.0, tuVi);
    }

    public void addTuVi(double amount) {
        this.tuVi += amount;
    }

    public double getFoundationConsolidation() {
        return foundationConsolidation;
    }

    public void setFoundationConsolidation(double foundationConsolidation) {
        this.foundationConsolidation = Math.max(0.0, Math.min(1.0, foundationConsolidation));
    }

    public int getHighestRealm() {
        return highestRealm;
    }

    public int getHighestLayer() {
        return highestLayer;
    }

    public float getLingQi() {
        return lingQi;
    }

    public void setLingQi(float lingQi) {
        this.lingQi = Math.max(0.0f, Math.min(getMaxLingQi(), lingQi));
    }

    public void consumeLingQi(float amount) {
        this.lingQi = Math.max(0.0f, this.lingQi - amount);
    }

    public void restoreLingQi(float amount) {
        this.lingQi = Math.min(getMaxLingQi(), this.lingQi + amount);
    }

    public boolean isMeditating() {
        return isMeditating;
    }

    public void setMeditating(boolean meditating) {
        this.isMeditating = meditating;
    }

    public double getRequiredTuVi() {
        return getRealm().calculateRequiredTuVi(this.layer);
    }

    public float getMaxLingQi() {
        float base = TuTienConfig.BASE_MAX_LINGQI.get().floatValue();
        float perRealm = TuTienConfig.LINGQI_PER_REALM.get().floatValue();
        float realmBase = getRealm().getBaseMaxQi();
        float layerFactor = (this.layer - 1) * 15.0f;
        return (base + this.realm * perRealm + realmBase + layerFactor);
    }

    public float getLingQiRegen() {
        float base = TuTienConfig.BASE_LINGQI_REGEN.get().floatValue();
        float realmBonus = this.realm * 0.5f;
        float meditationBonus = isMeditating ? 5.0f : 0.0f;
        return (base + realmBonus + meditationBonus);
    }

    public double getCultivationSpeed(double arrayMultiplier) {
        double base = TuTienConfig.BASE_CULTIVATION_SPEED.get();
        double realmMult = 1.0 + (this.realm * 0.2);
        double speed = base * realmMult * pillSpeedMultiplier * Math.max(1.0, arrayMultiplier);
        return speed;
    }

    public boolean canBreakthrough() {
        return this.tuVi >= getRequiredTuVi();
    }

    /**
     * Đột phá cảnh giới hoặc tầng tu vi.
     * Trả về true nếu thành công.
     */
    public boolean performBreakthrough(ServerPlayer player, boolean hasBreakthroughPill) {
        if (!canBreakthrough()) {
            return false;
        }

        if (this.layer < 9) {
            // Đột phá tầng 1 -> 8
            this.tuVi = 0.0;
            this.layer++;
            this.foundationConsolidation = 0.5; // Cần củng cố căn cơ cho tầng mới
            updateHighest();
            syncAttributes(player);
            player.sendSystemMessage(Component.translatable("tutien.message.breakthrough_layer_success", getRealm().getDisplayNameComponent(), this.layer));
            return true;
        } else {
            // Đang ở tầng 9, cần đột phá lên cảnh giới kế tiếp!
            if (this.realm >= 8) {
                player.sendSystemMessage(Component.translatable("tutien.message.breakthrough_max_realm"));
                return false;
            }

            if (!hasBreakthroughPill) {
                Realm next = getRealm().getNext();
                player.sendSystemMessage(Component.translatable("tutien.message.breakthrough_pill_required", next.getDisplayNameComponent()));
                return false;
            }

            // Đột phá cảnh giới lớn!
            this.tuVi = 0.0;
            this.realm++;
            this.layer = 1;
            this.foundationConsolidation = 0.2; // Căn cơ cảnh giới mới rất mỏng, cần củng cố
            updateHighest();
            syncAttributes(player);
            player.sendSystemMessage(Component.translatable("tutien.message.breakthrough_realm_success", getRealm().getDisplayNameComponent()));
            return true;
        }
    }

    private void updateHighest() {
        if (this.realm > this.highestRealm || (this.realm == this.highestRealm && this.layer > this.highestLayer)) {
            this.highestRealm = this.realm;
            this.highestLayer = this.layer;
        }
    }

    public void syncAttributes(ServerPlayer player) {
        Realm r = getRealm();
        // Căn cơ ảnh hưởng đến chỉ số: [50% + 50% * foundationConsolidation]
        double consolidationFactor = 0.5 + 0.5 * this.foundationConsolidation;

        double healthBonus = (r.getBaseHealthBonus() + (this.layer - 1) * 4.0) * consolidationFactor;
        double attackBonus = (r.getBaseAttackBonus() + (this.layer - 1) * 1.0 + this.pillDamageBonus) * consolidationFactor;
        double armorBonus = (r.getBaseArmorBonus() + (this.layer - 1) * 0.5 + this.pillDefenseBonus) * consolidationFactor;

        applyModifier(player.getAttribute(Attributes.MAX_HEALTH), HEALTH_MOD_ID, healthBonus);
        applyModifier(player.getAttribute(Attributes.ATTACK_DAMAGE), DAMAGE_MOD_ID, attackBonus);
        applyModifier(player.getAttribute(Attributes.ARMOR), ARMOR_MOD_ID, armorBonus);
    }

    private void applyModifier(AttributeInstance instance, ResourceLocation id, double value) {
        if (instance == null) return;
        instance.removeModifier(id);
        if (value > 0.0) {
            instance.addPermanentModifier(new AttributeModifier(id, value, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public void tick(ServerPlayer player, double arrayMultiplier) {
        // LingQi regen per second (executed every 20 ticks or tick delta)
        float regenPerTick = getLingQiRegen() / 20.0f;
        restoreLingQi(regenPerTick);

        // Pill buff decrement
        if (pillBuffTicks > 0) {
            pillBuffTicks--;
            if (pillBuffTicks <= 0) {
                pillSpeedMultiplier = 1.0;
                pillDamageBonus = 0.0f;
                pillDefenseBonus = 0.0f;
                syncAttributes(player);
                player.sendSystemMessage(Component.translatable("tutien.message.pill_buff_ended"));
            }
        }

        // Skill cooldowns decrement
        if (!cooldowns.isEmpty()) {
            cooldowns.entrySet().removeIf(entry -> {
                int cd = entry.getValue() - 1;
                if (cd <= 0) return true;
                entry.setValue(cd);
                return false;
            });
        }

        // Meditation tick
        if (isMeditating) {
            // Check cancel conditions: player moved significantly, dead, in water
            if (!player.isAlive()) {
                isMeditating = false;
                return;
            }

            double speedPerSecond = getCultivationSpeed(arrayMultiplier);
            double gainThisTick = speedPerSecond / 20.0;
            addTuVi(gainThisTick);

            // Foundation consolidation
            if (foundationConsolidation < 1.0) {
                double rate = TuTienConfig.CONSOLIDATION_RATE.get() / 20.0;
                foundationConsolidation = Math.min(1.0, foundationConsolidation + rate);
                if (foundationConsolidation >= 1.0) {
                    syncAttributes(player);
                    player.sendSystemMessage(Component.translatable("tutien.message.foundation_consolidated"));
                }
            }
        }
    }

    public void applyPillBuff(int durationTicks, double speedMult, float damageBonus, float defenseBonus) {
        this.pillBuffTicks = durationTicks;
        this.pillSpeedMultiplier = Math.max(1.0, speedMult);
        this.pillDamageBonus = damageBonus;
        this.pillDefenseBonus = defenseBonus;
    }

    // Skills
    public Map<String, SkillInstance> getLearnedSkills() {
        return learnedSkills;
    }

    public boolean hasLearnedSkill(String skillId) {
        return learnedSkills.containsKey(skillId);
    }

    public void learnSkill(String skillId) {
        learnedSkills.putIfAbsent(skillId, new SkillInstance(skillId));
    }

    public String[] getEquippedSkills() {
        return equippedSkills;
    }

    public void setEquippedSkill(int slot, String skillId) {
        if (slot >= 0 && slot < 4) {
            equippedSkills[slot] = skillId != null ? skillId : "";
        }
    }

    public int getCooldown(String skillId) {
        return cooldowns.getOrDefault(skillId, 0);
    }

    public void setCooldown(String skillId, int ticks) {
        cooldowns.put(skillId, ticks);
    }

    // Crafting & Sect
    public int getAlchemyLevel() { return alchemyLevel; }
    public void setAlchemyLevel(int level) { this.alchemyLevel = Math.max(1, Math.min(9, level)); }
    public int getAlchemyExp() { return alchemyExp; }
    public void addAlchemyExp(int exp) {
        this.alchemyExp += exp;
        int nextReq = this.alchemyLevel * 500;
        if (this.alchemyExp >= nextReq && this.alchemyLevel < 9) {
            this.alchemyLevel++;
            this.alchemyExp -= nextReq;
        }
    }

    public int getRefiningLevel() { return refiningLevel; }
    public void setRefiningLevel(int level) { this.refiningLevel = Math.max(1, Math.min(9, level)); }
    public int getRefiningExp() { return refiningExp; }
    public void addRefiningExp(int exp) {
        this.refiningExp += exp;
        int nextReq = this.refiningLevel * 500;
        if (this.refiningExp >= nextReq && this.refiningLevel < 9) {
            this.refiningLevel++;
            this.refiningExp -= nextReq;
        }
    }

    public long getCongHuan() { return congHuan; }
    public void addCongHuan(long amount) { this.congHuan = Math.max(0, this.congHuan + amount); }
    public boolean consumeCongHuan(long amount) {
        if (this.congHuan >= amount) {
            this.congHuan -= amount;
            return true;
        }
        return false;
    }

    public long getCongHien() { return congHien; }
    public void addCongHien(long amount) { this.congHien = Math.max(0, this.congHien + amount); }
    public boolean consumeCongHien(long amount) {
        if (this.congHien >= amount) {
            this.congHien -= amount;
            return true;
        }
        return false;
    }

    public String getSectId() { return sectId; }
    public void setSectId(String sectId) { this.sectId = sectId != null ? sectId : "none"; }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("Realm", realm);
        tag.putInt("Layer", layer);
        tag.putDouble("TuVi", tuVi);
        tag.putDouble("Foundation", foundationConsolidation);
        tag.putInt("HighestRealm", highestRealm);
        tag.putInt("HighestLayer", highestLayer);
        tag.putFloat("LingQi", lingQi);
        tag.putBoolean("Meditating", isMeditating);

        tag.putInt("PillTicks", pillBuffTicks);
        tag.putDouble("PillSpeed", pillSpeedMultiplier);
        tag.putFloat("PillDmg", pillDamageBonus);
        tag.putFloat("PillDef", pillDefenseBonus);

        tag.putInt("AlchemyLevel", alchemyLevel);
        tag.putInt("AlchemyExp", alchemyExp);
        tag.putInt("RefiningLevel", refiningLevel);
        tag.putInt("RefiningExp", refiningExp);

        tag.putLong("CongHuan", congHuan);
        tag.putLong("CongHien", congHien);
        tag.putString("SectId", sectId);

        ListTag skillsList = new ListTag();
        for (SkillInstance instance : learnedSkills.values()) {
            skillsList.add(instance.save());
        }
        tag.put("LearnedSkills", skillsList);

        for (int i = 0; i < 4; i++) {
            tag.putString("EquippedSkill_" + i, equippedSkills[i]);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.realm = nbt.getInt("Realm");
        this.layer = Math.max(1, nbt.getInt("Layer"));
        this.tuVi = nbt.getDouble("TuVi");
        this.foundationConsolidation = nbt.contains("Foundation") ? nbt.getDouble("Foundation") : 1.0;
        this.highestRealm = nbt.getInt("HighestRealm");
        this.highestLayer = Math.max(1, nbt.getInt("HighestLayer"));
        this.lingQi = nbt.contains("LingQi") ? nbt.getFloat("LingQi") : getMaxLingQi();
        this.isMeditating = nbt.getBoolean("Meditating");

        this.pillBuffTicks = nbt.getInt("PillTicks");
        this.pillSpeedMultiplier = nbt.contains("PillSpeed") ? nbt.getDouble("PillSpeed") : 1.0;
        this.pillDamageBonus = nbt.getFloat("PillDmg");
        this.pillDefenseBonus = nbt.getFloat("PillDef");

        this.alchemyLevel = Math.max(1, nbt.getInt("AlchemyLevel"));
        this.alchemyExp = nbt.getInt("AlchemyExp");
        this.refiningLevel = Math.max(1, nbt.getInt("RefiningLevel"));
        this.refiningExp = nbt.getInt("RefiningExp");

        this.congHuan = nbt.getLong("CongHuan");
        this.congHien = nbt.getLong("CongHien");
        this.sectId = nbt.getString("SectId");

        this.learnedSkills.clear();
        if (nbt.contains("LearnedSkills", Tag.TAG_LIST)) {
            ListTag list = nbt.getList("LearnedSkills", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                SkillInstance inst = SkillInstance.load(list.getCompound(i));
                this.learnedSkills.put(inst.getSkillId(), inst);
            }
        }

        for (int i = 0; i < 4; i++) {
            this.equippedSkills[i] = nbt.getString("EquippedSkill_" + i);
        }
    }
}
