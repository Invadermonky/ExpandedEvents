package com.expandedevents.mixins;

import com.expandedevents.api.event.GenerateEnchantmentListEvent;
import com.expandedevents.tags.ItemTags;
import com.expandedevents.utils.IEnchantmentTableEvent;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ContainerEnchantment.class)
public abstract class ContainerEnchantmentMixin extends Container implements IEnchantmentTableEvent {
    @Shadow public IInventory tableInventory;
    @Shadow @Final private World world;
    @Shadow @Final private BlockPos position;
    @Shadow @Final private Random rand;
    @Shadow public int xpSeed;
    @Shadow public int[] enchantClue;
    @Shadow public int[] worldClue;
    @Shadow public int[] enchantLevels;

    @Unique private EntityPlayer ee$playerCapture;
    @Unique private List<List<EnchantmentData>> ee$cachedEnchants;
    @Unique private int[] ee$enchantmentCost;
    @Unique private int[] ee$reagentCost;

    @Override
    public int ee$getEnchantmentLevelCost(int enchantmentTier) {
        return this.ee$enchantmentCost[enchantmentTier];
    }

    @Override
    public void ee$setEnchantmentLevelCost(int enchantmentTier, int levelCost) {
        this.ee$enchantmentCost[enchantmentTier] = levelCost;
    }

    @Override
    public int ee$getReagentCost(int enchantmentTier) {
        return this.ee$reagentCost[enchantmentTier];
    }

    @Override
    public void ee$setReagentCost(int enchantmentTier, int reagentCost) {
        this.ee$reagentCost[enchantmentTier] = reagentCost;
    }

    /**
     * @author Invadermonky
     * @reason Fills the enchantLevels array so it contains -1 instead of 0 to prevent the enchanting table from
     *         displaying enchantment information when no item is present.
     */
    @Inject(method = "Lnet/minecraft/inventory/ContainerEnchantment;<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", at = @At("TAIL"))
    private void setEnchantLevels(InventoryPlayer playerInv, World worldIn, BlockPos pos, CallbackInfo ci) {
        Arrays.fill(this.enchantLevels, -1);
        this.ee$playerCapture = playerInv.player;
        this.ee$enchantmentCost = new int[] {1, 2, 3};
        //Reagent cost are initialized at Short.MAX_VALUE to prevent flickering when the reagent slot item changes.
        this.ee$reagentCost = new int[] {Short.MAX_VALUE, Short.MAX_VALUE, Short.MAX_VALUE};
    }

    /**
     * @author Invadermonky
     * @reason Adds window properties to sync the new event controlled enchantment level cost and reagent cost.
     */
    @Inject(method = "broadcastData", at = @At("RETURN"))
    private void addNewWindowProperties(IContainerListener crafting, CallbackInfo ci) {
        crafting.sendWindowProperty(this, 100, this.ee$getEnchantmentLevelCost(0));
        crafting.sendWindowProperty(this, 101, this.ee$getEnchantmentLevelCost(1));
        crafting.sendWindowProperty(this, 102, this.ee$getEnchantmentLevelCost(2));
        crafting.sendWindowProperty(this, 103, this.ee$getReagentCost(0));
        crafting.sendWindowProperty(this, 104, this.ee$getReagentCost(1));
        crafting.sendWindowProperty(this, 105, this.ee$getReagentCost(2));
    }

    /**
     * @author Invadermonky
     * @reason Syncs the new window properties between client and server for the new event controlled enchantment
     *         level cost and reagent cost.
     */
    @SideOnly(Side.CLIENT)
    @Inject(method = "updateProgressBar", at = @At("RETURN"))
    private void updateNewWindowProperties(int id, int data, CallbackInfo ci) {
        if(id >= 100 && id <= 102) {
            this.ee$setEnchantmentLevelCost(id - 100, data);
        } else if(id >= 103 && id <= 105) {
            this.ee$setReagentCost(id - 103, data);
        }
    }

