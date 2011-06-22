import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Craft {
	public static ArrayList<Craft> craftList = new ArrayList<Craft>();
	private static Logger log = Logger.getLogger("Minecraft");

	CraftType type;
	String name;
	short[][][] matrix;
	ArrayList<Craft.DataBlock> dataBlocks;
	ArrayList<Craft.CraftComplexBlock> complexBlocks;
	World world;
	int sizeX = 0;
	int sizeZ = 0;
	int sizeY = 0;
	int posX;
	int posY;
	int posZ;
	int blockCount = 0;
	int flyBlockCount = 0;
	int maxBlocks;
	int waterLevel = -1;
	int newWaterLevel = -1;

	short waterType = 0;

	int minX = 0;
	int maxX = 0;
	int minY = 0;
	int maxY = 0;
	int minZ = 0;
	int maxZ = 0;
	Player player;
	int speed = 1;

	long lastMove = System.currentTimeMillis();
	boolean haveControl = true;
	boolean isOnBoard = true;
	String customName = null;

	boolean blockPlaced = false;

	Craft(World w, CraftType type, Player player, String customName) {
		this.type = type;
		this.name = type.name;
		this.customName = customName;
		this.player = player;
		this.world = w;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public static Craft getCraft(Player player) {
		if (craftList.isEmpty())
			return null;

		for (Craft craft : craftList) {
			if (craft.player.getName().equalsIgnoreCase(player.getName())) {
				return craft;
			}
		}

		return null;
	}

	public static Craft getCraft(World w, int x, int y, int z) {
		if (craftList.isEmpty())
			return null;

		for (Craft craft : craftList) {
			if (craft.isIn(w, x, y, z)) {
				return craft;
			}
		}
		return null;
	}

	public void addBlock(Block block) {
		int x = block.getX() - this.posX;
		int y = block.getY() - this.posY;
		int z = block.getZ() - this.posZ;

		if (((x < this.sizeX - 1) && (!isFree(this.matrix[(x + 1)][y][z])))
				|| ((x > 0) && (!isFree(this.matrix[(x - 1)][y][z])))
				|| ((y < this.sizeY - 1) && (!isFree(this.matrix[x][(y + 1)][z])))
				|| ((y > 0) && (!isFree(this.matrix[x][(y - 1)][z])))
				|| ((z < this.sizeZ - 1) && (!isFree(this.matrix[x][y][(z + 1)])))
				|| ((z > 0) && (!isFree(this.matrix[x][y][(z - 1)])))) {
			short blockId = (short) block.getType();

			if (blockId == 331) {
				blockId = 55;
			} else if (blockId == 323) {
				blockId = 68;
			} else if (blockId == 324) {
				blockId = 64;
				this.matrix[x][(y + 1)][z] = blockId;
				this.dataBlocks.add(new Craft.DataBlock(x, y + 1, z, block
						.getData() + 8));
				this.blockCount += 1;
			} else if (blockId == 330) {
				blockId = 71;
				this.matrix[x][(y + 1)][z] = blockId;
				this.dataBlocks.add(new Craft.DataBlock(x, y + 1, z, block
						.getData() + 8));
				this.blockCount += 1;
			} else if (blockId == 338) {
				blockId = 83;
			} else if (blockId >= 256) {
				return;
			}

			this.matrix[x][y][z] = blockId;

			if (BlocksInfo.isDataBlock(blockId)) {
				this.dataBlocks.add(new Craft.DataBlock(x, y, z, block
						.getData()));
			}

			this.blockCount += 1;
		}
	}

	public boolean isIn(World w, int x, int y, int z) {
		boolean in = (w.getType().name().equals(this.world.getType().name()))
				&& (x >= this.posX) && (x < this.posX + this.sizeX)
				&& (y >= this.posY) && (y < this.posY + this.sizeY)
				&& (z >= this.posZ) && (z < this.posZ + this.sizeZ);
		return in;
	}

	static void addCraft(Craft craft) {
		craftList.add(craft);
	}

	static void removeCraft(Craft craft) {
		craftList.remove(craft);
	}

	private boolean canGoThrough(int blockId, int data) {
		if ((blockId == 0)
				|| ((blockId >= 8) && (blockId <= 11) && (data != 0)))
			return true;

		if ((!this.type.canNavigate) && (!this.type.canDive))
			return false;

		if (((blockId == 8) || (blockId == 9)) && (this.waterType == 8))
			return true;

		if (((blockId == 10) || (blockId == 11)) && (this.waterType == 10))
			return true;

		return (blockId == 79) && (this.type.iceBreaker)
				&& (this.waterType == 8);
	}

	private static boolean isFree(int blockId) {
		return (blockId == 0) || (blockId == -1);
	}

	@SuppressWarnings("unused")
	private static boolean isAirOrWater(int blockId) {
		return (blockId == 0) || ((blockId >= 8) && (blockId <= 11));
	}

	public boolean isOnCraft(Player player, boolean precise) {
		int x = (int) Math.floor(player.getX());
		int y = (int) Math.floor(player.getY());
		int z = (int) Math.floor(player.getZ());

		if (isIn(player.getWorld(), x, y - 1, z)) {
			if (!precise)
				return true;

			if (this.matrix[(x - this.posX)][(y - this.posY - 1)][(z - this.posZ)] != -1) {
				return true;
			}
		}

		return false;
	}

	public static void setBlock(int id, World w, int x, int y, int z) {
		if ((y < 0) || (y > 127) || (id < 0) || (id > 255)) {
			MoveCraft.logger.log(Level.SEVERE, "Invalid setBlock : id=" + id
					+ " x=" + x + " y=" + y + " z=" + z);
			return;
		}

		w.setBlockAt(id, x, y, z);
	}

	private boolean isCraftBlock(int x, int y, int z) {
		if ((x >= 0) && (y >= 0) && (z >= 0) && (x < this.sizeX)
				&& (y < this.sizeY) && (z < this.sizeZ)) {
			return this.matrix[x][y][z] != -1;
		}
		return false;
	}

	public boolean canMove(int dx, int dy, int dz) {
		dx = this.speed * dx;
		dz = this.speed * dz;

		if (Math.abs(this.speed * dy) > 1) {
			dy = this.speed * dy / 2;
			if (Math.abs(dy) == 0)
				dy = (int) Math.signum(dy);

		}

		if ((this.posY + dy < 0) || (this.posY + this.sizeY + dy > 128)) {
			return false;
		}

		if (isOnCraft(this.player, true)) {
			int X = (int) Math.floor(this.player.getX()) + dx;
			int Y = (int) Math.floor(this.player.getY()) + dy;
			int Z = (int) Math.floor(this.player.getZ()) + dz;

			if (((!isCraftBlock(X - this.posX, Y - this.posY, Z - this.posZ)) && (!canGoThrough(
					this.getWorld().getBlockIdAt(X, Y, Z), 0)))
					|| ((!isCraftBlock(X - this.posX, Y + 1 - this.posY, Z
							- this.posZ)) && (!canGoThrough(this.getWorld()
							.getBlockIdAt(X, Y + 1, Z), 0)))) {
				return false;
			}
		}

		this.newWaterLevel = this.waterLevel;

		for (int x = 0; x < this.sizeX; x++) {
			for (int z = 0; z < this.sizeZ; z++) {
				for (int y = 0; y < this.sizeY; y++) {
					if ((isFree(this.matrix[x][y][z]))
							|| (isCraftBlock(x + dx, y + dy, z + dz))) {
						continue;
					}
					int blockId = this.getWorld().getBlockIdAt(
							this.posX + x + dx, this.posY + y + dy,
							this.posZ + z + dz);
					int blockData = this.getWorld().getBlockData(
							this.posX + x + dx, this.posY + y + dy,
							this.posZ + z + dz);

					if ((dy < 0) && (blockId >= 8) && (blockId <= 11)) {
						if (y > this.newWaterLevel) {
							this.newWaterLevel = y;
						}
					} else if ((dy > 0) && (blockId == 0)) {
						if (y - 1 < this.newWaterLevel) {
							this.newWaterLevel = (y - 1);
						}
					}

					if (!canGoThrough(blockId, blockData)) {
						return false;
					}
				}
			}

		}

		return true;
	}

	public void move(int dx, int dy, int dz) {
		dx = this.speed * dx;
		dz = this.speed * dz;

		if (Math.abs(this.speed * dy) > 1) {
			dy = this.speed * dy / 2;
			if (Math.abs(dy) == 0)
				dy = (int) Math.signum(dy);

		}

		/* Loop One */
		for (int x = 0; x < this.sizeX; x++) {
			for (int y = 0; y < this.sizeY; y++) {
				for (int z = 0; z < this.sizeZ; z++) {
					int craftBlockId = this.matrix[x][y][z];

					if ((craftBlockId == -1) || (craftBlockId == 0)
							|| ((craftBlockId >= 8) && (craftBlockId <= 11)))
						continue;
					int blockId = this.getWorld().getBlockIdAt(this.posX + x,
							this.posY + y, this.posZ + z);

					if ((craftBlockId == 46) && (this.type.bomber)) {
						continue;
					}
					if ((blockId == 0) || ((blockId >= 8) && (blockId <= 11))) {
						if ((this.waterType != 0) && (y <= this.waterLevel))
							this.matrix[x][y][z] = 0;
						else {
							this.matrix[x][y][z] = -1;
						}
						this.blockCount -= 1;
					}
				}

			}

		}

		for (Craft.CraftComplexBlock complexBlock : this.complexBlocks) {
			complexBlock.data = this.getWorld().getComplexBlock(
					this.posX + complexBlock.x, this.posY + complexBlock.y,
					this.posZ + complexBlock.z);
		}

		/* Loop Two */
		for (int x = 0; x < this.sizeX; x++) {
			for (int z = 0; z < this.sizeZ; z++) {
				for (int y = 0; y < this.sizeY; y++) {
					short blockId = this.matrix[x][y][z];

					if (!BlocksInfo.needsSupport(blockId)) {
						continue;
					}

					if (((blockId == 64) || (blockId == 71))
							&& (this.getWorld().getBlockData(this.posX + x,
									this.posY + y, this.posZ + z) >= 8)) {
						continue;
					}
					setBlock(0, this.getWorld(), this.posX + x, this.posY + y,
							this.posZ + z);
				}

			}

		}

		/* Loop Three */
		for (int x = 0; x < this.sizeX; x++) {
			for (int z = 0; z < this.sizeZ; z++) {
				for (int y = 0; y < this.sizeY; y++) {
					short blockId = this.matrix[x][y][z];
					
					if (blockId == -1) {
						continue;
					}
					if ((x - dx >= 0) && (y - dy >= 0) && (z - dz >= 0)
							&& (x - dx < this.sizeX) && (y - dy < this.sizeY)
							&& (z - dz < this.sizeZ)) {
						if ((this.matrix[(x - dx)][(y - dy)][(z - dz)] == -1)
								|| (BlocksInfo
										.needsSupport(this.matrix[(x - dx)][(y - dy)][(z - dz)]))) {
							if ((y > this.waterLevel)
									|| ((!this.type.canNavigate) && (!this.type.canDive)))
								setBlock(0, this.getWorld(), this.posX + x,
										this.posY + y, this.posZ + z);
							else {
								setBlock(this.waterType, this.getWorld(),
										this.posX + x, this.posY + y, this.posZ
												+ z);
							}
						}

					} else if ((y > this.waterLevel)
							|| ((!this.type.canNavigate) && (!this.type.canDive)))
						setBlock(0, this.getWorld(), this.posX + x, this.posY
								+ y, this.posZ + z);
					else {
						setBlock(this.waterType, this.getWorld(),
								this.posX + x, this.posY + y, this.posZ + z);
					}

					if (BlocksInfo.needsSupport(blockId)) {
						continue;
					}
					if ((x + dx >= 0) && (y + dy >= 0) && (z + dz >= 0)
							&& (x + dx < this.sizeX) && (y + dy < this.sizeY)
							&& (z + dz < this.sizeZ)) {
						if (this.matrix[x][y][z] != this.matrix[(x + dx)][(y + dy)][(z + dz)]) {
							setBlock(blockId, this.getWorld(), this.posX + dx
									+ x, this.posY + dy + y, this.posZ + dz + z);
						}
					} else {
						setBlock(blockId, this.getWorld(), this.posX + dx + x,
								this.posY + dy + y, this.posZ + dz + z);
					}
				}

			}

		}

		Block block = new Block();

		for (Craft.DataBlock dataBlock : this.dataBlocks) {
			if (BlocksInfo
					.needsSupport(this.matrix[dataBlock.x][dataBlock.y][dataBlock.z])) {
				block.setX(this.posX + dx + dataBlock.x);
				block.setY(this.posY + dy + dataBlock.y);
				block.setZ(this.posZ + dz + dataBlock.z);

				block.setType(this.matrix[dataBlock.x][dataBlock.y][dataBlock.z]);
				block.setData(dataBlock.data);
				block.update();
			} else {
				this.getWorld().setBlockData(this.posX + dx + dataBlock.x,
						this.posY + dy + dataBlock.y,
						this.posZ + dz + dataBlock.z, dataBlock.data);
			}

		}

		/* Loop Four */
		for (int x = 0; x < this.sizeX; x++) {
			for (int z = 0; z < this.sizeZ; z++) {
				for (int y = 0; y < this.sizeY; y++) {
					short blockId = this.matrix[x][y][z];

					if ((!BlocksInfo.needsSupport(blockId))
							|| (BlocksInfo.isDataBlock(blockId)))
						continue;
					setBlock(blockId, this.getWorld(), this.posX + dx + x,
							this.posY + dy + y, this.posZ + dz + z);
				}

			}

		}

		for (Craft.CraftComplexBlock complexBlock : this.complexBlocks) {
			if ((complexBlock.data instanceof Sign)) {
				Sign sign = (Sign) this.getWorld().getComplexBlock(
						this.posX + dx + complexBlock.x,
						this.posY + dy + complexBlock.y,
						this.posZ + dz + complexBlock.z);

				sign.setText(0, ((Sign) (Sign) complexBlock.data).getText(0));
				sign.setText(1, ((Sign) (Sign) complexBlock.data).getText(1));
				sign.setText(2, ((Sign) (Sign) complexBlock.data).getText(2));
				sign.setText(3, ((Sign) (Sign) complexBlock.data).getText(3));
				sign.update();
			}

		}

		/* Players Loop */
		for (Player p : etc.getServer().getPlayerList()) {
			if ((p.getX() >= this.posX) && (p.getX() < this.posX + this.sizeX)
					&& (p.getY() >= this.posY)
					&& (p.getY() <= this.posY + this.sizeY)
					&& (p.getZ() >= this.posZ)
					&& (p.getZ() < this.posZ + this.sizeZ)) {
				p.teleportTo(p.getX() + dx, p.getY() + dy, p.getZ() + dz,
						p.getRotation(), p.getPitch());
			}
		}

		this.posX += dx;
		this.posY += dy;
		this.posZ += dz;

		this.minX = this.posX;
		this.minY = this.posY;
		this.minZ = this.posZ;
		this.maxX = (this.posX + this.sizeX - 1);
		this.maxY = (this.posY + this.sizeY - 1);
		this.maxZ = (this.posZ + this.sizeZ - 1);

		if ((this.waterLevel == this.sizeY - 1)
				&& (this.newWaterLevel < this.waterLevel)) {
			this.waterLevel = this.newWaterLevel;
		} else if ((this.waterLevel <= -1)
				&& (this.newWaterLevel > this.waterLevel)) {
			this.waterLevel = this.newWaterLevel;
		} else if ((this.waterLevel >= 0) && (this.waterLevel < this.sizeY - 1)) {
			this.waterLevel -= dy;
		}

		this.lastMove = System.currentTimeMillis();
	}

	public void setSpeed(int speed) {
		if (speed < 1)
			this.speed = speed;
		else if (speed > this.type.maxSpeed)
			this.speed = this.type.maxSpeed;
		else
			this.speed = speed;
	}

	public int getSpeed() {
		return this.speed;
	}

	public static class CraftComplexBlock {
		int x;
		int y;
		int z;
		ComplexBlock data;

		CraftComplexBlock(int x, int y, int z, ComplexBlock data) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.data = data;
		}
	}

	public static class DataBlock {
		int x;
		int y;
		int z;
		int data;

		DataBlock(int x, int y, int z, int data) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.data = data;
		}
	}
}

/*
 * Location: C:\Users\David\Downloads\MoveCraft.jar Qualified Name: Craft
 * JD-Core Version: 0.6.0
 */