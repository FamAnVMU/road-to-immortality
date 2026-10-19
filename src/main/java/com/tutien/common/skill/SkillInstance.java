package com.tutien.common.skill;

import net.minecraft.nbt.CompoundTag;

public class SkillInstance {
    private final String skillId;
    private int exp;
    private MasteryLevel mastery;

    public SkillInstance(String skillId) {
        this(skillId, 0);
    }

    public SkillInstance(String skillId, int exp) {
        this.skillId = skillId;
        this.exp = exp;
        this.mastery = MasteryLevel.fromExp(exp);
    }

    public String getSkillId() {
        return skillId;
    }

    public int getExp() {
        return exp;
    }

    public MasteryLevel getMastery() {
        return mastery;
    }

    public void addExp(int amount) {
        this.exp += amount;
        this.mastery = MasteryLevel.fromExp(this.exp);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("SkillId", skillId);
        tag.putInt("Exp", exp);
        return tag;
    }

    public static SkillInstance load(CompoundTag tag) {
        String id = tag.getString("SkillId");
        int exp = tag.getInt("Exp");
        return new SkillInstance(id, exp);
    }
}
