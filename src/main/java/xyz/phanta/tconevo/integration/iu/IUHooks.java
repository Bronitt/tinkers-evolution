package xyz.phanta.tconevo.integration.iu;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import xyz.phanta.tconevo.integration.IntegrationHooks;

import java.util.Optional;

public interface IUHooks extends IntegrationHooks {

    String MOD_ID = "industrialupgrade";

    @Inject(MOD_ID)
    IUHooks INSTANCE = new Noop();

//    Optional<ItemStack> getItemSolarPanel();

    float getSunlight(World world, BlockPos pos);

    boolean consumeEF(ItemStack stack, double amount, EntityLivingBase entity, boolean commit);

    class Noop implements IUHooks {

//        @Override
//        public Optional<ItemStack> getItemSolarPanel() {
//            return Optional.empty();
//        }

        @Override
        public float getSunlight(World world, BlockPos pos) {
            return (world.getLightFor(EnumSkyBlock.SKY, pos) / 15F)
                    * Math.max(MathHelper.cos(world.getCelestialAngleRadians(1F)) * 2F + 0.2F, 0F);
        }

        @Override
        public boolean consumeEF(ItemStack stack, double amount, EntityLivingBase entity, boolean commit) {
            return false;
        }

    }

}
