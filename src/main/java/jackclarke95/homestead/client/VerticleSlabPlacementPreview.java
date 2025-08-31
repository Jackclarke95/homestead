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
            Vec3d blockOrigin = Vec3d.of(pos);
            double y = face == Direction.UP ? 1.001 : 0.001;
            Vec3d center = blockOrigin.add(0.5, y, 0.5);
            Vec3d north = blockOrigin.add(0.5, y, 0.0);
            Vec3d south = blockOrigin.add(0.5, y, 1.0);
            Vec3d east = blockOrigin.add(1.0, y, 0.5);
            Vec3d west = blockOrigin.add(0.0, y, 0.5);

            VertexConsumer vc = context.consumers().getBuffer(RenderLayer.getLines());

            drawTriangle(vc, context.matrixStack(), context.camera(), center, north, east);
            drawTriangle(vc, context.matrixStack(), context.camera(), center, east, south);
            drawTriangle(vc, context.matrixStack(), context.camera(), center, south, west);
            drawTriangle(vc, context.matrixStack(), context.camera(), center, west, north);
        });
    }

    private static void drawTriangle(VertexConsumer vc, MatrixStack matrices, Camera camera, Vec3d v1, Vec3d v2,
            Vec3d v3) {
        Vec3d cam = camera.getPos();
        float r = 0.2f, g = 0.8f, b = 1.0f, a = 0.7f;

        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);
        MatrixStack.Entry entry = matrices.peek();

        addVertex(vc, entry, v1, r, g, b, a);
        addVertex(vc, entry, v2, r, g, b, a);
        addVertex(vc, entry, v2, r, g, b, a);
        addVertex(vc, entry, v3, r, g, b, a);
        addVertex(vc, entry, v3, r, g, b, a);
        addVertex(vc, entry, v1, r, g, b, a);

        matrices.pop();
    }

    private static void addVertex(VertexConsumer vc, MatrixStack.Entry entry, Vec3d pos, float r, float g, float b,
            float a) {
        vc.vertex(entry.getPositionMatrix(), (float) pos.x, (float) pos.y, (float) pos.z)
                .color(r, g, b, a)
                .light(0xF000F0)
                .normal(0, 1, 0);
    }
}