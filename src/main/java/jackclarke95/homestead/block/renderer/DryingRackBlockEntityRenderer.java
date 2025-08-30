package jackclarke95.homestead.block.renderer;

import jackclarke95.homestead.block.entity.custom.DryingRackBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Direction;
import jackclarke95.homestead.block.custom.DryingRackBlock;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class DryingRackBlockEntityRenderer implements BlockEntityRenderer<DryingRackBlockEntity> {
    public DryingRackBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(DryingRackBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack stack = entity.getStack(0);

        matrices.push();

        Direction facing = getBlockFacing(entity);
        boolean isBlockItem = stack.getItem() instanceof BlockItem;

        matrices.translate(0.5f, getYTranslation(isBlockItem), 0.5f);

        matrices.scale(0.5f, 0.5f, 0.5f);

        // TODO: Rotate banners and shields to appear properly
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(getRotationForFacing(facing)));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(getXRotation(isBlockItem)));

        ModelTransformationMode mode = isBlockItem ? ModelTransformationMode.FIXED : ModelTransformationMode.GUI;

        itemRenderer.renderItem(stack, mode,
                getLightLevel(entity.getWorld(), entity.getPos()),
                OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);

        matrices.pop();
    }

    private Direction getBlockFacing(DryingRackBlockEntity entity) {
        Direction facing = Direction.WEST;
        if (entity.getWorld() != null) {
            net.minecraft.block.BlockState state = entity.getWorld().getBlockState(entity.getPos());
            if (state.contains(DryingRackBlock.FACING)) {
                facing = state.get(DryingRackBlock.FACING);
            }
        }
        return facing;
    }

    private float getYTranslation(boolean isBlockItem) {
        // Adjust as needed for your visuals
        return isBlockItem ? 0.8875f : 0.7625f;
    }

    private float getXRotation(boolean isBlockItem) {
        return isBlockItem ? -22.5f : (90f - 22.5f);
    }

    private float getRotationForFacing(Direction facing) {
        switch (facing) {
            case NORTH:
                return 0f;
            case EAST:
                return 270f;
            case SOUTH:
                return 180f;
            case WEST:
            default:
                return 90f;
        }
    }

    private int getLightLevel(World world, BlockPos pos) {
        int bLight = world.getLightLevel(LightType.BLOCK, pos);
        int sLight = world.getLightLevel(LightType.SKY, pos);

        return LightmapTextureManager.pack(bLight, sLight);
    }
}