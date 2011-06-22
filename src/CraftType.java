import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class CraftType {
	String name = "";
	String driveCommand = "pilot";

	int minBlocks = 9;
	int maxBlocks = 500;
	int maxSpeed = 4;

	int flyBlockType = 0;

	String flyBlockName = null;

	int flyBlockPercent = 0;

	boolean canFly = false;
	boolean canNavigate = false;
	boolean canDive = false;
	boolean iceBreaker = false;
	boolean bomber = false;

	String sayOnControl = "You control the craft";
	String sayOnRelease = "You release the craft";

	short[] structureBlocks = null;

	public static ArrayList<CraftType> craftTypes = new ArrayList<CraftType>();

	public CraftType(String name) {
		this.name = name;
	}

	public static CraftType getCraftType(String name) {
		for (CraftType type : craftTypes) {
			if (type.name.equalsIgnoreCase(name)) {
				return type;
			}
		}
		return null;
	}

	public String getCommand() {
		return "/" + this.name.toLowerCase();
	}

	public Boolean canUse(Player player) {
		if (player.canUseCommand(getCommand())) {
			return Boolean.valueOf(true);
		}
		return Boolean.valueOf(false);
	}

	private static void loadDefaultCraftTypes() {
		if (getCraftType("boat") == null)
			craftTypes.add(getDefaultCraftType("boat"));
		if (getCraftType("ship") == null) {
			craftTypes.add(getDefaultCraftType("ship"));
		}

		if (getCraftType("bomber") == null)
			craftTypes.add(getDefaultCraftType("bomber"));
		if (getCraftType("aircraft") == null)
			craftTypes.add(getDefaultCraftType("aircraft"));
		if (getCraftType("airship") == null)
			craftTypes.add(getDefaultCraftType("airship"));
		if (getCraftType("UFO") == null) {
			craftTypes.add(getDefaultCraftType("UFO"));
		}

		if (getCraftType("submarine") == null)
			craftTypes.add(getDefaultCraftType("submarine"));
	}

	private static CraftType getDefaultCraftType(String name) {
		CraftType craftType = new CraftType(name);

		if (name.equalsIgnoreCase("template")) {
			setAttribute(
					craftType,
					"structureBlocks",
					"4,5,17,19,20,35,41,42,43,44,45,46,47,48,49,50,53,57,65,67,68,69,75,76,77,85,87,88,89");
		} else if (name.equalsIgnoreCase("boat")) {
			craftType.driveCommand = "sail";
			craftType.canNavigate = true;
			craftType.minBlocks = 9;
			craftType.maxBlocks = 500;
			craftType.maxSpeed = 4;
			craftType.sayOnControl = "You're on a boat !";
			craftType.sayOnRelease = "You release the helm";
		} else if (name.equalsIgnoreCase("ship")) {
			craftType.driveCommand = "sail";
			craftType.canNavigate = true;
			craftType.minBlocks = 50;
			craftType.maxBlocks = 1000;
			craftType.maxSpeed = 6;
			craftType.sayOnControl = "You're on a ship !";
			craftType.sayOnRelease = "You release the helm";
		} else if (name.equalsIgnoreCase("icebreaker")) {
			craftType.driveCommand = "sail";
			craftType.canNavigate = true;
			craftType.minBlocks = 50;
			craftType.maxBlocks = 1000;
			craftType.maxSpeed = 4;
			craftType.iceBreaker = true;
			craftType.sayOnControl = "Let's break some ice !";
			craftType.sayOnRelease = "You release the helm";
		} else if (name.equalsIgnoreCase("aircraft")) {
			craftType.driveCommand = "pilot";
			craftType.canFly = true;
			craftType.minBlocks = 9;
			craftType.maxBlocks = 1000;
			craftType.maxSpeed = 6;
			craftType.sayOnControl = "You're on an aircraft !";
			craftType.sayOnRelease = "You release the joystick";
		} else if (name.equalsIgnoreCase("bomber")) {
			craftType.driveCommand = "pilot";
			craftType.canFly = true;
			craftType.minBlocks = 20;
			craftType.maxBlocks = 1000;
			craftType.maxSpeed = 4;
			craftType.bomber = true;
			craftType.sayOnControl = "You're on a bomber !";
			craftType.sayOnRelease = "You release the joystick";
		} else if (name.equalsIgnoreCase("airship")) {
			craftType.driveCommand = "pilot";
			craftType.canFly = true;
			craftType.minBlocks = 9;
			craftType.maxBlocks = 1000;
			craftType.maxSpeed = 6;
			craftType.flyBlockName = "wool";
			craftType.flyBlockType = 35;
			craftType.flyBlockPercent = 60;
			craftType.sayOnControl = "You're on an airship !";
			craftType.sayOnRelease = "You release the control panel";
		} else if (name.equalsIgnoreCase("UFO")) {
			craftType.driveCommand = "pilot";
			craftType.canFly = true;
			craftType.minBlocks = 9;
			craftType.maxBlocks = 1000;
			craftType.maxSpeed = 9;
			craftType.flyBlockName = "lightstone";
			craftType.flyBlockType = 89;
			craftType.flyBlockPercent = 4;
			craftType.sayOnControl = "You're on a UFO !";
			craftType.sayOnRelease = "You release the control panel";
		} else if (name.equalsIgnoreCase("USO")) {
			craftType.driveCommand = "pilot";
			craftType.canFly = true;
			craftType.canDive = true;
			craftType.minBlocks = 9;
			craftType.maxBlocks = 1000;
			craftType.maxSpeed = 9;
			craftType.flyBlockName = "lightstone";
			craftType.flyBlockType = 89;
			craftType.flyBlockPercent = 4;
			craftType.sayOnControl = "You're on a USO !";
			craftType.sayOnRelease = "You release the control panel";
		} else if (name.equalsIgnoreCase("submarine")) {
			craftType.driveCommand = "dive";
			craftType.canDive = true;
			craftType.minBlocks = 10;
			craftType.maxBlocks = 1000;
			craftType.maxSpeed = 3;
			craftType.sayOnControl = "You're into a submarine !";
			craftType.sayOnRelease = "You release the helm";
		}

		return craftType;
	}

	private static void setAttribute(CraftType craftType, String attribute,
			String value) {
		if (attribute.equalsIgnoreCase("driveCommand")) {
			craftType.driveCommand = value;
		} else if (attribute.equalsIgnoreCase("minBlocks")) {
			craftType.minBlocks = Integer.parseInt(value);
		} else if (attribute.equalsIgnoreCase("maxBlocks")) {
			craftType.maxBlocks = Integer.parseInt(value);
		} else if (attribute.equalsIgnoreCase("maxSpeed")) {
			craftType.maxSpeed = Integer.parseInt(value);
		} else if (attribute.equalsIgnoreCase("flyBlockName")) {
			craftType.flyBlockName = value;
		} else if (attribute.equalsIgnoreCase("flyBlockType")) {
			craftType.flyBlockType = Integer.parseInt(value);
		} else if (attribute.equalsIgnoreCase("flyBlockPercent")) {
			craftType.flyBlockPercent = Integer.parseInt(value);
		} else if (attribute.equalsIgnoreCase("canNavigate")) {
			craftType.canNavigate = Boolean.parseBoolean(value);
		} else if (attribute.equalsIgnoreCase("canFly")) {
			craftType.canFly = Boolean.parseBoolean(value);
		} else if (attribute.equalsIgnoreCase("canDive")) {
			craftType.canDive = Boolean.parseBoolean(value);
		} else if (attribute.equalsIgnoreCase("bomber")) {
			craftType.bomber = Boolean.parseBoolean(value);
		} else if (attribute.equalsIgnoreCase("sayOnControl")) {
			craftType.sayOnControl = value;
		} else if (attribute.equalsIgnoreCase("sayOnRelease")) {
			craftType.sayOnRelease = value;
		} else if (attribute.equalsIgnoreCase("structureBlocks")) {
			String[] split = value.split(",");
			craftType.structureBlocks = new short[split.length];
			int i = 0;
			for (String blockId : split) {
				craftType.structureBlocks[i] = Short.parseShort(blockId);
				i++;
			}
		}
	}

	public static void saveType(File dir, CraftType craftType, boolean force) {
		File craftFile = new File(dir.getName() + File.separator
				+ craftType.name + ".txt");

		if (!craftFile.exists()) {
			try {
				craftFile.createNewFile();
			} catch (IOException ex) {
				MoveCraft.logger.log(Level.SEVERE, null, ex);
				return;
			}
		} else
			return;
		try {
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(craftFile));

			writeAttribute(writer, "driveCommand", craftType.driveCommand,
					force);
			writeAttribute(writer, "minBlocks", craftType.minBlocks, true);
			writeAttribute(writer, "maxBlocks", craftType.maxBlocks, force);

			if (craftType.structureBlocks != null) {
				String line = "structureBlocks=";
				for (short blockId : craftType.structureBlocks) {
					line = line + blockId + ",";
				}

				writer.write(line.substring(0, line.length() - 1));
				writer.newLine();
			}
			writeAttribute(writer, "maxSpeed", craftType.maxSpeed, force);
			writeAttribute(writer, "flyBlockName", craftType.flyBlockName,
					force);
			writeAttribute(writer, "flyBlockType", craftType.flyBlockType,
					force);
			writeAttribute(writer, "flyBlockPercent",
					craftType.flyBlockPercent, force);
			writeAttribute(writer, "canNavigate", craftType.canNavigate, force);
			writeAttribute(writer, "canFly", craftType.canFly, force);
			writeAttribute(writer, "canDive", craftType.canDive, force);

			writeAttribute(writer, "bomber", craftType.bomber, force);
			writeAttribute(writer, "sayOnControl", craftType.sayOnControl,
					force);
			writeAttribute(writer, "sayOnRelease", craftType.sayOnRelease,
					force);

			writer.close();
		} catch (IOException ex) {
			MoveCraft.logger.log(Level.SEVERE, null, ex);
		}
	}

	public static void saveTypes(File dir) {
		for (CraftType craftType : craftTypes) {
			saveType(dir, craftType, false);
		}

		saveType(dir, getDefaultCraftType("template"), true);
	}

	private static void writeAttribute(BufferedWriter writer, String attribute,
			String value, boolean force) throws IOException {
		if (((value == null) || (value.trim().equals(""))) && (!force))
			return;
		writer.write(attribute + "=" + value);
		writer.newLine();
	}

	private static void writeAttribute(BufferedWriter writer, String attribute,
			int value, boolean force) throws IOException {
		if ((value == 0) && (!force))
			return;
		writer.write(attribute + "=" + value);
		writer.newLine();
	}

	private static void writeAttribute(BufferedWriter writer, String attribute,
			boolean value, boolean force) throws IOException {
		if ((!value) && (!force))
			return;
		writer.write(attribute + "=" + value);
		writer.newLine();
	}

	public static void loadTypes(File dir) {
		File[] craftTypesList = dir.listFiles();
		craftTypes.clear();

		for (File craftFile : craftTypesList) {
			if ((!craftFile.isFile())
					|| (!craftFile.getName().endsWith(".txt")))
				continue;
			String craftName = craftFile.getName().split("\\.")[0];

			if (craftName.equalsIgnoreCase("template")) {
				continue;
			}
			CraftType craftType = new CraftType(craftName);
			try {
				BufferedReader reader = new BufferedReader(new FileReader(
						craftFile));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] split = line.split("=");

					if (split.length >= 2) {
						setAttribute(craftType, split[0], split[1]);
					}
				}
				reader.close();
			} catch (IOException ex) {
				MoveCraft.logger.log(Level.SEVERE, null, ex);
			}

			craftTypes.add(craftType);
		}

		loadDefaultCraftTypes();
	}
}

/*
 * Location: C:\Users\David\Downloads\MoveCraft.jar Qualified Name: CraftType
 * JD-Core Version: 0.6.0
 */