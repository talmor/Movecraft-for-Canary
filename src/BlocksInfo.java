public class BlocksInfo {
	// TODO: Better a hashmap
	public static BlocksInfo.BlockInfo[] blocks = new BlocksInfo.BlockInfo[100];

	public static void loadBlocksInfo() {
		blocks[0] = new BlocksInfo.BlockInfo("air", false, false, false);
		blocks[1] = new BlocksInfo.BlockInfo("smoothstone", false, false, false);
		blocks[2] = new BlocksInfo.BlockInfo("grass", false, false, false);
		blocks[3] = new BlocksInfo.BlockInfo("dirt", false, false, false);
		blocks[4] = new BlocksInfo.BlockInfo("cobblestone", false, false, false);
		blocks[5] = new BlocksInfo.BlockInfo("wood", true, false, false);
		blocks[6] = new BlocksInfo.BlockInfo("sapling", true, false, false);
		blocks[7] = new BlocksInfo.BlockInfo("adminium", false, false, false);
		blocks[8] = new BlocksInfo.BlockInfo("water", true, false, false);
		blocks[9] = new BlocksInfo.BlockInfo("water", true, false, false);
		blocks[10] = new BlocksInfo.BlockInfo("lava", true, false, false);
		blocks[11] = new BlocksInfo.BlockInfo("lava", true, false, false);
		blocks[12] = new BlocksInfo.BlockInfo("sand", false, false, false);
		blocks[13] = new BlocksInfo.BlockInfo("gravel", false, false, false);
		blocks[14] = new BlocksInfo.BlockInfo("gold ore", false, false, false);
		blocks[15] = new BlocksInfo.BlockInfo("iron ore", false, false, false);
		blocks[16] = new BlocksInfo.BlockInfo("charcoal", false, false, false);
		blocks[17] = new BlocksInfo.BlockInfo("trunc", false, false, false);
		blocks[18] = new BlocksInfo.BlockInfo("foliage", false, false, false);
		blocks[19] = new BlocksInfo.BlockInfo("sponge", false, false, false);
		blocks[20] = new BlocksInfo.BlockInfo("glass", false, false, false);
		blocks[23] = new BlocksInfo.BlockInfo("dispenser", true, false, true);
		blocks[24] = new BlocksInfo.BlockInfo("sandstone", false, false, false);
		blocks[35] = new BlocksInfo.BlockInfo("wool", true, false, false);
		blocks[37] = new BlocksInfo.BlockInfo("yellow flower", false, true,
				false);
		blocks[38] = new BlocksInfo.BlockInfo("red flower", false, true, false);
		blocks[39] = new BlocksInfo.BlockInfo("brown mushroom", false, true,
				false);
		blocks[40] = new BlocksInfo.BlockInfo("red mushroom", false, true,
				false);
		blocks[41] = new BlocksInfo.BlockInfo("gold block", false, false, false);
		blocks[42] = new BlocksInfo.BlockInfo("iron block", false, false, false);
		blocks[43] = new BlocksInfo.BlockInfo("double steps", false, false,
				false);
		blocks[44] = new BlocksInfo.BlockInfo("step", true, false, false);
		blocks[45] = new BlocksInfo.BlockInfo("brick", false, false, false);
		blocks[46] = new BlocksInfo.BlockInfo("TNT", false, false, false);
		blocks[47] = new BlocksInfo.BlockInfo("library", false, false, false);
		blocks[48] = new BlocksInfo.BlockInfo("mossy cobblestone", false,
				false, false);
		blocks[49] = new BlocksInfo.BlockInfo("obsidian", false, false, false);
		blocks[50] = new BlocksInfo.BlockInfo("torch", true, true, false);
		blocks[51] = new BlocksInfo.BlockInfo("fire", true, true, false);
		blocks[52] = new BlocksInfo.BlockInfo("spawner", true, false, false);
		blocks[53] = new BlocksInfo.BlockInfo("wooden stair", true, false,
				false);
		blocks[54] = new BlocksInfo.BlockInfo("chest", true, false, true);
		blocks[55] = new BlocksInfo.BlockInfo("redstone dust", true, true,
				false);
		blocks[56] = new BlocksInfo.BlockInfo("diamond", false, false, false);
		blocks[57] = new BlocksInfo.BlockInfo("diamond block", false, false,
				false);
		blocks[58] = new BlocksInfo.BlockInfo("workbench", false, false, false);
		blocks[59] = new BlocksInfo.BlockInfo("seed", true, true, false);
		blocks[60] = new BlocksInfo.BlockInfo("field", true, false, false);
		blocks[61] = new BlocksInfo.BlockInfo("furnace", true, false, false);
		blocks[62] = new BlocksInfo.BlockInfo("furnace", true, false, false);
		blocks[63] = new BlocksInfo.BlockInfo("sign", true, true, true);
		blocks[64] = new BlocksInfo.BlockInfo("wooden door", true, true, false);
		blocks[65] = new BlocksInfo.BlockInfo("ladder", true, true, false);
		blocks[66] = new BlocksInfo.BlockInfo("rail", true, true, false);
		blocks[67] = new BlocksInfo.BlockInfo("cobblestone stair", true, false,
				false);
		blocks[68] = new BlocksInfo.BlockInfo("sign", true, true, true);
		blocks[69] = new BlocksInfo.BlockInfo("lever", true, true, false);
		blocks[70] = new BlocksInfo.BlockInfo("pressure plate", true, true,
				false);
		blocks[71] = new BlocksInfo.BlockInfo("steel door", true, true, false);
		blocks[72] = new BlocksInfo.BlockInfo("wooden pressure plate", true,
				true, false);
		blocks[73] = new BlocksInfo.BlockInfo("redstone ore", false, false,
				false);
		blocks[74] = new BlocksInfo.BlockInfo("redstone ore", false, false,
				false);
		blocks[75] = new BlocksInfo.BlockInfo("redstone torch", true, true,
				false);
		blocks[76] = new BlocksInfo.BlockInfo("redstone torch", true, true,
				false);
		blocks[77] = new BlocksInfo.BlockInfo("stone button", true, true, false);
		blocks[78] = new BlocksInfo.BlockInfo("snow", false, true, false);
		blocks[79] = new BlocksInfo.BlockInfo("ice", false, false, false);
		blocks[80] = new BlocksInfo.BlockInfo("snow block", false, false, false);
		blocks[81] = new BlocksInfo.BlockInfo("cacti", false, true, false);
		blocks[82] = new BlocksInfo.BlockInfo("clay", false, false, false);
		blocks[83] = new BlocksInfo.BlockInfo("reed", true, true, false);
		blocks[84] = new BlocksInfo.BlockInfo("jukebox", true, false, false);
		blocks[85] = new BlocksInfo.BlockInfo("fence", true, false, false);
		blocks[86] = new BlocksInfo.BlockInfo("pumpkin", true, false, false);
		blocks[87] = new BlocksInfo.BlockInfo("hellstone", false, false, false);
		blocks[88] = new BlocksInfo.BlockInfo("mud", false, false, false);
		blocks[89] = new BlocksInfo.BlockInfo("lightstone", false, false, false);
		blocks[90] = new BlocksInfo.BlockInfo("portal", true, true, false);
		blocks[91] = new BlocksInfo.BlockInfo("pumpkin", true, false, false);
	}

	public static String getName(int blockId) {
		return blocks[blockId].name;
	}

	public static boolean isDataBlock(int blockId) {
		return (blockId != -1) && (blocks[blockId].isDataBlock);
	}

	public static boolean isComplexBlock(int blockId) {
		return (blockId != -1) && (blocks[blockId].isComplexBlock);
	}

	public static boolean needsSupport(int blockId) {
		return (blockId != -1) && (blocks[blockId].needSupport);
	}

	private static class BlockInfo {
		String name;
		boolean isDataBlock;
		boolean needSupport;
		boolean isComplexBlock;

		public BlockInfo(String name, boolean isDataBlock, boolean needSupport,
				boolean isComplexBlock) {
			this.name = name;
			this.isDataBlock = isDataBlock;
			this.needSupport = needSupport;
			this.isComplexBlock = isComplexBlock;
		}
	}
}

/*
 * Location: C:\Users\David\Downloads\MoveCraft.jar Qualified Name: BlocksInfo
 * JD-Core Version: 0.6.0
 */