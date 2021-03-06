package ee.lutsu.alpha.mc.mytown.event.prot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import ee.lutsu.alpha.mc.mytown.ChunkCoord;
import ee.lutsu.alpha.mc.mytown.MyTown;
import ee.lutsu.alpha.mc.mytown.MyTownDatasource;
import ee.lutsu.alpha.mc.mytown.Utils;
import ee.lutsu.alpha.mc.mytown.entities.Resident;
import ee.lutsu.alpha.mc.mytown.entities.TownBlock;
import ee.lutsu.alpha.mc.mytown.entities.TownSettingCollection.Permissions;
import ee.lutsu.alpha.mc.mytown.event.ProtBase;
import ee.lutsu.alpha.mc.mytown.event.ProtectionEvents;

public class TinkersConstruct extends ProtBase {
    public static TinkersConstruct instance = new TinkersConstruct();

    private Class<?> clHammer, clExcavator, clBlueSlime;

    @Override
    public void load() throws Exception {
        clHammer = Class.forName("tconstruct.items.tools.Hammer");
        clExcavator = Class.forName("tconstruct.items.tools.Excavator");
        clBlueSlime = Class.forName("tconstruct.entity.BlueSlime");
    }

    /**
     * Check if a tool was used inside a town and sees if the user of the tool
     * is allowed to use it
     */
    @Override
    public String update(Resident res, Item tool, ItemStack item) throws Exception {
        if (clHammer.isInstance(tool) || clExcavator.isInstance(tool)) {
            MovingObjectPosition pos = Utils.getMovingObjectPositionFromPlayer(res.onlinePlayer.worldObj, res.onlinePlayer, false, 10.0D);
            if (pos == null) return null;
            
            if (!res.canInteract((int) pos.blockX, (int) pos.blockY, (int) pos.blockZ, Permissions.Build)){
                return "Cannot interact here";
            }
            
            for (int z = -1; z <= 1; z++) {
                for (int x = -1; x <= 1; x++) {
                    //Log.warning("Checking (%s, %s, %s)", pos2.xCoord + x, pos2.yCoord, pos2.zCoord + z);
                    if (!res.canInteract((int) pos.blockX + x, (int) pos.blockY, (int) pos.blockZ + z, Permissions.Build)) {
                        return "Cannot interact here";
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public String update(Entity e){
    	if (clBlueSlime.isInstance(e)){
    		EntityLiving mob = (EntityLiving) e;
            if (e.isEntityAlive()) {
                if (!canBe(mob.dimension, mob.posX, mob.posY, mob.posY +1, mob.posZ)) {
                    // silent removal of the mob
                    ProtectionEvents.instance.toRemove.add(e);
                    return null;
                }
            }
    	}
    	
    	return null;
    }

    private boolean canBe(int dim, double x, double yFrom, double yTo, double z) {
        TownBlock b = MyTownDatasource.instance.getBlock(dim, ChunkCoord.getCoord(x), ChunkCoord.getCoord(z));
        if (b != null && b.settings.yCheckOn) {
            if (yTo < b.settings.yCheckFrom || yFrom > b.settings.yCheckTo) {
                b = b.getFirstFullSidingClockwise(b.town());
            }
        }

        if (b == null || b.town() == null) {
            return !MyTown.instance.getWorldWildSettings(dim).disableMobs;
        }

        return !b.settings.disableMobs;
    }

    @Override
    public boolean isEntityInstance(Item e) {
        return clHammer.isInstance(e) || clExcavator.isInstance(e);
    }
    
    @Override
    public boolean isEntityInstance(Entity e){
    	return clBlueSlime.isInstance(e);
    }

    @Override
    public boolean loaded() {
        return clHammer != null;
    }

    @Override
    public String getMod() {
        return "Tinkers Construct";
    }

    @Override
    public String getComment() {
        return "Blocks Tinkers Construct tools";
    }

}
