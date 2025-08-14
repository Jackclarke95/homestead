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

                // Calculate positioning based on the actual model dimensions
                // Horizontal bar runs from X=2 to X=14 (in 16-unit block space)
                // Left post: X=1-3, Right post: X=13-15
                // Usable hanging space: X=3 to X=13 (10 units = 0.625 block width)
                float leftEdge = 3.0f / 16.0f; // 0.1875 - inner edge of left post
                float rightEdge = 13.0f / 16.0f; // 0.8125 - inner edge of right post

                // Add padding from the posts to avoid touching them
                float padding = 0.03f; // Small buffer from the posts
                float adjustedLeftEdge = leftEdge + padding;
                float adjustedRightEdge = rightEdge - padding;
                float adjustedUsableWidth = adjustedRightEdge - adjustedLeftEdge;

                // CSS space-around: Equal space around each item
                // For 4 items: [space][item][space][item][space][item][space][item][space]
                // Total spaces = 8 (2 spaces around each item, but edge spaces are half)
                // Effective spaces = 4 items + 3 full gaps + 2 half-edge-gaps = 4 + 3 + 1 = 8
                // units
                float spacePerUnit = adjustedUsableWidth / 8.0f;

                // Determine slot position based on facing direction
                // We want slots to fill left-to-right from the player's perspective when they
                // placed the block
                int visualSlot = slot;
                if (facing == Direction.SOUTH || facing == Direction.NORTH) {
                    // For east and north facings, reverse the slot order to maintain
                    // left-to-right
                    // filling from the player's placement perspective
                    visualSlot = 3 - slot;
                }

                float x = adjustedLeftEdge + spacePerUnit + (visualSlot * 2.0f * spacePerUnit);

                // Position just below the horizontal bar (bar is at Y=8-10, so items hang just
                // below)
                float y = 6.0f / 16.0f; // 0.375 - just below the horizontal bar

                matrices.translate(x, y, 0.5f); // Center in Z direction

                // Make items smaller and apply proper rotation
                matrices.scale(0.4f, 0.4f, 0.4f);

                // Flip texture for north/south facings to correct mirroring
                if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
                }

                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45.0f));

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
