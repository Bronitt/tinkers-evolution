package xyz.phanta.tconevo.integration.iu;


import com.denfop.ElectricItem;
import com.denfop.api.item.IElectricItemManager;
import com.denfop.api.item.IEnergyItem;
import com.denfop.utils.ModUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class EFStoreItemHandler implements IElectricItemManager {

    public static final EFStoreItemHandler INSTANCE = new EFStoreItemHandler();

    private static final EntityEquipmentSlot[] ARMOUR_SLOTS = {
            EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET
    };

    private EFStoreItemHandler() {
        // NO-OP
    }

    public double charge(ItemStack stack, double amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
        IEnergyItem item = (IEnergyItem) stack.getItem();

        assert item.getMaxEnergy(stack) > 0.0D;
        if (this.getCharge(stack) == item.getMaxEnergy(stack)) {
            return 0;
        }
        if (!(amount < 0.0D) && ModUtils.getSize(stack) <= 1 && item.getTierItem(stack) <= tier) {
            if (!ignoreTransferLimit && amount > item.getTransferEnergy(stack)) {
                amount = item.getTransferEnergy(stack);
            }

            NBTTagCompound tNBT = ModUtils.nbt(stack);
            double newCharge = tNBT.getDouble("charge");
            amount = Math.min(amount, item.getMaxEnergy(stack) - newCharge);
            if (!simulate) {
                newCharge += amount;
                if (newCharge > 0.0D) {
                    tNBT.setDouble("charge", newCharge);
                } else {
                    tNBT.setDouble("charge", 0);
                }
            }

            return amount;
        } else {
            return 0.0D;
        }
    }

    public double discharge(
            ItemStack stack,
            double amount,
            int tier,
            boolean ignoreTransferLimit,
            boolean externally,
            boolean simulate
    ) {
        IEnergyItem item = (IEnergyItem) stack.getItem();

        assert item.getMaxEnergy(stack) > 0.0D;
        if (!(amount < 0.0D) && ModUtils.getSize(stack) <= 1 && item.getTierItem(stack) <= tier) {
            if (externally && !item.canProvideEnergy(stack)) {
                return 0.0D;
            } else {
                if (!ignoreTransferLimit && amount > item.getTransferEnergy(stack)) {
                    amount = item.getTransferEnergy(stack);
                }

                NBTTagCompound tNBT = ModUtils.nbt(stack);
                double newCharge = tNBT.getDouble("charge");
                amount = Math.min(amount, newCharge);
                if (!simulate) {
                    newCharge -= amount;
                    if (newCharge > 0.0D) {
                        tNBT.setDouble("charge", newCharge);
                    } else {
                        tNBT.setDouble("charge", 0);
                    }
                }

                return amount;
            }
        } else {
            return 0.0D;
        }
    }

    public double getCharge(ItemStack stack) {
        return ModUtils.nbt(stack).getDouble("charge");
    }

    public double getMaxCharge(ItemStack stack) {
        return ((IEnergyItem) stack.getItem()).getMaxEnergy(stack);
    }

    public boolean canUse(ItemStack stack, double amount) {
        return ElectricItem.manager.getCharge(stack) >= amount;
    }

    public boolean use(ItemStack stack, double amount, EntityLivingBase entity) {
        double transfer = this.getCharge(stack);
        if (transfer >= amount) {
            ElectricItem.manager.discharge(stack, amount, 2147483647, true, false, false);
            return true;
        } else {
            return false;
        }
    }

    public String getToolTip(ItemStack stack) {
        if (stack.getItem() instanceof IEnergyItem) {
            double charge = ElectricItem.manager.getCharge(stack);
            return ModUtils.getString(charge) + "/" + ModUtils.getString(
                    ((IEnergyItem) stack.getItem()).getMaxEnergy(stack)
            ) + " EF";
        } else {
            return "";
        }
    }

    public int getTier(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IEnergyItem
                ? ((IEnergyItem) stack.getItem()).getTierItem(stack)
                : 0;
    }

}
