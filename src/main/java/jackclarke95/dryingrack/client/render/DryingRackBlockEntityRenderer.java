package jackclarke95.dryingrack.client.render;

import jackclarke95.dryingrack.block.DryingRackBlock;
import jackclarke95.dryingrack.block.entity.DryingRackBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class DryingRackBlockEntityRenderer implements BlockEntityRenderer<DryingRackBlockEntity> {
    private final ItemRenderer itemRenderer;

    public DryingRackBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
        System.out.println("DryingRackBlockEntityRenderer constructor called!");
    }

    @Override
    public void render(DryingRackBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Debug: Check if we have items to render
        int itemCount = 0;
        for (int i = 0; i < 4; i++) {
            if (!entity.getStack(i).isEmpty())
                itemCount++;
        }

        if (itemCount > 0) {
            System.out.println("DryingRackRenderer: Rendering " + itemCount + " items");
        }

        // Get the block's facing direction
        Direction facing = entity.getCachedState().get(DryingRackBlock.FACING);

        // Calculate the rotation angle based on facing direction
        float blockRotation = switch (facing) {
            case NORTH -> 0f;
            case EAST -> 90f;
            case SOUTH -> 180f;
            case WEST -> 270f;
            default -> 0f;
        };

        // Render items hanging from the rack
        for (int slot = 0; slot < 4; slot++) {
            ItemStack stack = entity.getStack(slot);
            if (!stack.isEmpty()) {
                System.out.println("Rendering item in slot " + slot + ": " + stack.getItem().toString());

                matrices.push();

                // Apply block rotation first - rotate around the center of the block
                matrices.translate(0.5f, 0f, 0.5f); // Move to block center
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockRotation));
                matrices.translate(-0.5f, 0f, -0.5f); // Move back

                // Calculate positioning for the slanted tabletop surface
                // Tabletop runs from X=2 to X=14, Z=3 to Z=18 (extended for the slant)
                // Usable surface: X=3 to X=13 for items
                float leftEdge = 3.0f / 16.0f; // 0.1875 - start of usable surface
                float rightEdge = 13.0f / 16.0f; // 0.8125 - end of usable surface

                // Add padding to avoid items being too close to edges
                float padding = 0.02f;
                float adjustedLeftEdge = leftEdge + padding;
                float adjustedRightEdge = rightEdge - padding;
                float adjustedUsableWidth = adjustedRightEdge - adjustedLeftEdge;

                // Space items evenly across the tabletop width
                float spacePerUnit = adjustedUsableWidth / 8.0f;

                // Determine slot position based on facing direction
                int visualSlot = slot;
                if (facing == Direction.SOUTH || facing == Direction.NORTH) {
                    // Reverse slot order for consistent left-to-right filling
                    visualSlot = 3 - slot;
                }

                float x = adjustedLeftEdge + spacePerUnit + (visualSlot * 2.0f * spacePerUnit);

                // Position items ON the slanted tabletop surface
                // The tabletop is at Y=12 (0.75) and slopes down slightly due to -10 degree
                // rotation
                // We'll place items slightly above the surface to account for the slant
                float y = 12.5f / 16.0f; // 0.78125 - slightly above the tabletop surface

                // Position in the middle of the tabletop depth
                float z = 10.5f / 16.0f; // 0.65625 - middle of the slanted surface depth

                matrices.translate(x, y, z);

                // Make items smaller for tabletop display
                matrices.scale(0.3f, 0.3f, 0.3f);

                // Flip texture for north/south facings to correct mirroring
                if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
                }

                // Apply the slant rotation to match the tabletop angle (-10 degrees around X
                // axis)
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-10.0f));

                // Render the item
                itemRenderer.renderItem(
                        stack,
                        ModelTransformationMode.GROUND,
                        light,
                        overlay,
                        matrices,
                        vertexConsumers,
                        entity.getWorld(),
                        (int) entity.getPos().asLong());

                matrices.pop();
            }
        }
    }
}
