package io.georgeous.piggyback;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.EntityArmorStand;

public class MyEntityTypes<T extends Entity> extends EntityTypes {


    //public static final EntityTypes<MyArmor> MY_ARMOR;

    static {
        //MY_ARMOR = a("armor_stand", EntityTypes.Builder.a(MyArmor::new, EnumCreatureType.g).a(0.5F, 1.975F).trackingRange(10));

    }

    public MyEntityTypes(EntityTypes.b entitytypes_b, EnumCreatureType enumcreaturetype, boolean flag, boolean flag1, boolean flag2, boolean flag3, ImmutableSet immutableset, EntitySize entitysize, int i, int j) {
        super(entitytypes_b, enumcreaturetype, flag, flag1, flag2, flag3, immutableset, entitysize, i, j);
    }
}
