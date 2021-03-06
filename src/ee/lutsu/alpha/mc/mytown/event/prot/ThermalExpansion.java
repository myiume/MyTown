package ee.lutsu.alpha.mc.mytown.event.prot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import ee.lutsu.alpha.mc.mytown.MyTownDatasource;
import ee.lutsu.alpha.mc.mytown.entities.Resident;
import ee.lutsu.alpha.mc.mytown.entities.TownSettingCollection.Permissions;
import ee.lutsu.alpha.mc.mytown.event.ProtBase;
import ee.lutsu.alpha.mc.mytown.event.ProtectionEvents;

public class ThermalExpansion extends ProtBase {
	public static ThermalExpansion instance = new ThermalExpansion();
	
	Class<?> clEntityFlorb;
	
    public void load() throws Exception {
    	clEntityFlorb = Class.forName("thermalexpansion.entity.EntityFlorb");
    }

    public boolean loaded() {
        return clEntityFlorb != null;
    }
    
    public boolean isEntityInstance(Entity e) {
        return clEntityFlorb.isInstance(e);
    }
    
    public String update(Entity e) throws Exception {
    	if (clEntityFlorb.isInstance(e)){
    		EntityThrowable t = (EntityThrowable) e;
    		EntityLivingBase owner = t.getThrower();

            if (owner == null || !(owner instanceof EntityPlayer)) {
                return "No owner or is not a player";
            }

            Resident thrower = ProtectionEvents.instance.lastOwner = MyTownDatasource.instance.getResident((EntityPlayer) owner);

            int x = (int) (t.posX + t.motionX);
            int y = (int) (t.posY + t.motionY);
            int z = (int) (t.posZ + t.motionZ);
            int dim = thrower.onlinePlayer.dimension;

            if (!thrower.canInteract(dim, x, y, z, Permissions.Build)) {
                return "Florb would land in a town";
            }
    	}
    	return null;
    }

	@Override
	public String getMod() {
		return "ThermalExpansion";
	}

	@Override
	public String getComment() {
		return "Build-Check: Florbs";
	}
}