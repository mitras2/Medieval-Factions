package dansplugins.factionsystem.objects.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansplugins.factionsystem.MedievalFactions;
import dansplugins.factionsystem.objects.domain.specification.IGate;
import dansplugins.factionsystem.objects.helper.GateCoord;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Gate implements IGate {

	public Gate(String name) {
		setName(name);
	}

	public Gate() {
		// Default Constructor
	}

	private class GateJson {
		public String name;
		public String factionName;
		public String open;
		public String vertical;
		public String material;
		public String world;
		public String coord1;
		public String coord2;
		public String triggerCoord;
	}

	private String name = "gateName";
	private String factionName = "";
	private boolean open = false;
	private boolean vertical = true;
	private GateCoord coord1 = null;
	private GateCoord coord2 = null;
	private GateCoord trigger = null;
	private Material material = Material.IRON_BARS;
	private World _world = null;
	private String world = "";

	@Override
	public World getWorld()
	{
		if (_world != null)
		{
			return _world;
		}
		_world = MedievalFactions.getInstance().getServer().getWorld(world);
		return _world;
	}

	@Override
	public void setWorld(String worldName)
	{
		world = worldName;
		_world = null;
	}

	private Sound soundEffect = Sound.BLOCK_ANVIL_HIT;

	;
	private GateStatus gateStatus = GateStatus.READY;

	public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();;
        Map<String, String> saveMap = new HashMap<>();

        saveMap.put("name", name);
        saveMap.put("factionName", factionName);
        saveMap.put("open", String.valueOf(open));
        saveMap.put("vertical", String.valueOf(vertical));
        saveMap.put("material", material.name());
        saveMap.put("world", coord1.getWorld());
        saveMap.put("coord1", coord1.toString());
        saveMap.put("coord2", coord2.toString());
        saveMap.put("triggerCoord", trigger.toString());

        return saveMap;
    }

	static Gate load(String jsonData) {
//    	System.out.println("Gate Load");

		Gson gson = new GsonBuilder().setPrettyPrinting().create();;

		Gate newGate = new Gate();

		try
		{
			Gate.GateJson data = gson.fromJson(jsonData, Gate.GateJson.class);

			newGate.world = data.world;
			newGate.coord1 = new GateCoord();
			newGate.coord1 = GateCoord.fromString(data.coord1);
			newGate.coord2 = new GateCoord();
			newGate.coord2 = GateCoord.fromString(data.coord2);
			newGate.trigger = new GateCoord();
			newGate.trigger = GateCoord.fromString(data.triggerCoord);
			newGate.material = Material.getMaterial(data.material);
			newGate.open = Boolean.parseBoolean(data.open);
			newGate.vertical = Boolean.parseBoolean(data.vertical);
		}
		catch (Exception e)
		{
			System.out.println("ERROR: Could not load faction gate.\n");
		}

		return newGate;
	}


	@Override
	public boolean isIntersecting(Gate gate)
	{
		boolean xoverlap = coord2.getX() > gate.coord1.getX() && coord1.getX() < coord2.getX();
		boolean yoverlap = coord2.getY() > gate.coord1.getY() && coord1.getY() < gate.coord1.getY();
		boolean zoverlap = coord2.getZ() > gate.coord1.getZ() && coord1.getZ() < coord2.getZ();
		return xoverlap && yoverlap && zoverlap;
	}

	@Override
	public int getTopLeftX()
	{
		if (coord1 != null && coord2 != null)
		{
			return coord1.getX() < coord2.getX() ? coord1.getX() : coord2.getX();
		}
		return 0;
	}

	@Override
	public int getTopLeftY()
	{
		if (coord1 != null && coord2 != null)
		{
			return coord1.getY() > coord2.getY() ? coord1.getY() : coord2.getY();
		}
		return 0;
	}

	@Override
	public int getTopLeftZ()
	{
		if (coord1 != null && coord2 != null)
		{
			return coord1.getZ() < coord2.getZ() ? coord1.getZ() : coord2.getZ();
		}
		return 0;
	}

	@Override
	public int getBottomRightX()
	{
		if (coord1 != null && coord2 != null)
		{
			return coord1.getX() < coord2.getX() ? coord2.getX() : coord1.getX();
		}
		return 0;
	}

	@Override
	public int getBottomRightY()
	{
		if (coord1 != null && coord2 != null)
		{
			return coord1.getY() < coord2.getY() ? coord1.getY() : coord2.getY();
		}
		return 0;
	}

	@Override
	public int getBottomRightZ()
	{
		if (coord1 != null && coord2 != null)
		{
			return coord1.getZ() < coord2.getZ() ? coord2.getZ() : coord1.getZ();
		}
		return 0;
	}

	@Override
	public int getTopLeftChunkX()
	{
		if (coord1 != null && coord2 != null)
		{
			return coord1.getX() < coord2.getX() ? coord1.getX() / 16: coord2.getX() / 16;
		}
		return 0;
	}

	@Override
	public int getTopLeftChunkZ()
	{
		if (coord1 != null && coord2 != null)
		{
			return coord1.getZ() < coord2.getZ() ? coord1.getZ() / 16 : coord2.getZ() / 16;
		}
		return 0;
	}

	@Override
	public int getBottomRightChunkX()
	{
		if (coord1 != null && coord2 != null)
		{
			return coord1.getX() < coord2.getX() ? coord2.getX() / 16: coord1.getX() / 16;
		}
		return 0;
	}

	@Override
	public int getBottomRightChunkZ()
	{
		if (coord1 != null && coord2 != null)
		{
			return coord1.getZ() < coord2.getZ() ? coord2.getZ() / 16 : coord1.getZ() / 16;
		}
		return 0;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String value)
	{
		name = value;
	}

	@Override
	public boolean isOpen()
	{
		return open ? true : false;
	}

	@Override
	public boolean isReady()
	{
		return gateStatus.equals(GateStatus.READY);
	}

	@Override
	public boolean isClosed()
	{
		return open ? false : true;
	}

	@Override
	public String getStatus()
	{
		return gateStatus.toString().substring(0,1).toUpperCase() + gateStatus.toString().substring(1).toLowerCase();
	}

	@Override
	public GateCoord getTrigger()
	{
		return trigger;
	}

	@Override
	public GateCoord getCoord1()
	{
		return coord1;
	}

	@Override
	public GateCoord getCoord2()
	{
		return coord2;
	}

	@Override
	public boolean isParallelToZ()
	{
		if (coord1 != null && coord2 != null)
		{
			if (coord1.getZ() != coord2.getZ())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean isParallelToX()
	{
		if (coord1 != null && coord2 != null)
		{
			if (coord1.getX() != coord2.getX())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	@Override
	public ArrayList<Block> GateBlocks()
	{
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (int y = coord1.getY(); y < coord2.getY(); y++)
		{
			for (int z = coord1.getZ(); z < coord2.getZ(); z++)
			{
				for (int x = coord1.getX(); x < coord2.getX(); x++)
				{
					blocks.add(getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}

	@Override
	public boolean gateBlocksMatch(Material mat)
	{
		int topY = coord1.getY();
		int bottomY = coord2.getY();
		if (coord2.getY() > coord1.getY())
		{
			topY = coord2.getY();
			bottomY = coord1.getY();
		}

		int leftX = coord1.getX();
		int rightX = coord2.getX();
		if (coord2.getX() < coord1.getX())
		{
			leftX = coord2.getX();
			rightX = coord1.getX();
		}

		int leftZ = coord1.getZ();
		int rightZ = coord2.getZ();
		if (coord2.getZ() < coord1.getZ())
		{
			leftZ = coord2.getZ();
			rightZ = coord1.getZ();
		}

		if (isParallelToZ())
		{
			rightX++;
		}
		else if (isParallelToX())
		{
			rightZ++;
		}

		for (int y = topY; y > bottomY; y--)
		{
			for (int z = leftZ; z < rightZ; z++)
			{
				for (int x = leftX; x < rightX; x++)
				{
					if (!getWorld().getBlockAt(x, y, z).getType().equals(mat))
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public ErrorCodeAddCoord addCoord(Block clickedBlock)
	{
		if (coord1 == null)
		{
			setWorld(clickedBlock.getWorld().getName());
			coord1 = new GateCoord(clickedBlock);
			material = clickedBlock.getType();
		}
		else if (coord2 == null)
		{
			if (!coord1.getWorld().equalsIgnoreCase(clickedBlock.getWorld().getName()))
			{
				return ErrorCodeAddCoord.WorldMismatch;
			}
			if (!clickedBlock.getType().equals(material))
			{
				return ErrorCodeAddCoord.MaterialMismatch;
			}
			// GetDim methods use coord2 object.
			coord2 = new GateCoord(clickedBlock);
			if (getDimX() > 1 && getDimY() > 1 && getDimZ() > 1)
			{
				// No cuboids.
				coord2 = null;
				return ErrorCodeAddCoord.NoCuboids;
			}
			if (getDimY() <= 2)
			{
				coord2 = null;
				return ErrorCodeAddCoord.LessThanThreeHigh;
			}

			if (isParallelToX() && getDimY() > 1)
			{
				vertical = true;
			}
			else if (isParallelToZ() && getDimY() > 1)
			{
				vertical = true;
			}
			else
			{
				vertical = false;
			}

			int area = 0;
			if (vertical)
			{
				if (isParallelToX())
				{
					area = getDimX() * getDimY();
				}
				else if (isParallelToZ())
				{
					area = getDimZ() * getDimY();
				}
			}
			else if (!vertical)
			{
				if (isParallelToX())
				{
					area = getDimX() * getDimY();
				}
				else if (isParallelToZ())
				{
					area = getDimZ() * getDimY();
				}
			}
			if (area > MedievalFactions.getInstance().getConfig().getInt("factionMaxGateArea"))
			{
				// Gate size exceeds config limit.
				coord2 = null;
				return ErrorCodeAddCoord.Oversized;
			}
			if (!gateBlocksMatch(material))
			{
				coord2 = null;
				return ErrorCodeAddCoord.MaterialMismatch;
			}
		}
		else
		{
			trigger = new GateCoord(clickedBlock);
		}
		return ErrorCodeAddCoord.None;
	}

	@Override
	public int getDimX()
	{
		return getDimX(coord1, coord2);
	}

	@Override
	public int getDimY()
	{
		return getDimY(coord1, coord2);
	}

	@Override
	public int getDimZ()
	{
		return getDimZ(coord1, coord2);
	}

	@Override
	public int getDimX(GateCoord first, GateCoord second)
	{
		GateCoord tmp;
		if (first.getX() > second.getX())
		{
			tmp = second;
			second = first;
			first = tmp;
		}
		return second.getX() - first.getX();
	}

	@Override
	public int getDimY(GateCoord first, GateCoord second)
	{
		GateCoord tmp;
		if (first.getY() > second.getY())
		{
			tmp = second;
			second = first;
			first = tmp;
		}
		return second.getY() - first.getY();
	}

	@Override
	public int getDimZ(GateCoord first, GateCoord second)
	{
		GateCoord tmp;
		if (first.getZ() > second.getZ())
		{
			tmp = second;
			second = first;
			first = tmp;
		}
		return second.getZ() - first.getZ();
	}

	@Override
	public void openGate()
	{
		if (open || !gateStatus.equals(GateStatus.READY))
			return;
		open = true;
		gateStatus = GateStatus.OPENING;
		// For vertical we only need to iterate over x/y
		if (vertical)
		{
			if (isParallelToX())
			{
				int topY = coord1.getY();
				int _bottomY = coord2.getY();
				if (coord2.getY() > coord1.getY())
				{
					topY = coord2.getY();
					_bottomY = coord1.getY();
				}
				final int bottomY = _bottomY;

				int _leftX = coord1.getX();
				int _rightX = coord2.getX();
				if (coord2.getX() < coord1.getX())
				{
					_leftX = coord2.getX();
					_rightX = coord1.getX();
				}

				final int leftX = _leftX;
				final int rightX = _rightX;

				int c = 0;
				for (int y = bottomY; y <= topY; y++)
				{
					c++;
					final int blockY = y;
					Bukkit.getScheduler().runTaskLater(MedievalFactions.getInstance(), new Runnable() {
						Block b;
	                    @Override
	                    public void run() {
	        				for (int x = leftX; x <= rightX; x++)
	        				{
	        					b = getWorld().getBlockAt(x, blockY, coord1.getZ());
	        					b.setType(Material.AIR);
	        					getWorld().playSound(b.getLocation(), soundEffect, 0.1f, 0.1f);
	        				}
	                    }
	                }, c * 10);
				}
				Bukkit.getScheduler().runTaskLater(MedievalFactions.getInstance(), new Runnable() {
					Block b;
                    @Override
                    public void run() {
    					gateStatus = GateStatus.READY;
    					open = true;
                    }
				}, (topY - bottomY + 2) * 10);
			}
			else if (isParallelToZ())
			{
				int topY = coord1.getY();
				int _bottomY = coord2.getY();
				if (coord2.getY() > coord1.getY())
				{
					topY = coord2.getY();
					_bottomY = coord1.getY();
				}
				final int bottomY = _bottomY;
				int _leftZ = coord1.getZ();
				int _rightZ = coord2.getZ();
				if (coord2.getZ() < coord1.getZ())
				{
					_leftZ = coord2.getZ();
					_rightZ = coord1.getZ();
				}

				final int leftZ = _leftZ;
				final int rightZ = _rightZ;

				int c = 0;
				for (int y = bottomY; y <= topY; y++)
				{
					c++;
					final int blockY = y;
					Bukkit.getScheduler().runTaskLater(MedievalFactions.getInstance(), new Runnable() {
						Block b;
	                    @Override
	                    public void run() {
	        				for (int z = leftZ; z <= rightZ; z++)
	        				{
	        					b = getWorld().getBlockAt(coord1.getX(), blockY, z);
	        					b.setType(Material.AIR);
	        					getWorld().playSound(b.getLocation(), soundEffect, 0.1f, 0.1f);
	        				}
	                    }
	                }, c * 10);
				}
				Bukkit.getScheduler().runTaskLater(MedievalFactions.getInstance(), new Runnable() {
					Block b;
                    @Override
                    public void run() {
    					gateStatus = GateStatus.READY;
    					open = true;
                    }
				}, (topY - bottomY + 2) * 10);

			}

		}
		else
		{
			// TODO: Bridge code iterates over x/z
		}
	}

	@Override
	public void closeGate()
	{

		if (!open || !gateStatus.equals(GateStatus.READY))
			return;

		open = false;
		gateStatus = GateStatus.CLOSING;
		// For vertical we only need to iterate over x/y
		if (vertical)
		{
			if (isParallelToX())
			{
				int topY = coord1.getY();
				int _bottomY = coord2.getY();
				if (coord2.getY() > coord1.getY())
				{
					topY = coord2.getY();
					_bottomY = coord1.getY();
				}
				final int bottomY = _bottomY;
				int _leftX = coord1.getX();
				int _rightX = coord2.getX();
				if (coord2.getX() < coord1.getX())
				{
					_leftX = coord2.getX();
					_rightX = coord1.getX();
				}

				final int leftX = _leftX;
				final int rightX = _rightX;

				int c = 0;
				for (int y = topY; y >= bottomY; y--)
				{
					c++;
					final int blockY = y;
					Bukkit.getScheduler().runTaskLater(MedievalFactions.getInstance(), new Runnable() {
						Block b;
	                    @Override
	                    public void run() {
	        				for (int x = leftX; x <= rightX; x++)
	        				{
	        					b = getWorld().getBlockAt(x, blockY, coord1.getZ());
	        					b.setType(material);
	        					getWorld().playSound(b.getLocation(), soundEffect, 0.1f, 0.1f);
	        				}
	                    }
	                }, c * 10);
				}
				Bukkit.getScheduler().runTaskLater(MedievalFactions.getInstance(), new Runnable() {
					Block b;
                    @Override
                    public void run() {
    					gateStatus = GateStatus.READY;
    					open = false;
                    }
				}, (topY - bottomY + 2) * 10);
			}
			else if (isParallelToZ())
			{
				int topY = coord1.getY();
				int _bottomY = coord2.getY();
				if (coord2.getY() > coord1.getY())
				{
					topY = coord2.getY();
					_bottomY = coord1.getY();
				}
				final int bottomY = _bottomY;
				int _leftZ = coord1.getZ();
				int _rightZ = coord2.getZ();

				if (coord2.getZ() < coord1.getZ())
				{
					_leftZ = coord2.getZ();
					_rightZ = coord1.getZ();
				}
				final int leftZ = _leftZ;
				final int rightZ = _rightZ;

				int c = 0;
				for (int y = topY; y >= bottomY; y--)
				{
					c++;
					final int blockY = y;
					Bukkit.getScheduler().runTaskLater(MedievalFactions.getInstance(), new Runnable() {
						Block b;
	                    @Override
	                    public void run() {
	        				for (int z = leftZ; z <= rightZ; z++)
	        				{
	        					b = getWorld().getBlockAt(coord1.getX(), blockY, z);
	        					b.setType(material);
	        					b.getState().update(true);
	        					getWorld().playSound(b.getLocation(), soundEffect, 0.1f, 0.1f);
	        				}
	                    }
	                }, c * 10);
				}
				Bukkit.getScheduler().runTaskLater(MedievalFactions.getInstance(), new Runnable() {
					Block b;
                    @Override
                    public void run() {
    					gateStatus = GateStatus.READY;
    					open = false;
                    }
				}, (topY - bottomY + 2) * 10);
			}
		}
		else
		{
			// TODO: Bridge code iterates over x/z
		}
	}

	@Override
	public void fillGate()
	{

		if (!open)
			return;

		open = false;
		// For vertical we only need to iterate over x/y
		if (vertical)
		{
			if (isParallelToX())
			{
				int topY = coord1.getY();
				int bottomY = coord2.getY();
				if (coord2.getY() > coord1.getY())
				{
					topY = coord2.getY();
					bottomY = coord1.getY();
				}

				int _leftX = coord1.getX();
				int _rightX = coord2.getX();
				if (coord2.getX() < coord1.getX())
				{
					_leftX = coord2.getX();
					_rightX = coord1.getX();
				}

				final int leftX = _leftX;
				final int rightX = _rightX;

				for (int y = topY; y >= bottomY; y--)
				{
					Block b = null;
    				for (int x = leftX; x <= rightX; x++)
    				{
    					b = getWorld().getBlockAt(x, y, coord1.getZ());
    					b.setType(material);
    				};
					if (b != null)
						getWorld().playSound(b.getLocation(), soundEffect, 0.1f, 0.1f);
				}
			}
			else if (isParallelToZ())
			{
				int topY = coord1.getY();
				int bottomY = coord2.getY();
				if (coord2.getY() > coord1.getY())
				{
					topY = coord2.getY();
					bottomY = coord1.getY();
				}

				int _leftZ = coord1.getZ();
				int _rightZ = coord2.getZ();

				if (coord2.getZ() < coord1.getZ())
				{
					_leftZ = coord2.getZ();
					_rightZ = coord1.getZ();
				}
				final int leftZ = _leftZ;
				final int rightZ = _rightZ;

				for (int y = topY; y >= bottomY; y--)
				{
					Block b = null;
    				for (int z = leftZ; z <= rightZ; z++)
    				{
    					b = getWorld().getBlockAt(coord1.getX(), y, z);
    					b.setType(material);
    				};
					if (b != null)
						getWorld().playSound(b.getLocation(), soundEffect, 0.1f, 0.1f);
				}
			}
		}
		else
		{
			// TODO: Bridge code iterates over x/z
		}
	}

	@Override
	public boolean hasBlock(Block targetBlock)
	{

		int topY = coord1.getY();
		int bottomY = coord2.getY();
		if (coord2.getY() > coord1.getY())
		{
			topY = coord2.getY();
			bottomY = coord1.getY();
		}

		int _leftZ = coord1.getZ();
		int _rightZ = coord2.getZ();

		if (coord2.getZ() < coord1.getZ())
		{
			_leftZ = coord2.getZ();
			_rightZ = coord1.getZ();
		}
		int leftZ = _leftZ;
		int rightZ = _rightZ;

		int _leftX = coord1.getX();
		int _rightX = coord2.getX();
		if (coord2.getX() < coord1.getX())
		{
			_leftX = coord2.getX();
			_rightX = coord1.getX();
		}

		int leftX = _leftX;
		int rightX = _rightX;

		if (targetBlock.getX() >= leftX && targetBlock.getX() <= rightX
				&& targetBlock.getY() >= bottomY && targetBlock.getY() <= topY
				&& targetBlock.getZ() >= leftZ && targetBlock.getZ() <= rightZ
				&& targetBlock.getWorld().getName().equalsIgnoreCase(coord1.getWorld()))
		{
			return true;
		}

		if (trigger.equals(targetBlock))
		{
			return true;
		}

		return false;
	}

	@Override
	public String coordsToString()
	{
		if (coord1 == null || coord2 == null || trigger == null)
			return "";

		return String.format("(%d, %d, %d to %d, %d, %d) Trigger (%d, %d, %d)", coord1.getX(), coord1.getY(), coord1.getZ(), coord2.getX(), coord2.getY(), coord2.getZ(),
				trigger.getX(), trigger.getY(), trigger.getZ());
	}
}
