// VerticleSlabPlacementPreview.java
package jackclarke95.homestead.client;

import net.minecraft.client.render.VertexConsumer;
import jackclarke95.homestead.block.custom.VerticleSlabBlock;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
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

            Direction face = bhr.getSide();
            BlockPos pos = bhr.getBlockPos();

            if (!(block instanceof VerticleSlabBlock)) {
                return;
            }

            BlockState state = client.world.getBlockState(pos);

            if (state.isAir()) {
                return;
            }

            // Do not render preview if aiming at the open face of a half vertical slab that
            // would merge
            if (state.getBlock() instanceof VerticleSlabBlock &&
                    state.get(VerticleSlabBlock.TYPE) == jackclarke95.homestead.util.VerticalSlabType.HALF &&
                    block instanceof VerticleSlabBlock &&
                    face == state.get(Properties.HORIZONTAL_FACING).getOpposite()) {
                return;
            }

            // Do not render preview if the block in front of the targeted face contains a
            // half vertical slab (would merge)
            BlockPos mergeCheckPos = pos.offset(face);
            BlockState mergeCheckState = client.world.getBlockState(mergeCheckPos);
            if (mergeCheckState.getBlock() instanceof VerticleSlabBlock &&
                    mergeCheckState.get(VerticleSlabBlock.TYPE) == jackclarke95.homestead.util.VerticalSlabType.HALF &&
                    block instanceof VerticleSlabBlock) {
                return;
            }

            // Draw the cross on the actual face being aimed at, using the correct sub-box
            // in the VoxelShape based on hit position
            if ((face == Direction.UP || face == Direction.DOWN) && !(state.getBlock() instanceof VerticleSlabBlock)) {
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
                VoxelShape shape = state.getOutlineShape(client.world, pos, ShapeContext.absent());
                if (shape.isEmpty()) {
                    return;
                }
                Vec3d hit = bhr.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());
                final double[] best = new double[6];
                final boolean[] found = new boolean[1];
                final double threshold = 1e-4;
                shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                    if (face == Direction.UP) {
                        if (hit.x >= minX - threshold && hit.x <= maxX + threshold &&
                                hit.z >= minZ - threshold && hit.z <= maxZ + threshold &&
                                Math.abs(hit.y - maxY) < 0.2) {
                            best[0] = minX;
                            best[1] = maxY;
                            best[2] = minZ;
                            best[3] = maxX;
                            best[4] = minY;
                            best[5] = maxZ;
                            found[0] = true;
                        }
                    } else if (face == Direction.DOWN) {
                        if (hit.x >= minX - threshold && hit.x <= maxX + threshold &&
                                hit.z >= minZ - threshold && hit.z <= maxZ + threshold &&
                                Math.abs(hit.y - minY) < 0.2) {
                            best[0] = minX;
                            best[1] = maxY;
                            best[2] = minZ;
                            best[3] = maxX;
                            best[4] = minY;
                            best[5] = maxZ;
                            found[0] = true;
                        }
                    }
                });
                if (!found[0])
                    return;
                double eps = 0.001;
                double y = (face == Direction.UP ? best[1] + eps : best[4] - eps);
                Vec3d[] corners = new Vec3d[4];
                corners[0] = new Vec3d(best[0], y, best[2]).add(pos.getX(), pos.getY(), pos.getZ());
                corners[1] = new Vec3d(best[3], y, best[2]).add(pos.getX(), pos.getY(), pos.getZ());
                corners[2] = new Vec3d(best[3], y, best[5]).add(pos.getX(), pos.getY(), pos.getZ());
                corners[3] = new Vec3d(best[0], y, best[5]).add(pos.getX(), pos.getY(), pos.getZ());
                VertexConsumer vc = context.consumers().getBuffer(RenderLayer.getLines());
                drawCross(vc, context.matrixStack(), context.camera(), corners[0], corners[2], corners[1], corners[3]);
            }

            boolean facingVerticalSlab = state.getBlock() instanceof VerticleSlabBlock;
            // ...existing code...

            if (face.getAxis().isHorizontal()) {
                if (facingVerticalSlab) {
                    Direction slabFacing = state.get(Properties.HORIZONTAL_FACING);
                    if ((slabFacing.getAxis() == Direction.Axis.Z && face.getAxis() == Direction.Axis.X) ||
                            (slabFacing.getAxis() == Direction.Axis.X && face.getAxis() == Direction.Axis.Z)) {
                        return;
                    }
                }
                VoxelShape shape = state.getOutlineShape(client.world, pos, ShapeContext.absent());
                if (shape.isEmpty()) {
                    return;
                }
                Vec3d hit = bhr.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());
                final double[] best = new double[6];
                final boolean[] found = new boolean[1];
                final double threshold = 1e-4;
                shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                    switch (face) {
                        case NORTH:
                            if (hit.x >= minX - threshold && hit.x <= maxX + threshold &&
                                    hit.y >= minY - threshold && hit.y <= maxY + threshold &&
                                    Math.abs(hit.z - minZ) < 0.2) {
                                best[0] = minX;
                                best[1] = maxY;
                                best[2] = minZ;
                                best[3] = maxX;
                                best[4] = minY;
                                best[5] = maxZ;
                                found[0] = true;
                            }
                            break;
                        case SOUTH:
                            if (hit.x >= minX - threshold && hit.x <= maxX + threshold &&
                                    hit.y >= minY - threshold && hit.y <= maxY + threshold &&
                                    Math.abs(hit.z - maxZ) < 0.2) {
                                best[0] = minX;
                                best[1] = maxY;
                                best[2] = minZ;
                                best[3] = maxX;
                                best[4] = minY;
                                best[5] = maxZ;
                                found[0] = true;
                            }
                            break;
                        case WEST:
                            if (hit.z >= minZ - threshold && hit.z <= maxZ + threshold &&
                                    hit.y >= minY - threshold && hit.y <= maxY + threshold &&
                                    Math.abs(hit.x - minX) < 0.2) {
                                best[0] = minX;
                                best[1] = maxY;
                                best[2] = minZ;
                                best[3] = maxX;
                                best[4] = minY;
                                best[5] = maxZ;
                                found[0] = true;
                            }
                            break;
                        case EAST:
                            if (hit.z >= minZ - threshold && hit.z <= maxZ + threshold &&
                                    hit.y >= minY - threshold && hit.y <= maxY + threshold &&
                                    Math.abs(hit.x - maxX) < 0.2) {
                                best[0] = minX;
                                best[1] = maxY;
                                best[2] = minZ;
                                best[3] = maxX;
                                best[4] = minY;
                                best[5] = maxZ;
                                found[0] = true;
                            }
                            break;
                        default:
                            break;
                    }
                });
                if (!found[0])
                    return;
                double eps = 0.001;
                // For vertical thirds, we need the face's bounds
                double x0 = best[0] + pos.getX();
                double x1 = best[3] + pos.getX();
                double y0 = best[4] + pos.getY();
                double y1 = best[1] + pos.getY();
                double z0, z1;
                if (face == Direction.NORTH) {
                    z0 = z1 = best[2] + pos.getZ() - eps;
                } else if (face == Direction.SOUTH) {
                    z0 = z1 = best[5] + pos.getZ() + eps;
                } else if (face == Direction.WEST) {
                    z0 = best[2] + pos.getZ();
                    z1 = best[5] + pos.getZ();
                } else if (face == Direction.EAST) {
                    z0 = best[2] + pos.getZ();
                    z1 = best[5] + pos.getZ();
                } else {
                    z0 = best[2] + pos.getZ();
                    z1 = best[5] + pos.getZ();
                }
                double x, z;
                if (face == Direction.WEST) {
                    x = x0 = x1 = best[0] + pos.getX() - eps;
                } else if (face == Direction.EAST) {
                    x = x0 = x1 = best[3] + pos.getX() + eps;
                }
                VertexConsumer vc = context.consumers().getBuffer(RenderLayer.getLines());
                drawVerticalThirds(vc, context.matrixStack(), context.camera(), face, x0, x1, y0, y1, z0, z1, eps);
                return;
            }
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

    // Draws two diagonal lines (cross) between the given pairs
    private static void drawCross(VertexConsumer vc, MatrixStack matrices, Camera camera, Vec3d a1, Vec3d a2, Vec3d b1,
            Vec3d b2) {
        drawLine(vc, matrices, camera, a1, a2);
        drawLine(vc, matrices, camera, b1, b2);
    }

    // Draws two vertical lines dividing the face into thirds
    private static void drawVerticalThirds(VertexConsumer vc, MatrixStack matrices, Camera camera, Direction face,
            double x0, double x1, double y0, double y1, double z0, double z1, double eps) {
        // Always use the full block width (0 to 1) for vertical lines
        switch (face) {
            case NORTH:
            case SOUTH: {
                double z = (face == Direction.NORTH ? z0 - eps : z1 + eps);
                double xBlockMin = Math.floor(x0);
                double xBlockMax = xBlockMin + 1.0;
                for (int i = 1; i <= 2; i++) {
                    double frac = i / 3.0;
                    double x = xBlockMin + (xBlockMax - xBlockMin) * frac;
                    drawLine(vc, matrices, camera, new Vec3d(x, y0, z), new Vec3d(x, y1, z));
                }
                break;
            }
            case EAST:
            case WEST: {
                double x = (face == Direction.WEST ? x0 - eps : x1 + eps);
                double zBlockMin = Math.floor(z0);
                double zBlockMax = zBlockMin + 1.0;
                for (int i = 1; i <= 2; i++) {
                    double frac = i / 3.0;
                    double z = zBlockMin + (zBlockMax - zBlockMin) * frac;
                    drawLine(vc, matrices, camera, new Vec3d(x, y0, z), new Vec3d(x, y1, z));
                }
                break;
            }
            default:
                break;
        }
    }
}