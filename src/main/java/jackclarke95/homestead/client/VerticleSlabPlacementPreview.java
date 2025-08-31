// VerticleSlabPlacementPreview.java
package jackclarke95.homestead.client;

import net.minecraft.client.render.VertexConsumer;
import jackclarke95.homestead.block.custom.VerticleSlabBlock;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;

public class VerticleSlabPlacementPreview {
    public static void register() {
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.crosshairTarget == null || client.player == null) {
                return;
            }

            if (!(client.crosshairTarget instanceof BlockHitResult bhr)) {
                return;
            }

            ItemStack held = client.player.getStackInHand(Hand.MAIN_HAND);

            if (held.isEmpty()) {
                return;
            }
            if (!(held.getItem() instanceof BlockItem)) {
                return;
            }

            Block block = ((BlockItem) held.getItem()).getBlock();

            if (!(block instanceof VerticleSlabBlock)) {
                return;
            }

            Direction face = bhr.getSide();

            if (face != Direction.UP && face != Direction.DOWN) {
                return;
            }

            BlockPos pos = bhr.getBlockPos();

            if (face == Direction.UP) {
                BlockPos above = pos.up();

                if (!client.world.getBlockState(above).isAir()) {
                    return;
                }
            } else if (face == Direction.DOWN) {
                BlockPos below = pos.down();

                if (!client.world.getBlockState(below).isAir()) {
                    return;
                }
            }

            Vec3d blockOrigin = Vec3d.of(pos);
            double y = face == Direction.UP ? 1.001 : 0.001;
            Vec3d vecA1 = blockOrigin.add(0, y, 0);
            Vec3d vecA2 = blockOrigin.add(1, y, 1);
            Vec3d vecB1 = blockOrigin.add(0, y, 1);
            Vec3d vecB2 = blockOrigin.add(1, y, 0.);

            VertexConsumer vc = context.consumers().getBuffer(RenderLayer.getLines());
            drawLine(vc, context.matrixStack(), context.camera(), vecA1, vecA2);
            drawLine(vc, context.matrixStack(), context.camera(), vecB1, vecB2);
        });
    }

    private static void drawLine(VertexConsumer vc, MatrixStack matrices, Camera camera, Vec3d v1, Vec3d v2) {
        Vec3d cam = camera.getPos();

        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);
        MatrixStack.Entry entry = matrices.peek();

        addVertex(vc, entry, v1);
        addVertex(vc, entry, v2);

        matrices.pop();
    }

    private static void addVertex(VertexConsumer vc, MatrixStack.Entry entry, Vec3d pos) {
        vc.vertex(entry.getPositionMatrix(), (float) pos.x, (float) pos.y, (float) pos.z)
                .color(0, 0, 0, 255)
                .light(0xF000F0)
                .normal(0, 1, 0);
    }
}