    /**
     * @author Invadermonky
     * @reason Resets the event enchantment values whenever an item in the added or removed from the slots.
     */
    @Inject(method = "onCraftMatrixChanged", at = @At("HEAD"))
    private void resetEnchantmentCache(IInventory inventory, CallbackInfo ci) {
        this.ee$cachedEnchants = Lists.newArrayList(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    /**
     * @author Invadermonky
     * @reason Replaces Minecraft's enchantment list generation with an event, allowing developers to
     *         change and manipulate numerous parts of enchanting table and item enchantment logic.
     */
    @ModifyExpressionValue(
            method = "onCraftMatrixChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/inventory/ContainerEnchantment;getEnchantmentList(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;"
            )
    )
    private List<EnchantmentData> generateEnchantmentListEventMixin(List<EnchantmentData> original, @Local(ordinal = 1) int enchantTier) {
        ItemStack toEnchant = this.tableInventory.getStackInSlot(0);
        ItemStack reagent = this.tableInventory.getStackInSlot(1);
        if (!reagent.getItem().hasContainerItem(reagent) || reagent.getCount() == 1) {
            GenerateEnchantmentListEvent event = new GenerateEnchantmentListEvent(this.world, this.position, this.ee$playerCapture, toEnchant, reagent, this.xpSeed, enchantTier, this.enchantLevels[enchantTier], original);
            MinecraftForge.EVENT_BUS.post(event);
            if (!event.getEnchantments().isEmpty()) {
                //Logic is changed very slightly from Minecraft. Instead of accessing a random enchant from the list,
                //the list is instead shuffled and the first element is the displayed enchant.
                Collections.shuffle(event.getModifiableEnchantments(), this.rand);
                EnchantmentData data = event.getEnchantments().get(0);
                this.enchantLevels[enchantTier] = event.getRequiredLevel();
                this.enchantClue[enchantTier] = Enchantment.getEnchantmentID(data.enchantment);
                this.worldClue[enchantTier] = data.enchantmentLevel;
                this.ee$cachedEnchants.set(enchantTier, event.getEnchantments());
                this.ee$setEnchantmentLevelCost(enchantTier, event.getLevelCost());
                this.ee$setReagentCost(enchantTier, event.getReagentCost());
            }
        }
        //Empty list is returned in order to bypass MC logic which will overwrite event logic.
        return Collections.emptyList();
    }

    /**
     * @author Invadermonky
     * @reason Resets all event fields to their default values. Also Changes the enchantment table
     *         enchantLevels reset value to -1 to support level 0 enchanting.
     */
    @Inject(
            method = "onCraftMatrixChanged",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/inventory/ContainerEnchantment;worldClue:[I",
                    ordinal = 2
            )
    )
    private void resetEventValues(IInventory inventory, CallbackInfo ci) {
        Arrays.fill(this.enchantLevels, -1);
        this.ee$enchantmentCost = new int[] {1, 2, 3};
    }

    /**
     * @author Invadermonky
     * @reason Modifies the enchanting reagent item check to account for event-controlled no reagent cost enchanting.
     */
    @ModifyExpressionValue(
            method = "enchantItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
                    ordinal = 0
            )
    )
    private boolean allowReagentlessEnchanting(boolean isEmpty, @Local(ordinal = 0, argsOnly = true) int enchantmentTier) {
        return isEmpty && this.ee$getReagentCost(enchantmentTier) != 0;
    }

