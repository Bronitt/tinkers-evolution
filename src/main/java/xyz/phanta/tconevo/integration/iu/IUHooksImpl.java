package xyz.phanta.tconevo.integration.iu;

import com.denfop.items.resource.ItemDust;
import com.denfop.tiles.mechanism.TileEntityFluidIntegrator;
import com.denfop.IUItem;
import com.denfop.recipe.InputItemStack;
import com.denfop.tiles.mechanism.dual.heat.TileAlloySmelter;
import io.github.phantamanta44.libnine.util.nullity.Reflected;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import slimeknights.tconstruct.library.materials.Material;
import xyz.phanta.tconevo.init.TconEvoItems;
import xyz.phanta.tconevo.init.TconEvoMaterials;
import xyz.phanta.tconevo.item.ItemMaterial;
import xyz.phanta.tconevo.item.ItemMetal;

@Reflected
public class IUHooksImpl implements IUHooks {

    private static final IUHooks NOOP = new IUHooks.Noop();

    @Override
    public void onInit(FMLInitializationEvent event) {

        ItemStack energiumDust = IUItem.energiumDust;
        if (energiumDust != null) {

            TileAlloySmelter.addAlloysmelter(
                    new InputItemStack(ItemMaterial.Type.COALESCENCE_MATRIX.newStack(1)),
                    new InputItemStack(new ItemStack(IUItem.energiumDust.getItem(),9,24)),
                    TconEvoItems.METAL.newStack(ItemMetal.Type.ENERGETIC_METAL, ItemMetal.Form.INGOT, 1),
                    6000
            );

        }

        Fluid uuMatter = FluidRegistry.getFluid("iufluiduu_matter");
        if (uuMatter != null) {
            TileEntityFluidIntegrator.addRecipe(
                    ItemMaterial.Type.COALESCENCE_MATRIX.newStack(1),
                    (ItemStack)null,
                    new FluidStack(uuMatter, 72),
                    new FluidStack(TconEvoMaterials.UU_METAL.getFluid(), Material.VALUE_Ingot)
            );
        }
    }

//    @Override
//    public void onPostInit(FMLPostInitializationEvent event) {
//        if (TconEvoConfig.moduleIndustrialUpgrade.fuelSuperheatedSteamBurnTime > 0) {
//            Fluid superhotSteam = FluidRegistry.getFluid("ic2superheated_steam");
//            if (superhotSteam != null) {
//                TinkerRegistry.registerSmelteryFuel(new FluidStack(superhotSteam, 50),
//                        TconEvoConfig.moduleIndustrialCraft.fuelSuperheatedSteamBurnTime);
//            }
//        }
//        if (IUReflect.canGetSolarMultiplier()) {
//            ItemStack solarGen = IC2Items.getItem("te", "solar_generator");
//            if (solarGen != null) {
//                ModifierPhotovoltaic.registerSolarItem(solarGen,
//                        (int)Math.round(IUReflect.getSolarMultiplier() * PowerWrapper.RF_PER_EU * 20D));
//            }
//        }
//    }

//    @Override
//    public Optional<ItemStack> getItemSolarPanel() {
//        return Optional.ofNullable(IC2Items.getItem("te", "solar_generator"));
//    }

    @Override
    public float getSunlight(World world, BlockPos pos) {
        return IUReflect.canGetSkyLight() ? IUReflect.getSkyLight(world, pos) : NOOP.getSunlight(world, pos);
    }

    @Override
    public boolean consumeEF(ItemStack stack, double amount, EntityLivingBase entity, boolean commit) {
        return commit ? EFStoreItemHandler.INSTANCE.use(stack, amount, entity)
                : EFStoreItemHandler.INSTANCE.canUse(stack, amount);
    }

}
