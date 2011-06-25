class BlockLoc {
    int x;
    int y;
    int z;

    public BlockLoc(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean equals(Object object) {
        if (!(object instanceof BlockLoc))
            return false;

        BlockLoc block = (BlockLoc) object;
        return (this.x == block.x) && (this.y == block.y) && (this.z == block.z);
    }

    public int hashCode() {
        return Integer.valueOf(this.x).hashCode() >> 13 ^ Integer.valueOf(this.y).hashCode() >> 7
                ^ Integer.valueOf(this.z).hashCode();
    }
}
