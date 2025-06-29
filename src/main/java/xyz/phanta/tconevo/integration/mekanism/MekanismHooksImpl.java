package xyz.phanta.tconevo.integration.mekanism;

import mekanism.common.MekanismItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import io.github.phantamanta44.libnine.util.nullity.Reflected;

import java.util.Optional;

@Reflected
public class MekanismHooksImpl implements MekanismHooks {

    @Override
    public void onInit(FMLInitializationEvent event) {
        OreDictionary.registerOre("pelletHDPE", createHdpeStack(MekanismItems.HDPE_PELLET));
        OreDictionary.registerOre("rodHDPE", createHdpeStack(MekanismItems.HDPE_ROD));
        OreDictionary.registerOre("sheetHDPE", createHdpeStack(MekanismItems.HDPE_SHEET));
        OreDictionary.registerOre("stickHDPE", createHdpeStack(MekanismItems.HDPE_STICK));
    }

    private static ItemStack createHdpeStack(Item item) {
        return new ItemStack(item, 1);
    }

    @Override
    public Optional<ItemStack> getItemEnergyTablet() {
        return Optional.of(new ItemStack(MekanismItems.EnergyTablet));
    }

}
