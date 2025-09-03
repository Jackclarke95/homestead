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

            if (state.getBlock() instanceof VerticleSlabBlock &&
                    state.get(VerticleSlabBlock.TYPE) == jackclarke95.homestead.util.VerticalSlabType.HALF &&
                    block instanceof VerticleSlabBlock &&
                    face == state.get(Properties.HORIZONTAL_FACING).getOpposite()) {
                return;
            }

            BlockPos mergeCheckPos = pos.offset(face);
            BlockState mergeCheckState = client.world.getBlockState(mergeCheckPos);
            if (mergeCheckState.getBlock() instanceof VerticleSlabBlock &&
                    mergeCheckState.get(VerticleSlabBlock.TYPE) == jackclarke95.homestead.util.VerticalSlabType.HALF &&
                    block instanceof VerticleSlabBlock) {
                return;
            }

            if (face.getAxis().isVertical() && !(state.getBlock() instanceof VerticleSlabBlock)) {
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
                double y = (face == Direction.UP ? best[1] : best[4]);
                VertexConsumer vc = context.consumers().getBuffer(RenderLayer.getLines());

                int steps = 16;

                java.util.function.BiFunction<Double, Double, Boolean> isInside = (x, z) -> {
                    final boolean[] inside = { false };
                    double yCheck = (face == Direction.UP ? best[1] - 1e-6 : best[4] + 1e-6);
                    shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                        if (x >= minX - 1e-7 && x <= maxX + 1e-7 &&
                                z >= minZ - 1e-7 && z <= maxZ + 1e-7 &&
                                yCheck >= minY - 1e-7 && yCheck <= maxY + 1e-7) {
                            inside[0] = true;
                        }
                    });
                    return inside[0];
                };

                for (int i = 0; i < steps; i++) {
                    double t0 = i / 16.0;
                    double t1 = (i + 1) / 16.0;
                    double x0 = t0, z0 = t0;
                    double x1 = t1, z1 = t1;
                    boolean inside0 = isInside.apply(x0, z0);
                    boolean inside1 = isInside.apply(x1, z1);
                    if (inside0 && inside1) {
                        Vec3d p0 = new Vec3d(x0, y, z0).add(pos.getX(), pos.getY(), pos.getZ());
                        Vec3d p1 = new Vec3d(x1, y, z1).add(pos.getX(), pos.getY(), pos.getZ());
                        drawLine(vc, context.matrixStack(), context.camera(), p0, p1);
                    }
                }

                for (int i = 0; i < steps; i++) {
                    double t0 = i / 16.0;
                    double t1 = (i + 1) / 16.0;
                    double x0 = t0, z0 = 1 - t0;
                    double x1 = t1, z1 = 1 - t1;
                    boolean inside0 = isInside.apply(x0, z0);
                    boolean inside1 = isInside.apply(x1, z1);
                    if (inside0 && inside1) {
                        Vec3d p0 = new Vec3d(x0, y, z0).add(pos.getX(), pos.getY(), pos.getZ());
                        Vec3d p1 = new Vec3d(x1, y, z1).add(pos.getX(), pos.getY(), pos.getZ());
                        drawLine(vc, context.matrixStack(), context.camera(), p0, p1);
                    }
                }
            }

            boolean facingVerticalSlab = state.getBlock() instanceof VerticleSlabBlock;

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
                double threshold = 1e-4;
                java.util.List<double[]> faceBoxes = new java.util.ArrayList<>();
                final double[] planeCoord = new double[1];
                switch (face) {
                    case NORTH:
                        planeCoord[0] = Math.round(hit.z * 16.0) / 16.0;
                        break;
                    case SOUTH:
                        planeCoord[0] = Math.round(hit.z * 16.0) / 16.0;
                        break;
                    case WEST:
                        planeCoord[0] = Math.round(hit.x * 16.0) / 16.0;
                        break;
                    case EAST:
                        planeCoord[0] = Math.round(hit.x * 16.0) / 16.0;
                        break;
                    default:
                        break;
                }
                shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                    switch (face) {
                        case NORTH:
                            if (Math.abs(minZ - planeCoord[0]) < threshold)
                                faceBoxes.add(new double[] { minX, minY, minZ, maxX, maxY, maxZ });
                            break;
                        case SOUTH:
                            if (Math.abs(maxZ - planeCoord[0]) < threshold)
                                faceBoxes.add(new double[] { minX, minY, minZ, maxX, maxY, maxZ });
                            break;
                        case WEST:
                            if (Math.abs(minX - planeCoord[0]) < threshold)
                                faceBoxes.add(new double[] { minX, minY, minZ, maxX, maxY, maxZ });
                            break;
                        case EAST:
                            if (Math.abs(maxX - planeCoord[0]) < threshold)
                                faceBoxes.add(new double[] { minX, minY, minZ, maxX, maxY, maxZ });
                            break;
                        default:
                            break;
                    }
                });
                if (faceBoxes.isEmpty())
                    return;
                VertexConsumer vc = context.consumers().getBuffer(RenderLayer.getLines());
                for (double[] box : faceBoxes) {
                    double minX = box[0], minY = box[1], minZ = box[2], maxX = box[3], maxY = box[4], maxZ = box[5];
                    double y0 = minY + pos.getY();
                    double y1 = maxY + pos.getY();
                    switch (face) {
                        case NORTH:
                        case SOUTH: {
                            double z = (face == Direction.NORTH ? minZ + pos.getZ() : maxZ + pos.getZ());
                            for (int i = 1; i <= 2; i++) {
                                double frac = i / 3.0;
                                // Always use full block width for x
                                double x = frac;
                                if (x >= minX && x <= maxX) {
                                    double worldX = x + pos.getX();
                                    drawLine(vc, context.matrixStack(), context.camera(), new Vec3d(worldX, y0, z),
                                            new Vec3d(worldX, y1, z));
                                }
                            }
                            break;
                        }
                        case EAST:
                        case WEST: {
                            double x = (face == Direction.WEST ? minX + pos.getX() : maxX + pos.getX());
                            for (int i = 1; i <= 2; i++) {
                                double frac = i / 3.0;
                                // Always use full block width for z
                                double z = frac;
                                if (z >= minZ && z <= maxZ) {
                                    double worldZ = z + pos.getZ();
                                    drawLine(vc, context.matrixStack(), context.camera(), new Vec3d(x, y0, worldZ),
                                            new Vec3d(x, y1, worldZ));
                                }
                            }
                            break;
                        }
                        default:
                            break;
                    }
                }
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
}