    /**
     * @author Invadermonky
     * @reason Modifies the enchanting regent item count check to account for the event-controlled reagent cost.
     */
    @Definition(id = "itemstack1", local = @Local(type = ItemStack.class, ordinal = 1))
    @Definition(id = "getCount", method = "Lnet/minecraft/item/ItemStack;getCount()I")
    @Definition(id = "i", local = @Local(type = int.class, ordinal = 1))
    @Expression("itemstack1.getCount() < i")
    @ModifyExpressionValue(method = "enchantItem", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean overrideReagentCountCheck(boolean original, @Local(ordinal = 0, argsOnly = true) int enchantmentTier, @Local(ordinal = 1) ItemStack reagent) {
        return reagent.getCount() < this.ee$getReagentCost(enchantmentTier);
    }

    /**
     * @author Invadermonky
     * @reason Modifies the enchantment level check to account for event-controlled no-level requirement enchanting.
     */
    @Definition(id = "enchantLevels", field = "Lnet/minecraft/inventory/ContainerEnchantment;enchantLevels:[I")
    @Definition(id = "id", local = @Local(type = int.class, argsOnly = true, ordinal = 0))
    @Expression("this.enchantLevels[id] > 0")
    @ModifyExpressionValue(method = "enchantItem", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean overrideEnchantLevelCheck(boolean original, @Local(ordinal = 0, argsOnly = true) int enchantmentTier) {
        return this.enchantLevels[enchantmentTier] >= 0;
    }

    /**
     * @author Invadermonky
     * @reason Overrides the hardcoded gui-button controlled player experience level check to instead use the
     *         event-controlled level cost.
     */
    @Definition(id = "playerIn", local = @Local(type = EntityPlayer.class, argsOnly = true, ordinal = 0))
    @Definition(id = "experienceLevel", field = "Lnet/minecraft/entity/player/EntityPlayer;experienceLevel:I")
    @Definition(id = "i", local = @Local(type = int.class, ordinal = 1))
    @Expression("playerIn.experienceLevel >= i")
    @ModifyExpressionValue(method = "enchantItem", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean overrideExperienceLevelCheck(boolean original, @Local(argsOnly = true, ordinal = 0) EntityPlayer player, @Local(argsOnly = true, ordinal = 0) int enchantmentTier) {
        return player.experienceLevel >= this.ee$getEnchantmentLevelCost(enchantmentTier);
    }

    /**
     * @author Invadermonky
     * @reason Replaces the repeated enchantment list generation with the enchantment list cached
     *         from the event.
     */
    @ModifyExpressionValue(
            method = "enchantItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/inventory/ContainerEnchantment;getEnchantmentList(Lnet/minecraft/item/ItemStack;II)Ljava/util/List;"
            )
    )
    private List<EnchantmentData> getCachedEventEnchants(List<EnchantmentData> original, @Local(argsOnly = true, ordinal = 0) int enchantmentTier) {
        return this.ee$cachedEnchants.get(enchantmentTier);
    }

    /**
     * @author Invadermonky
     * @reason Changes the hardcoded player level enchanting cost into the event controlled enchanting cost.
     */
    @Redirect(
            method = "enchantItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayer;onEnchant(Lnet/minecraft/item/ItemStack;I)V"
            )
    )
    public void overrideEnchantmentLevelCost(EntityPlayer player, ItemStack toEnchant, int levels, @Local(argsOnly = true, ordinal = 0) int enchantmentTier) {
        player.onEnchant(toEnchant, this.ee$getEnchantmentLevelCost(enchantmentTier));
    }

    /**
     * @author Invadermonky
     * @reason Changes the hardcoded reagent enchanting cost into the event controlled reagent cost.
     */
    @Redirect(
            method = "enchantItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;shrink(I)V"
            )
    )
    private void shrinkReagentMixin(ItemStack reagent, int amount, @Local(argsOnly = true, ordinal = 0) int enchantmentTier) {
        int toConsume = this.ee$getReagentCost(enchantmentTier);
        if(toConsume > 0) {
            if(reagent.getItem().hasContainerItem(reagent) && reagent.getCount() == 1) {
                this.tableInventory.setInventorySlotContents(1, reagent.getItem().getContainerItem(reagent));
            } else {
                reagent.shrink(toConsume);
            }
        }
    }

    /**
     * @author Invadermonky
     * @reason Modifies the hardcoded check for the DYE item to instead redirect to
     *         {@link ItemTags#isEnchantingReagent(ItemStack)}.
     */
    @Definition(id = "itemstack1", local = @Local(type = ItemStack.class, ordinal = 1))
    @Definition(id = "getItem", method = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;")
    @Definition(id = "DYE", field = "Lnet/minecraft/init/Items;DYE:Lnet/minecraft/item/Item;")
    @Expression("itemstack1.getItem() == DYE")
    @ModifyExpressionValue(method = "transferStackInSlot", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private boolean allowEnchantingReagentMixin(boolean original, @Local(ordinal = 1) ItemStack stack) {
        return ItemTags.isEnchantingReagent(stack);
    }

    /**
     * @author Invadermonky
     * @reason Cancels the metadata check following the item check for Dye.
     */
    @Definition(id = "itemstack1", local = @Local(type = ItemStack.class, ordinal = 1))
    @Definition(id = "byDyeDamage", method = "Lnet/minecraft/item/EnumDyeColor;byDyeDamage(I)Lnet/minecraft/item/EnumDyeColor;")
    @Definition(id = "getMetadata", method = "Lnet/minecraft/item/ItemStack;getMetadata()I")
    @Definition(id = "BLUE", field = "Lnet/minecraft/item/EnumDyeColor;BLUE:Lnet/minecraft/item/EnumDyeColor;")
    @Expression("byDyeDamage(itemstack1.getMetadata()) == BLUE")
    @ModifyExpressionValue(method = "transferStackInSlot", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private boolean cancelMetadataCheck(boolean original, @Local(ordinal = 1) ItemStack stack) {
        return true;
    }

}
