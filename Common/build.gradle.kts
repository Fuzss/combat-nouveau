plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    modCompileOnlyApi(sharedLibs.puzzleslib.common)
}

multiloader {
    mixins {
        mixin("EntityMixin", "FoodDataMixin", "PlayerMixin", "ProjectileUtilMixin")
        clientMixin("ItemAttributeModifiers\$Display\$DefaultMixin", "LivingEntityMixin", "MinecraftMixin")
    }
}
