package jackclarke95.homestead.client.render;

import jackclarke95.homestead.block.RackBlock;
import jackclarke95.homestead.block.entity.RackBlockEntity;
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
        ItemStack stack = entity.getStack(0);
        if (stack.isEmpty())
            return;

        // Get the block's facing direction
        Direction facing = entity.getCachedState().get(RackBlock.FACING);
        float blockRotation = switch (facing) {
            case NORTH -> 0f;
            case EAST -> 90f;
            case SOUTH -> 180f;
            case WEST -> 270f;
            default -> 0f;
        };

        matrices.push();
        matrices.translate(0.4f, 0.85f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockRotation));

        matrices.scale(1.0f, 1.0f, 1.0f);

        // Apply a slight slant to match the rack's surface
        float gradientAngle = switch (facing) {
            case NORTH, SOUTH -> -11.25f;
            case EAST, WEST -> 11.25f;
            default -> -11.25f;
        };
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f + gradientAngle));

        // For east/west, add a Z rotation for correct orientation
        if (facing == Direction.EAST || facing == Direction.WEST) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        }

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
