package com.minecolonies.network.messages;

import com.minecolonies.colony.Colony;
import com.minecolonies.colony.ColonyManager;
import com.minecolonies.colony.buildings.BuildingFarmer;
import com.minecolonies.colony.permissions.Permissions;
import com.minecolonies.util.BlockPosUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Message to change the assignmentMode of the fields of the farmer.
 */
public class AssignmentModeMessage extends AbstractMessage<AssignmentModeMessage, IMessage>
{

    private int      colonyId;
    private BlockPos buildingId;
    private boolean  assignmentMode;

    /**
     * Empty standard constructor.
     */
    public AssignmentModeMessage()
    {
        super();
    }

    /**
     * Creates object for the assignmentMode message.
     *
     * @param building       View of the building to read data from.
     * @param assignmentMode assignmentMode of the particular farmer.
     */
    public AssignmentModeMessage(@NotNull BuildingFarmer.View building, boolean assignmentMode)
    {
        super();
        this.colonyId = building.getColony().getID();
        this.buildingId = building.getID();
        this.assignmentMode = assignmentMode;
    }

    @Override
    public void fromBytes(@NotNull ByteBuf buf)
    {
        colonyId = buf.readInt();
        buildingId = BlockPosUtil.readFromByteBuf(buf);
        assignmentMode = buf.readBoolean();
    }

    @Override
    public void toBytes(@NotNull ByteBuf buf)
    {
        buf.writeInt(colonyId);
        BlockPosUtil.writeToByteBuf(buf, buildingId);
        buf.writeBoolean(assignmentMode);
    }

    @Override
    public void messageOnServerThread(final AssignmentModeMessage message, final EntityPlayerMP player)
    {
        final Colony colony = ColonyManager.getColony(message.colonyId);
        if (colony != null)
        {
            //Verify player has permission to change this huts settings
            if (!colony.getPermissions().hasPermission(player, Permissions.Action.MANAGE_HUTS))
            {
                return;
            }

            @Nullable final BuildingFarmer building = colony.getBuilding(message.buildingId, BuildingFarmer.class);
            if (building != null)
            {
                building.setAssignManually(message.assignmentMode);
            }
        }
    }
}
