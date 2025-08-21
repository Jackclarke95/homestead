package jackclarke95.dryingrack.client.render;

import jackclarke95.dryingrack.block.RackBlock;
import jackclarke95.dryingrack.block.entity.RackBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class RackBlockEntityRenderer implements BlockEntityRenderer<RackBlockEntity> {
    private final ItemRenderer itemRenderer;

    public RackBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(RackBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Debug: Check if we have items to render
        int itemCount = 0;
        for (int i = 0; i < 4; i++) {
            if (!entity.getStack(i).isEmpty())
                itemCount++;
        }

        if (itemCount > 0) {
        }

        // Get the block's facing direction
        Direction facing = entity.getCachedState().get(RackBlock.FACING);

        // Calculate the rotation angle based on facing direction
        float blockRotation = switch (facing) {
            case NORTH -> 0f;
            case EAST -> 90f;
            case SOUTH -> 180f;
            case WEST -> 270f;
            default -> 0f;
        };

        // Render items lying on the rack
        for (int slot = 0; slot < 4; slot++) {
            matrices.push();
            ItemStack stack = entity.getStack(slot);
            if (!stack.isEmpty()) {

                // Apply block rotation first - rotate around the center of the block
                matrices.translate(0.5f, 0f, 0.5f); // Move to block center
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockRotation));
                matrices.translate(-0.5f, 0f, -0.5f); // Move back

                // Calculate positioning for 2x2 grid with precise pixel-based layout
                // Rack surfaces go from X=2 to X=14 (12 pixels wide), Z=1 to Z=15 (14 pixels
                // deep)
                // Layout: [1px edge][4px item][2px gap][4px item][1px edge] = 12 pixels total ✓
                // Items are 4x4 pixels each with 2 pixels between items, 1 pixel from edges

                // Reverse the visual slot order as requested
                // Original: 0|1 -> New: 3|2
                // 2|3 1|0
                // For East/West: swap the order again
                int visualSlot;
                if (facing == Direction.EAST || facing == Direction.WEST) {
                    // For east/west: use original slot order (no reversal)
                    visualSlot = slot;
                } else {
                    // For north/south: reverse the slot order
                    visualSlot = 3 - slot;
                }

                // Determine row and column for the reversed visual slot
                int row = visualSlot / 2; // 0 for visual slots 0,1 and 1 for visual slots 2,3
                int col = visualSlot % 2; // 0 for visual slots 0,2 and 1 for visual slots 1,3

                // Calculate exact pixel positions (each unit = 1/16 of a block)
                float itemSize = 4.0f / 16.0f; // 4 pixels = 0.25 block units
                float itemGap = 2.0f / 16.0f; // 2 pixels between items

                // Starting positions - different for east/west vs north/south
                boolean isEastWest = (facing == Direction.EAST || facing == Direction.WEST);
                float startX = 3.0f / 16.0f; // Pixel 3 (2 + 1 edge gap) - same for all
                float startZ;
                if (isEastWest) {
                    // For east/west: move backwards by 2 pixels
                    startZ = 4.0f / 16.0f; // Pixel 4 (moved backwards by 2 pixels)
                } else {
                    // For north/south: original position
                    startZ = 2.0f / 16.0f; // Pixel 2 (rack starts at Z=1, plus 1 edge gap)
                }

                // Calculate item center positions
                // First item: pixels 3-7, center at 5. Second item: pixels 9-13, center at 11
                float x = startX + (col * (itemSize + itemGap)) + (itemSize / 2.0f);
                float z = startZ + (row * (itemSize + itemGap)) + (itemSize / 2.0f);

                // Position items ON the slanted tabletop surface with height adjustments
                // Base height - different for east/west vs north/south
                float baseY;
                if (isEastWest) {
                    // For east/west: move down by 1 pixel
                    baseY = 11.5f / 16.0f; // Y=11.5 (moved down by 1 pixel)
                } else {
                    // For north/south: original height
                    baseY = 12.5f / 16.0f; // Y=12.5 (original height)
                }

                // Adjust height based on row and facing direction
                // For NORTH/SOUTH: front row (row 0) +0.5 pixel, back row (row 1) +2 pixels
                // For EAST/WEST: reverse the heights - front row (row 0) +2 pixels, back row
                // (row 1) +0.5 pixels
                float heightAdjustment;
                if (isEastWest) {
                    // Reverse heights for east/west facing and raise by 1 additional pixel
                    heightAdjustment = (row == 0) ? 3.0f / 16.0f : 1.5f / 16.0f; // +1 pixel to each
                } else {
                    // Normal heights for north/south facing
                    heightAdjustment = (row == 0) ? 0.5f / 16.0f : 2.0f / 16.0f;
                }
                float y = baseY + heightAdjustment;

                matrices.translate(x, y, z);

                // Scale items to fit 4x4 pixel area
                // Base item size is typically ~16 pixels, scale to 4 pixels = 0.25 scale
                matrices.scale(0.6f, 0.6f, 0.6f);

                // Apply gradient angle: 1 in 4 slope = arctan(1/4) ≈ 14.04 degrees
                // Direction depends on block facing: North/South = +14.04°, East/West = -14.04°
                float gradientAngle = switch (facing) {
                    case NORTH, SOUTH -> -22.5f; // Correct direction for north/south
                    case EAST, WEST -> 22.5f; // Opposite direction for east/west
                    default -> -22.5f;
                };

                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f + gradientAngle));

                // Add 180-degree Z-axis rotation for east/west orientations only
                if (facing == Direction.EAST || facing == Direction.WEST) {
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
                }

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
            }
            matrices.pop();
        }
    }
}
