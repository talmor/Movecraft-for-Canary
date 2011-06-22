import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class CraftBuilder {
	// private static Craft craft;
	// private static Logger log = Logger.getLogger("Minecraft");
	private static Stack<BlockLoc> blocksStack;
	private static HashMap<BlockLoc, BlockLoc> blocksList = null;
	private static HashMap<Integer, HashMap<Integer, HashMap<Integer, Short>>> dmatrix;
	private static Short nullBlock = -1;

	private static boolean isFree(Craft craft, int x, int y, int z) {
		if ((x < 0) || (x >= craft.sizeX) || (y < 0) || (y >= craft.sizeY)
				|| (z < 0) || (z >= craft.sizeZ)) {
			return true;
		}
		int blockId = craft.matrix[x][y][z];

		return (blockId == 0) || (blockId == -1);
	}

	private static Short get(int x, int y, int z) {
		HashMap<Integer, HashMap<Integer, Short>> xRow = dmatrix.get(new Integer(x));
		if (xRow != null) {
			HashMap<Integer,Short> yRow = xRow.get(new Integer(y));

			if (yRow != null) {
				return (Short) yRow.get(new Integer(z));
			}
		}

		return null;
	}

	private static void set(short blockType, int x, int y, int z) {
		HashMap<Integer, HashMap<Integer, Short>> xRow = dmatrix.get(new Integer(x));
		if (xRow == null) {
			xRow = new HashMap<Integer, HashMap<Integer, Short>>();
			dmatrix.put(new Integer(x), xRow);
		}
		
		HashMap<Integer,Short> yRow = xRow.get(new Integer(y));

		if (yRow == null) {
			yRow = new HashMap<Integer,Short>();
			xRow.put(new Integer(y), yRow);
		}

		Short type = (Short) yRow.get(new Integer(z));

		if (type == null)
			yRow.put(new Integer(z), new Short(blockType));
	}

	private static void detectWater(Craft craft, int x, int y, int z) {
		if ((x >= 0) && (x < craft.sizeX) && (y >= 0) && (y < craft.sizeY)
				&& (z >= 0) && (z < craft.sizeZ)
				&& (craft.matrix[x][y][z] != -1)) {
			return;
		}
		int blockId = craft.getWorld().getBlockIdAt(craft.posX + x,
				craft.posY + y, craft.posZ + z);

		if ((blockId == 8) || (blockId == 9)) {
			if (y > craft.waterLevel)
				craft.waterLevel = y;
			craft.waterType = 8;
			return;
		}

		if ((blockId == 10) || (blockId == 11)) {
			if (y > craft.waterLevel)
				craft.waterLevel = y;
			craft.waterType = 10;
			return;
		}
	}

	private static void removeWater(Craft craft) {
		boolean updated;
		do {
			updated = false;

			for (int x = 0; x < craft.sizeX; x++) {
				for (int z = 0; z < craft.sizeZ; z++) {
					for (int y = 0; y < craft.sizeY; y++) {
						if ((craft.matrix[x][y][z] < 8)
								|| (craft.matrix[x][y][z] > 11))
							continue;
						if ((!isFree(craft, x + 1, y, z))
								&& (!isFree(craft, x - 1, y, z))
								&& (!isFree(craft, x, y, z + 1))
								&& (!isFree(craft, x, y, z - 1))
								&& (!isFree(craft, x, y - 1, z))) {
							continue;
						}

						craft.matrix[x][y][z] = -1;
						updated = true;
					}
				}

			}

		}

		while (updated);
	}

	private static boolean createAirBubble(Craft craft) {
		BlockLoc block = (BlockLoc) blocksStack.pop();

		if (blocksList.get(block) != null) {
			return true;
		}

		blocksList.put(block, block);

		if ((block.x < 0) || (block.x > craft.maxX - craft.posX)
				|| (block.y < 0) || (block.y > craft.maxY - craft.posY)
				|| (block.z < 0) || (block.z > craft.maxZ - craft.posZ)) {
			return false;
		}

		if (craft.matrix[block.x][block.y][block.z] == -1) {
			if ((block.x == 0) || (block.x == craft.maxX - craft.posX)
					|| (block.y == 0) || (block.y == craft.maxY - craft.posY)
					|| (block.z == 0) || (block.z == craft.maxZ - craft.posZ)) {
				return false;
			}

			craft.matrix[block.x][block.y][block.z] = 0;
		} else {
			return true;
		}

		blocksStack.push(new BlockLoc(block.x + 1, block.y, block.z));
		blocksStack.push(new BlockLoc(block.x - 1, block.y, block.z));
		blocksStack.push(new BlockLoc(block.x, block.y + 1, block.z));
		blocksStack.push(new BlockLoc(block.x, block.y - 1, block.z));
		blocksStack.push(new BlockLoc(block.x, block.y, block.z + 1));
		blocksStack.push(new BlockLoc(block.x, block.y, block.z - 1));

		return true;
	}

	private static boolean secondPassDetection(Craft craft) {
		for (int x = 0; x < craft.sizeX; x++) {
			for (int z = 0; z < craft.sizeZ; z++) {
				boolean floor = false;

				for (int y = 0; y < craft.sizeY; y++) {
					if ((!floor) && (craft.matrix[x][y][z] != -1)) {
						floor = true;
					} else {
						if ((!floor) || (craft.matrix[x][y][z] != -1))
							continue;
						int blockId = craft.getWorld().getBlockIdAt(
								craft.posX + x, craft.posY + y, craft.posZ + z);

						craft.matrix[x][y][z] = (short) blockId;

						if (BlocksInfo.isDataBlock(blockId)) {
							addDataBlock(craft, craft.posX + x, craft.posY + y,
									craft.posZ + z);
						}

						if (BlocksInfo.isComplexBlock(blockId)) {
							addComplexBlock(craft, craft.posX + x, craft.posY
									+ y, craft.posZ + z);
						}

						if (blockId == 79) {
							craft.player
									.sendMessage("§cSorry, you can't have ice in the "
											+ craft.name);
							return false;
						}
					}
				}

			}

		}

		if (craft.waterType != 0) {
			craft.waterLevel = -1;

			for (int x = 0; x < craft.sizeX; x++) {
				for (int z = 0; z < craft.sizeZ; z++) {
					for (int y = 0; y < craft.sizeY; y++) {
						if (craft.matrix[x][y][z] != -1) {
							detectWater(craft, x + 1, y, z);
							detectWater(craft, x - 1, y, z);
							detectWater(craft, x, y, z + 1);
							detectWater(craft, x, y, z - 1);
						}
					}
				}

			}

			removeWater(craft);
		}

		if (craft.waterLevel != -1) {
			for (int x = 0; x < craft.sizeX; x++) {
				for (int z = 0; z < craft.sizeZ; z++) {
					for (int y = craft.waterLevel + 1; y < craft.sizeY; y++) {
						if (craft.matrix[x][y][z] == 0)
							craft.matrix[x][y][z] = -1;
					}
				}
			}
		} else {
			for (int x = 0; x < craft.sizeX; x++) {
				for (int z = 0; z < craft.sizeZ; z++) {
					for (int y = 0; y < craft.sizeY; y++) {
						if (craft.matrix[x][y][z] == 0) {
							craft.matrix[x][y][z] = -1;
						}
					}
				}
			}

		}

		if (craft.type.canDive) {
			blocksList = new HashMap<BlockLoc, BlockLoc>();
			blocksStack = new Stack<BlockLoc>();

			blocksStack.push(new BlockLoc((int) Math.floor(craft.player.getX())
					- craft.posX, (int) Math.floor(craft.player.getY() + 1.0D
					- craft.posY), (int) Math.floor(craft.player.getZ())
					- craft.posZ));
			do {
				if (createAirBubble(craft))
					continue;
				craft.player.sendMessage("§eThis " + craft.type.name
						+ " have holes, it needs to be waterproof");
				return false;
			}

			while (!blocksStack.isEmpty());

			blocksStack = null;
			blocksList = null;
		}

		return true;
	}

	private static void addDataBlock(Craft craft, int x, int y, int z) {
		craft.dataBlocks.add(new Craft.DataBlock(x - craft.posX,
				y - craft.posY, z - craft.posZ, craft.getWorld().getBlockData(
						x, y, z)));
	}

	private static void addComplexBlock(Craft craft, int x, int y, int z) {
		craft.complexBlocks.add(new Craft.CraftComplexBlock(x - craft.posX, y
				- craft.posY, z - craft.posZ, null));
	}

	private static void createMatrix(Craft craft) {
		craft.matrix = new short[craft.sizeX][craft.sizeY][craft.sizeZ];
		craft.dataBlocks = new ArrayList<Craft.DataBlock>();
		craft.complexBlocks = new ArrayList<Craft.CraftComplexBlock>();

		for (int x = 0; x < craft.sizeX; x++) {
			for (int z = 0; z < craft.sizeZ; z++) {
				for (int y = 0; y < craft.sizeY; y++) {
					craft.matrix[x][y][z] = -1;
				}
			}
		}

		Integer x;
		HashMap<Integer, HashMap<Integer, Short>> xRow;
		Iterator<Integer> iX$,iY$;
		Integer y;
		HashMap<Integer, Short> yRow;

		for (iX$ = dmatrix.keySet().iterator(); iX$.hasNext();) {
			x = iX$.next();
			xRow = dmatrix.get(x);
			for (iY$ = xRow.keySet().iterator(); iY$.hasNext();) {
				y = iY$.next();
				yRow = (HashMap<Integer, Short>) xRow.get(y);
				for (Integer z : yRow.keySet()) {
					short blockId = ((Short) yRow.get(z)).shortValue();

					if (blockId == -1) {
						continue;
					}
					craft.matrix[(x.intValue() - craft.posX)][(y.intValue() - craft.posY)][(z
							.intValue() - craft.posZ)] = blockId;

					if (BlocksInfo.isDataBlock(blockId)) {
						addDataBlock(craft, x.intValue(), y.intValue(),
								z.intValue());
					}
					if (BlocksInfo.isComplexBlock(blockId))
						addComplexBlock(craft, x.intValue(), y.intValue(),
								z.intValue());
				}
			}
		}
		dmatrix = null;
	}

	private static void detectBlock(Craft craft, int x, int y, int z, int dir) {
		Short blockType = get(x, y, z);

		if (blockType != null)
			return;

		blockType = new Short((short) craft.getWorld().getBlockIdAt(x, y, z));
		// int blockData = craft.getWorld().getBlockData(x, y, z);

		if ((blockType.shortValue() == 8) || (blockType.shortValue() == 9)) {
			if (y > craft.waterLevel)
				craft.waterLevel = y;
			craft.waterType = 8;
			set(nullBlock.shortValue(), x, y, z);
			return;
		}

		if ((blockType.shortValue() == 10) || (blockType.shortValue() == 11)) {
			if (y > craft.waterLevel)
				craft.waterLevel = y;
			craft.waterType = 10;
			set(nullBlock.shortValue(), x, y, z);
			return;
		}

		if (blockType.shortValue() == 0) {
			set(nullBlock.shortValue(), x, y, z);
			return;
		}

		if (blockType.shortValue() == 55) {
			if (dir != 1) {
				set(nullBlock.shortValue(), x, y, z);
				return;
			}

		} else if (craft.type.structureBlocks == null) {
			if ((blockType.shortValue() != 4)
					&& (blockType.shortValue() != 5)
					&& (blockType.shortValue() != 17)
					&& (blockType.shortValue() != 19)
					&& (blockType.shortValue() != 20)
					&& (blockType.shortValue() != 24)
					&& (blockType.shortValue() != 35)
					&& ((blockType.shortValue() < 41) || (blockType
							.shortValue() > 50))
					&& (blockType.shortValue() != 53)
					&& (blockType.shortValue() != 55)
					&& (blockType.shortValue() != 57)
					&& (blockType.shortValue() != 65)
					&& (blockType.shortValue() != 67)
					&& (blockType.shortValue() != 68)
					&& (blockType.shortValue() != 69)
					&& (blockType.shortValue() != 75)
					&& (blockType.shortValue() != 76)
					&& (blockType.shortValue() != 77)
					&& (blockType.shortValue() != 85)
					&& (blockType.shortValue() != 87)
					&& (blockType.shortValue() != 88)
					&& (blockType.shortValue() != 89)) {
				set(nullBlock.shortValue(), x, y, z);
				return;
			}

		} else {
			boolean found = false;
			for (short blockId : craft.type.structureBlocks) {
				if (blockType.shortValue() != blockId)
					continue;
				found = true;
			}
			if (!found) {
				set(nullBlock.shortValue(), x, y, z);
				return;
			}

		}

		set(blockType.shortValue(), x, y, z);

		craft.blockCount += 1;
		if (craft.blockCount > craft.type.maxBlocks) {
			return;
		}

		if (blockType.shortValue() == craft.type.flyBlockType) {
			craft.flyBlockCount += 1;
		}

		if (x < craft.minX)
			craft.minX = x;
		if (x > craft.maxX)
			craft.maxX = x;
		if (y < craft.minY)
			craft.minY = y;
		if (y > craft.maxY)
			craft.maxY = y;
		if (z < craft.minZ)
			craft.minZ = z;
		if (z > craft.maxZ)
			craft.maxZ = z;

		if (BlocksInfo.needsSupport(blockType.shortValue()))
			return;

		blocksStack.push(new BlockLoc(x, y, z));
	}

	private static void detectBlock(Craft craft, BlockLoc block) {
		detectBlock(craft, block.x + 1, block.y, block.z, 1);
		detectBlock(craft, block.x - 1, block.y, block.z, 2);
		detectBlock(craft, block.x, block.y + 1, block.z, 1);
		detectBlock(craft, block.x, block.y - 1, block.z, 6);
		detectBlock(craft, block.x, block.y, block.z + 1, 3);
		detectBlock(craft, block.x, block.y, block.z - 1, 4);

		detectBlock(craft, block.x + 1, block.y - 1, block.z, -1);
		detectBlock(craft, block.x - 1, block.y - 1, block.z, -1);
		detectBlock(craft, block.x, block.y - 1, block.z + 1, -1);
		detectBlock(craft, block.x, block.y - 1, block.z - 1, -1);
		detectBlock(craft, block.x + 1, block.y + 1, block.z, -1);
		detectBlock(craft, block.x - 1, block.y + 1, block.z, -1);
		detectBlock(craft, block.x, block.y + 1, block.z + 1, -1);
		detectBlock(craft, block.x, block.y + 1, block.z - 1, -1);
	}

	public static boolean detect(Craft craft, int X, int Y, int Z) {

		dmatrix = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Short>>>();

		craft.blockCount = 0;

		craft.minX = (craft.maxX = X);
		craft.minY = (craft.maxY = Y);
		craft.minZ = (craft.maxZ = Z);

		blocksStack = new Stack<BlockLoc>();
		blocksStack.push(new BlockLoc(X, Y, Z));
		do {
			detectBlock(craft, (BlockLoc) blocksStack.pop());
		} while (!blocksStack.isEmpty());

		blocksStack = null;

		if (craft.blockCount > craft.type.maxBlocks) {
			craft.player.sendMessage("§cUnable to detect the " + craft.name
					+ ", be sure it is not connected");
			craft.player
					.sendMessage("§c to the ground, or maybe it is too big for this type of craft");
			craft.player.sendMessage("§cThe maximum size is "
					+ craft.type.maxBlocks + " blocks");
			return false;
		}

		if (craft.blockCount < craft.type.minBlocks) {
			if (craft.blockCount == 0) {
				craft.player.sendMessage("§cThere is no " + craft.name
						+ " here");
				craft.player
						.sendMessage("§cBe sure you are standing on a block");
			} else {
				craft.player.sendMessage("§cThis " + craft.name
						+ " is too small !");
				craft.player
						.sendMessage("§cYou need to add "
								+ (craft.type.minBlocks - craft.blockCount)
								+ " blocks");
			}

			return false;
		}

		for (Craft c : Craft.craftList) {
			if ((c != craft) && (c.isOnBoard)) {
				if (((craft.minX >= craft.minX) || (craft.maxX >= craft.minX))
						&& ((craft.minX >= craft.minX) || (craft.maxX >= craft.minX))
						&& ((craft.minY >= craft.minY) || (craft.maxY >= craft.minY))
						&& ((craft.minY >= craft.minY) || (craft.maxY >= craft.minY))
						&& ((craft.minZ >= craft.minZ) || (craft.maxZ >= craft.minZ))
						&& ((craft.minZ >= craft.minZ) || (craft.maxZ >= craft.minZ))) {
					craft.player.sendMessage("§c" + craft.player.getName()
							+ " is already controling this " + craft.name);
					return false;
				}
			}

		}

		craft.sizeX = (craft.maxX - craft.minX + 1);
		craft.sizeY = (craft.maxY - craft.minY + 1);

		craft.sizeZ = (craft.maxZ - craft.minZ + 1);

		craft.posX = craft.minX;
		craft.posY = craft.minY;
		craft.posZ = craft.minZ;

		if (craft.waterLevel != -1) {
			craft.waterLevel -= craft.posY;
		}

		createMatrix(craft);

		if (!secondPassDetection(craft)) {
			return false;
		}

		if ((craft.type.canNavigate) && (!craft.type.canFly)
				&& (craft.waterType == 0)) {
			craft.player.sendMessage("§cThis " + craft.name
					+ " is not on water...");
			return false;
		}

		if ((craft.type.canDive) && (!craft.type.canFly)
				&& (craft.waterType == 0)) {
			craft.player.sendMessage("§cThis " + craft.name
					+ " is not into water...");
			return false;
		}

		if ((craft.type.canFly) && (!craft.type.canNavigate)
				&& (!craft.type.canDive) && (craft.waterLevel > -1)) {
			craft.player.sendMessage("§cThis " + craft.name
					+ " is into water...");
			return false;
		}

		if ((craft.type.canFly) && (craft.type.flyBlockType != 0)) {
			int flyBlocksNeeded = (int) Math
					.floor((craft.blockCount - craft.flyBlockCount)
							* (craft.type.flyBlockPercent * 0.01D)
							/ (1.0D - craft.type.flyBlockPercent * 0.01D));

			if (flyBlocksNeeded < 1) {
				flyBlocksNeeded = 1;
			}
			if (craft.flyBlockCount < flyBlocksNeeded) {
				craft.player.sendMessage("§cNot enough "
						+ craft.type.flyBlockName + " to make this "
						+ craft.name + " move");
				craft.player.sendMessage("§cYou need to add "
						+ (flyBlocksNeeded - craft.flyBlockCount) + " more");
				return false;
			}
		}

		if (craft.customName == null)
			craft.player.sendMessage("§e" + craft.type.sayOnControl);
		else {
			craft.player
					.sendMessage("§eWelcome on " + craft.customName + " !");
		}

		return true;
	}
}

/*
 * Location: C:\Users\David\Downloads\MoveCraft.jar Qualified Name: CraftBuilder
 * JD-Core Version: 0.6.0
 */