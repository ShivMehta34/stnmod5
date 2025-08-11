package net.tacoman.stnmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CrosshairPick {
    public static Entity livingUnderCrosshair(double range) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return null;
        Player p = mc.player;

        Vec3 eye = p.getEyePosition(1.0F);
        Vec3 look = p.getViewVector(1.0F);
        Vec3 end = eye.add(look.x * range, look.y * range, look.z * range);

        // block ray
        HitResult blockHit = p.level().clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, p));
        double max = range;
        if (blockHit != null && blockHit.getType() != HitResult.Type.MISS) {
            max = blockHit.getLocation().distanceTo(eye);
            end = blockHit.getLocation();
        }

        // entity tunnel
        Entity bestE = null;
        double bestD = max;
        AABB box = p.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0D, 1.0D, 1.0D);
        List<Entity> list = mc.level.getEntities(p, box, e -> e instanceof LivingEntity && e.isPickable() && e.isAlive() && e != p);

        for (Entity e : list) {
            AABB aabb = e.getBoundingBox().inflate(0.3D);
            var opt = aabb.clip(eye, end);
            if (opt.isPresent()) {
                double d = eye.distanceTo(opt.get());
                if (d < bestD) {
                    bestD = d;
                    bestE = e;
                }
            }
        }
        return (bestE instanceof LivingEntity) ? bestE : null;
    }
}
