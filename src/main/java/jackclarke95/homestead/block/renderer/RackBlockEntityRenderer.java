package jackclarke95.homestead.block.renderer;

import jackclarke95.homestead.block.entity.custom.RackBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Direction;
import jackclarke95.homestead.block.custom.RackBlock;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class RackBlockEntityRenderer implements BlockEntityRenderer<RackBlockEntity> {
    public RackBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(RackBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        ItemStack stack = entity.getStack(0);

        // Get block facing
        Direction facing = Direction.WEST;

        if (entity.getWorld() != null) {
            net.minecraft.block.BlockState state = entity.getWorld().getBlockState(entity.getPos());
            if (state.contains(RackBlock.FACING)) {
                facing = state.get(RackBlock.FACING);
            }
        }

        matrices.push();

        if (stack.getItem() instanceof net.minecraft.item.BlockItem) {
            matrices.translate(0.5f, 0.88f, 0.5f);
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(getRotationForFacing(facing)));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-22.5f));

            itemRenderer.renderItem(stack, ModelTransformationMode.FIXED,
                    getLightLevel(entity.getWorld(), entity.getPos()),
                    OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);
        } else {
            matrices.translate(0.5f, 0.7625f, 0.5f);
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(getRotationForFacing(facing)));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((90f - 22.5f)));

            itemRenderer.renderItem(stack, ModelTransformationMode.GUI,
                    getLightLevel(entity.getWorld(), entity.getPos()),
                    OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 0);
        }

        matrices.pop();
    }

    private float getRotationForFacing(Direction facing) {
        // WEST is default (90), adjust for others
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
