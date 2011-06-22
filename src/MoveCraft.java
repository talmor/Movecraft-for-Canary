import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class MoveCraft extends Plugin {
	PropertiesFile properties;
	static final String pluginName = "MoveCraft";
	static final String version = "0.6.1[Crow Test]";
	static final String updatrUrl = "http://dl.dropbox.com/u/4422249/Minecraft/Plugins/MoveCraft.updatr";
	static final String updatrFileUrl = "http://dl.dropbox.com/u/4422249/Minecraft/Plugins/MoveCraft.jar";
	static final String updatrNotes = "";
	static final DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static Logger logger = Logger.getLogger("Minecraft");
	final MoveCraft.MoveCraftListener listener;

	public MoveCraft() {
		this.listener = new MoveCraft.MoveCraftListener();
	}

	public static void consoleSay(String msg) {
		logger.info("MoveCraft" + " " + msg);
	}

	public void loadProperties() {
		File dir = new File("movecraft");
		if (!dir.exists())
			dir.mkdir();

		CraftType.loadTypes(dir);
		CraftType.saveTypes(dir);
	}

	public void enable() {
		loadProperties();

		consoleSay(version + " plugin enabled");
	}

	public void disable() {
		consoleSay(version + " plugin disabled");
	}

	public void initialize() {
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_CREATED,
				this.listener, this, PluginListener.Priority.CRITICAL);

		etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT,
				this.listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.PLAYER_MOVE,
				this.listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, this.listener,
				this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.ARM_SWING, this.listener,
				this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_RIGHTCLICKED,
				this.listener, this, PluginListener.Priority.MEDIUM);

		etc.getLoader().addListener(PluginLoader.Hook.SIGN_CHANGE,
				this.listener, this, PluginListener.Priority.MEDIUM);

		BlocksInfo.loadBlocksInfo();
	}

	public static String getDateTime() {
		return dateFormat.format(new Date());
	}

	class MoveCraftListener extends PluginListener {
		MoveCraftListener() {
		}

		public void onPlayerMove(Player player, Location from, Location to) {
			Craft craft = Craft.getCraft(player);

			if (craft != null) {
				craft.setSpeed(1);

				if ((craft.isOnBoard) && (!craft.isOnCraft(player, false))) {
					player.sendMessage("§eYou get off the " + craft.name);
					player.sendMessage("§7type /" + craft.name
							+ " remote for remote control");
					craft.isOnBoard = false;
					craft.haveControl = false;
				} else if ((!craft.isOnBoard)
						&& (craft.isOnCraft(player, false))) {
					player.sendMessage("§eWelcome on board");
					craft.isOnBoard = true;
					craft.haveControl = true;
				}
			}
		}

		public boolean onSignChange(Player player, Sign sign) {
			String craftTypeName = sign.getText(0).trim().toLowerCase();

			if (craftTypeName.startsWith("[")) {
				craftTypeName = craftTypeName.substring(1,
						craftTypeName.length() - 1);
			}

			CraftType craftType = CraftType.getCraftType(craftTypeName);

			if (craftType != null) {
				sign.setText(0, "[§1" + craftType.name + "§0]");

				String name = sign.getText(1);

				if (name.length() > 0) {
					sign.setText(1, "§e" + name);
				}
				return false;
			}

			return false;
		}

		public boolean onBlockCreate(Player player, Block blockPlaced,
				Block blockClicked, int itemInHand) {
			World world = player.getWorld();
			Craft playerCraft = Craft.getCraft(player);

			if (blockPlaced != null) {
				Craft craft = Craft.getCraft(world, blockPlaced.getX(),
						blockPlaced.getY(), blockPlaced.getZ());

				if (craft != null) {
					if ((blockPlaced.getType() == 321)
							|| (blockPlaced.getType() == 323)
							|| (blockPlaced.getType() == 324)
							|| (blockPlaced.getType() == 330)) {
						player.sendMessage("§eplease release the "
								+ craft.type.name + " to add this item");
						return true;
					}

					craft.addBlock(blockPlaced);
				}

				if (playerCraft != null) {
					playerCraft.blockPlaced = true;
				}

			}

			if ((blockClicked.getType() == 63)
					|| (blockClicked.getType() == 68)) {
				if (playerCraft == null) {
					Sign sign = (Sign) world.getComplexBlock(
							blockClicked.getX(), blockClicked.getY(),
							blockClicked.getZ());

					if (sign.getText(0).trim().equals(""))
						return false;

					String craftTypeName = sign.getText(0).trim();

					craftTypeName = craftTypeName.replaceAll("§.", "");

					if (craftTypeName.startsWith("[")) {
						craftTypeName = craftTypeName.substring(1,
								craftTypeName.length() - 1);
					}

					CraftType craftType = CraftType.getCraftType(craftTypeName);

					if (craftType != null) {
						String name = sign.getText(1).replaceAll("§.", "");

						if (name.trim().equals("")) {
							name = null;
						}
						String[] groups = (sign.getText(2) + " " + sign
								.getText(3)).replace(",", "").replace(";", "")
								.split("[ ]");

						if (!checkPermission(player, groups)) {
							player.sendMessage("§cyou are not allowed to take control of that "
									+ craftType.name + " !");
							return true;
						}

						int x = blockClicked.getX();
						int y = blockClicked.getY();
						int z = blockClicked.getZ();

						int direction = world.getBlockData(x, y, z);

						x += (direction == 5 ? -1 : direction == 4 ? 1 : 0);
						z += (direction == 3 ? -1 : direction == 2 ? 1 : 0);

						createCraft(player, craftType, x, y, z, name);

						return true;
					}

					return false;
				}

				releaseCraft(player, playerCraft);
			}

			return false;
		}

		public boolean checkPermission(Player player, String[] rights) {
			for (String right : rights) {
				if (right.trim().equalsIgnoreCase("")) {
					continue;
				}
				if (right.equalsIgnoreCase("public")) {
					return true;
				}

				if (player.equals(etc.getServer().matchPlayer(right))) {
					return true;
				}

				if (!right.startsWith("g:"))
					continue;
				if (player.isInGroup(right.substring(2))) {
					return true;
				}

			}

			return false;
		}

		public void onDisconnect(Player player) {
			Craft craft = Craft.getCraft(player);

			if (craft != null)
				Craft.removeCraft(craft);
		}

		public void onArmSwing(Player player) {
			Craft craft = Craft.getCraft(player);

			if (craft != null) {
				if (craft.blockPlaced) {
					craft.blockPlaced = false;
					return;
				}

				if (craft.blockCount <= 0) {
					releaseCraft(player, craft);
					return;
				}

				int item = player.getItemInHand();

				if ((!craft.haveControl) || (item == 256) || (item == 257)
						|| (item == 258) || (item == 269) || (item == 270)
						|| (item == 271) || (item == 273) || (item == 274)
						|| (item == 275) || (item == 277) || (item == 278)
						|| (item == 279) || (item == 284) || (item == 285)
						|| (item == 286) || ((item >= 290) && (item <= 294))
						|| (item == 336)) {
					return;
				}

				if (System.currentTimeMillis() - craft.lastMove < 200.0D)
					return;

				craft.setSpeed(craft.speed
						- (int) ((System.currentTimeMillis() - craft.lastMove) / 500L));

				if (craft.speed <= 0)
					craft.speed = 1;

				float rotation = 3.141593F * player.getRotation() / 180.0F;

				float nx = -(float) Math.sin(rotation);
				float nz = (float) Math.cos(rotation);

				int dx = (Math.abs(nx) >= 0.5D ? 1 : 0) * (int) Math.signum(nx);
				int dz = (Math.abs(nz) > 0.5D ? 1 : 0) * (int) Math.signum(nz);

				int dy = 0;

				if ((craft.type.canFly) || (craft.type.canDive)) {
					float p = player.getPitch();

					dy = -(Math.abs(player.getPitch()) >= 25.0F ? 1 : 0)
							* (int) Math.signum(p);

					if (Math.abs(player.getPitch()) >= 75.0F) {
						dx = 0;
						dz = 0;
					}

				}

				if ((craft.type.canDive) && (!craft.type.canFly)
						&& (craft.waterLevel <= 0) && (dy > 0)) {
					dy = 0;
				}

				while (!craft.canMove(dx, dy, dz)) {
					if (craft.speed == 1) {
						if ((craft.type.canFly) && (dy >= 0)) {
							dx = 0;
							dz = 0;
							dy = 1;
							if (craft.canMove(dx, dy, dz))
								break;
						}
						player.sendMessage("§cthe " + craft.name
								+ " won't go any further");
						return;
					}

					craft.setSpeed(craft.speed - 1);
				}

				craft.move(dx, dy, dz);

				craft.setSpeed(craft.speed + 1);
			}
		}

		private void createCraft(Player player, CraftType craftType, int x,
				int y, int z, String name) {
			Craft craft = Craft.getCraft(player);

			if (craft != null) {
				releaseCraft(player, craft);
			}

			craft = new Craft(player.getWorld(), craftType, player, name);

			if (!CraftBuilder.detect(craft, x, y, z)) {
				return;
			}

			Craft.addCraft(craft);

			player.sendMessage("§7Swing your arm where you want to go (no tools)");

			onPlayerMove(player, player.getLocation(), player.getLocation());
		}

		private void releaseCraft(Player player, Craft craft) {
			if (craft != null) {
				player.sendMessage("§e" + craft.type.sayOnRelease);
				Craft.removeCraft(craft);
			} else {
				player.sendMessage("§eYou don't have anything to release");
			}
		}

		private boolean processCommand(CraftType craftType, Player player,
				String[] split) {
			Craft craft = Craft.getCraft(player);

			if (split.length >= 2) {
				if (split[1].equalsIgnoreCase(craftType.driveCommand)) {
					createCraft(player, craftType,
							(int) Math.floor(player.getX()),
							(int) Math.floor(player.getY() - 1.0D),
							(int) Math.floor(player.getZ()), null);

					return true;
				}
				if (split[1].equalsIgnoreCase("setspeed")) {
					if (craft == null) {
						player.sendMessage("§eYou don't have any "
								+ craftType.name);
						return true;
					}

					int speed = Math.abs(Integer.parseInt(split[2]));

					if ((speed < 1) || (speed > craftType.maxSpeed)) {
						player.sendMessage("§cAllowed speed between 1 and "
								+ craftType.maxSpeed);
						return true;
					}

					craft.setSpeed(speed);
					player.sendMessage("§e" + craft.name + "'s speed set to "
							+ craft.speed);

					return true;
				}
				if (split[1].equalsIgnoreCase("setname")) {
					if (craft == null) {
						player.sendMessage("§eYou don't have any "
								+ craftType.name);
						return true;
					}

					craft.name = split[2];
					player.sendMessage("§e" + craft.name + "'s name set to "
							+ craft.name);
					return true;
				}
				if (split[1].equalsIgnoreCase("size")) {
					if (craft == null) {
						player.sendMessage("§eYou don't have any "
								+ craftType.name);
						return true;
					}

					player.sendMessage("§eThe " + craft.name
							+ " is built with " + craft.blockCount + " blocks");
					return true;
				}
				if (split[1].equalsIgnoreCase("remote")) {
					if (craft == null) {
						player.sendMessage("§eYou don't have any "
								+ craftType.name);
						return true;
					}

					if (craft.isOnCraft(player, true)) {
						player.sendMessage("§eYou are on the "
								+ craftType.name
								+ ", remote control not possible");
					} else {
						if (craft.haveControl)
							player.sendMessage("§eYou switch off the remote controller");
						else {
							player.sendMessage("§eYou switch on the remote controller");
						}

						craft.haveControl = (!craft.haveControl);
					}

					return true;
				}
				if (split[1].equalsIgnoreCase("release")) {
					releaseCraft(player, craft);
					return true;
				}

				if (split[1].equalsIgnoreCase("info")) {
					player.sendMessage("§a" + craftType.name);
					player.sendMessage("§emin size : " + craftType.minBlocks);
					player.sendMessage("§emax size : " + craftType.maxBlocks);
					player.sendMessage("§espeed : " + craftType.maxSpeed);

					if (craftType.canFly) {
						player.sendMessage("§ethe " + craftType.name
								+ " can fly");
					}
					if (craftType.canNavigate) {
						player.sendMessage("§ethe " + craftType.name
								+ " can navigate on both water and lava");
					}
					if (craftType.canDive) {
						player.sendMessage("§ethe " + craftType.name
								+ " can dive");
					}
					if (craftType.flyBlockType != 0) {
						player.sendMessage("§crequirement : "
								+ craftType.flyBlockPercent + "%" + " of "
								+ craftType.flyBlockName);
					}

					return true;
				}

			}

			player.sendMessage("§aMoveCraft v0.6 commands :");
			player.sendMessage("§e/" + craftType.name + " "
					+ craftType.driveCommand + " : §f" + " "
					+ craftType.driveCommand + " the " + craftType.name);
			player.sendMessage("§e/" + craftType.name + " "
					+ "release : §frelease the " + craftType.name);
			player.sendMessage("§e/" + craftType.name + " "
					+ "remote : §fremote control of the " + craftType.name);
			player.sendMessage("§e/" + craftType.name + " "
					+ "size : §fthe size of the " + craftType.name
					+ " in block");
			player.sendMessage("§e/" + craftType.name + " "
					+ "setname : §fset the " + craftType.name + "'s name");
			player.sendMessage("§e/" + craftType.name + " "
					+ "info : §fdisplays informations about the "
					+ craftType.name);

			return true;
		}

		public boolean onCommand(Player player, String[] split) {
			if ((split[0].equalsIgnoreCase("/reload"))
					&& (player.canUseCommand("/reload"))) {
				MoveCraft.this.loadProperties();
				return false;
			}
			if ((split[0].equalsIgnoreCase("/movecraft"))
					&& (player.canUseCommand("/movecraft"))) {
				if (split.length >= 2) {
					if (split[1].equalsIgnoreCase("types")) {
						for (CraftType craftType : CraftType.craftTypes) {
							if (craftType.canUse(player).booleanValue()) {
								player.sendMessage("§e " + craftType.name
										+ " :§f " + craftType.minBlocks + "-"
										+ craftType.maxBlocks + " blocks"
										+ " speed : " + craftType.maxSpeed);
							}
						}
						return true;
					}

					if (split[1].equalsIgnoreCase("list")) {
						if (Craft.craftList.isEmpty()) {
							player.sendMessage("§7no player controlled craft");
							return true;
						}

						for (Craft craft : Craft.craftList) {
							player.sendMessage("§e" + craft.name
									+ " controlled by "
									+ craft.player.getName() + " : "
									+ craft.blockCount + " blocks");
						}

						return true;
					}

					if (split[1].equalsIgnoreCase("reload")) {
						MoveCraft.this.loadProperties();
						player.sendMessage("§econfiguration reloaded");

						return true;
					}
				}

				player.sendMessage("§aMoveCraft v" + version + " commands :");
				player.sendMessage("§e/movecraft types  : §flist the types of craft available");
				player.sendMessage("§e/movecraft list : §flist the current player controled craft");
				player.sendMessage("§e/movecraft reload : §freload config files");
				player.sendMessage("§e/[craft type]  : §fcommands specific to the craft type");

				return true;
			}

			String craftName = split[0].substring(1);

			CraftType craftType = CraftType.getCraftType(craftName);

			if (craftType != null) {
				if (craftType.canUse(player).booleanValue()) {
					return processCommand(craftType, player, split);
				}
				player.sendMessage("§cyou are not allowed to use this type of craft");
				return true;
			}

			return false;
		}
	}
}

/*
 * Location: C:\Users\David\Downloads\MoveCraft.jar Qualified Name: MoveCraft
 * JD-Core Version: 0.6.0
 */