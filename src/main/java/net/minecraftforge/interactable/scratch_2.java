package net.minecraftforge.interactable;

import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.interactable.api.IInteractableTransaction;
import net.minecraftforge.interactable.api.IModifiableInteractable;
import net.minecraftforge.interactable.api.TransactionNotValidException;

class Scratch
{
    public static void main(String[] args)
    {
        
    }

    public boolean makeCobble(IModifiableInteractable<FluidStack, ?> fluidSource, IModifiableInteractable<ItemStack, ?> chest)
    {
        final IInteractableTransaction<FluidStack, ?> fluidTransaction = fluidSource.beginTransaction();
        final IInteractableTransaction<ItemStack, ?> chestTransaction = chest.beginTransaction();

        if (fluidTransaction.extractFirstMatching(f -> f.getFluid() == Fluids.WATER, 1).wasSuccessful() &&
              fluidTransaction.extractFirstMatching(f -> f.getFluid() == Fluids.LAVA, 1).wasSuccessful() &&
              chestTransaction.insert(new ItemStack(Blocks.COBBLESTONE)).wasSuccessful()) {
            try
            {
                fluidTransaction.commit();
                chestTransaction.commit();
                return true;
            }
            catch (TransactionNotValidException e)
            {
                return false;
            }
        }

        return false;
    }

}