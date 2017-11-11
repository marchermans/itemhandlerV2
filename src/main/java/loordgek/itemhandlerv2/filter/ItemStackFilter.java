package loordgek.itemhandlerv2.filter;

import com.google.common.collect.Range;
import com.google.common.collect.Table;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.function.Predicate;

public class ItemStackFilter implements Predicate<ItemStack> {
    private final Item item;
    private final Range<Integer> metadata;
    private final NBTTagCompound nbtTag;
    private final Table<Capability<?>, EnumFacing, Predicate<?>> capabilityFilters;
    private final Range<Integer> stackSize;
    private final boolean matchNBT;
    private final boolean matchItem;
    private final boolean matchMeta;
    private final boolean matchCap;
    private final boolean matchStackSize;


    private final boolean inverted;

    public ItemStackFilter(Item item, Range<Integer> metadata, NBTTagCompound nbtTag, Table<Capability<?>, EnumFacing, Predicate<?>> capabilityFilters,
                           Range<Integer> stackSize, boolean matchNBT, boolean matchItem, boolean matchMeta, boolean matchCap, boolean matchStackSize, boolean inverted) {
        this.item = item;
        this.metadata = metadata;
        this.nbtTag = nbtTag;
        this.capabilityFilters = capabilityFilters;
        this.stackSize = stackSize;
        this.matchNBT = matchNBT;
        this.matchItem = matchItem;
        this.matchMeta = matchMeta;
        this.matchCap = matchCap;
        this.matchStackSize = matchStackSize;

        this.inverted = inverted;
    }

    @SuppressWarnings("unchecked")
    public <T> boolean testCapability(ItemStack stack) {
        for (Table.Cell<Capability<?>, EnumFacing, Predicate<?>> cell : capabilityFilters.cellSet()) {
            Capability<T> cap = (Capability<T>) cell.getRowKey();
            EnumFacing face = cell.getColumnKey();

            boolean has = stack.hasCapability(cap, face);
            if (has == inverted) {
                return false;
            }
            if (has && ((Predicate<T>) cell.getValue()).test(stack.getCapability(cap, face)) == inverted) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean test(ItemStack stack) {
        if (matchItem) {
            if (stack.getItem() != item) {
                return inverted;
            }
        }
        if (matchMeta) {
            if (!metadata.contains(stack.getMetadata())) {
                return inverted;
            }
        }
        if (matchStackSize) {
            if (!stackSize.contains(stack.getCount())) {
                return inverted;
            }
        }
        if (matchNBT) {
            NBTTagCompound tag = stack.getTagCompound();
            if ((nbtTag == null) != (tag == null)) {
                return inverted;
            }
            return (tag == null || nbtTag.equals(tag)) != inverted;
        }
        if (matchCap) {
            return testCapability(stack);
        }
        return true;
    }

    public static Builder filterBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Range<Integer> stackSize = null;
        private Item item = null;
        private Range<Integer> metadata = null;
        private NBTTagCompound nbtTag = null;
        private Table<Capability<?>, EnumFacing, Predicate<?>> capabilityFilters = null;
        private boolean inverted = false;


        public Builder setInverted() {
            this.inverted = true;
            return this;
        }

        public Builder withCapabilityFilters(Table<Capability<?>, EnumFacing, Predicate<?>> capabilityFilters) {
            this.capabilityFilters = capabilityFilters;
            return this;
        }

        public Builder withNbtTag(NBTTagCompound nbtTag) {
            this.nbtTag = nbtTag;
            return this;
        }

        public Builder withMetadata(Range<Integer> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder withItemStack(ItemStack stack) {
            this.nbtTag = stack.getTagCompound();
            this.metadata = Range.singleton(stack.getMetadata());
            return withItem(stack.getItem());
        }

        public Builder withItem(Item item) {
            this.item = item;
            return this;
        }

        public Builder withStackSize(Range<Integer> stackSize) {
            this.stackSize = stackSize;
            return this;
        }

        public ItemStackFilter build() {
            return new ItemStackFilter(item, metadata, nbtTag, capabilityFilters, stackSize, nbtTag != null, item != null, metadata != null, capabilityFilters != null, stackSize != null, inverted);
        }
    }
}
