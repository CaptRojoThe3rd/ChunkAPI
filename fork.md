
EHL (Extended Height Limit) fork
-----

This fork extends the build limit to 512 blocks.

### Why is this a fork and not a standalone mod?

I wanted a mod to double the height limit that would be compatible with EndlessIDs. I originally
tried making a fork of EndlessIDs, but found it would be much easier (and not completely
impossible) to instead fork ChunkAPI to make the changes I wanted.

### Why 512 blocks?

This is the limit I (CaptRojoThe3rd) encountered when modifying ChunkAPI. This is because Minecraft
uses a 32-bit integer to represent which subchunks are modified when sending changes over the
network (with one bit representing each subchunk). ChunkAPI maintains this implementation, and makes
it a part of the API, meaning mods like EndlessIDs rely on it being an integer.

### Compatibility

Currently known incompatibilies:
* ArchaicFix's Phosphor backport (mixin conflicts)
