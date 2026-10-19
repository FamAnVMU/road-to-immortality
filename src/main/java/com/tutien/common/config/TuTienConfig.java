package com.tutien.common.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TuTienConfig {
    public static final ModConfigSpec COMMON_SPEC;
    
    // Cultivation
    public static final ModConfigSpec.DoubleValue BASE_CULTIVATION_SPEED;
    public static final ModConfigSpec.DoubleValue TU_VI_BASE_REQUIREMENT;
    public static final ModConfigSpec.DoubleValue TU_VI_LAYER_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue TU_VI_REALM_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue CONSOLIDATION_RATE;

    // LingQi
    public static final ModConfigSpec.DoubleValue BASE_MAX_LINGQI;
    public static final ModConfigSpec.DoubleValue LINGQI_PER_REALM;
    public static final ModConfigSpec.DoubleValue BASE_LINGQI_REGEN;

    // Spirit Stone
    public static final ModConfigSpec.IntValue SPIRIT_STONE_EXCHANGE_RATIO;

    // Altar
    public static final ModConfigSpec.IntValue TOWN_ALTAR_COST;
    public static final ModConfigSpec.IntValue CANGYUN_ALTAR_COST;
    public static final ModConfigSpec.IntValue KUNLUN_ALTAR_COST;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("cultivation");
        BASE_CULTIVATION_SPEED = builder
                .comment("Tốc độ tu luyện cơ bản khi ngồi thiền (tu vi mỗi giây)")
                .defineInRange("baseCultivationSpeed", 5.0, 0.1, 10000.0);
        TU_VI_BASE_REQUIREMENT = builder
                .comment("Lượng tu vi cơ bản để đột phá Luyện Khí tầng 1 lên tầng 2")
                .defineInRange("tuViBaseRequirement", 100.0, 10.0, 1000000.0);
        TU_VI_LAYER_MULTIPLIER = builder
                .comment("Hệ số nhân tu vi qua mỗi tầng")
                .defineInRange("tuViLayerMultiplier", 1.25, 1.0, 5.0);
        TU_VI_REALM_MULTIPLIER = builder
                .comment("Hệ số nhân tu vi qua mỗi cảnh giới")
                .defineInRange("tuViRealmMultiplier", 3.0, 1.0, 20.0);
        CONSOLIDATION_RATE = builder
                .comment("Tốc độ củng cố căn cơ mỗi giây khi tu luyện")
                .defineInRange("consolidationRate", 0.01, 0.0001, 1.0);
        builder.pop();

        builder.push("lingqi");
        BASE_MAX_LINGQI = builder
                .comment("Linh khí tối đa cơ bản ở Luyện Khí tầng 1")
                .defineInRange("baseMaxLingQi", 100.0, 10.0, 100000.0);
        LINGQI_PER_REALM = builder
                .comment("Linh khí tối đa tăng thêm mỗi cảnh giới")
                .defineInRange("lingQiPerRealm", 150.0, 10.0, 100000.0);
        BASE_LINGQI_REGEN = builder
                .comment("Tốc độ hồi linh khí cơ bản mỗi giây")
                .defineInRange("baseLingQiRegen", 2.0, 0.1, 1000.0);
        builder.pop();

        builder.push("currency");
        SPIRIT_STONE_EXCHANGE_RATIO = builder
                .comment("Tỷ lệ quy đổi giữa các bậc linh thạch (mặc định 1000:1)")
                .defineInRange("spiritStoneExchangeRatio", 1000, 10, 100000);
        builder.pop();

        builder.push("altar");
        TOWN_ALTAR_COST = builder
                .comment("Chi phí linh thạch hạ phẩm để sử dụng tế đàn Thị trấn")
                .defineInRange("townAltarCost", 50, 1, 10000);
        CANGYUN_ALTAR_COST = builder
                .comment("Chi phí linh thạch hạ phẩm để sử dụng tế đàn Thương Vân Tông")
                .defineInRange("cangyunAltarCost", 200, 1, 100000);
        KUNLUN_ALTAR_COST = builder
                .comment("Chi phí linh thạch hạ phẩm để sử dụng tế đàn Côn Luân Thánh Địa")
                .defineInRange("kunlunAltarCost", 1000, 1, 1000000);
        builder.pop();

        COMMON_SPEC = builder.build();
    }
}
