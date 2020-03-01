# g-file Format Structure
## File structure
* File header
* 3D Data
  * Vertices
    * Positions: List of Vector3
    * Normals: List of Vector3
    * UVs (if mesh is textured): List of Vector2
  * Triangles indices
* Round edge shader data (used by shader for outlines on brick)
  * Mapping for each **index** (offsets to the data)
* Average normals (used by shader)
  * Mapping for each **index** (index
* Flex bone weights
  * Mapping for each **vertex**
* Stud culling information


Size | Data type | Description | Example (3024.g)
:------- | :---: | :---: | :--- 
 4 bytes     | Char[4] | 4CC (ASCII = '10GB') | 0x31 0x30 0x47 0x42
 4 bytes     | Int32   | vertex count | 85 
 4 bytes     | Int32   | triangle / face index count | 192
 4 bytes     | Int32   | options / mesh type (58 or 59 or 62 or 63) | Hex: 0x3A Dec: 58
 3 * 4 bytes | float   | position 1 (X, Y, Z) | -0.4,  0.32, 0.4
 3 * 4 bytes | float   | position 2, 3,  ... | 0.4, 0.32, 0.4, ... , -6.477
 3 * 4 bytes | float   | normal 1 (X, Y, Z) | 0.0, 1.0, 0.0
 3 * 4 bytes | float   | normal 2, 3, ... |  0.0, 1.0, 0.0, ... , 0.0
 2 * 4 bytes | float   | texture UV 1 (X, Y) (if options 59 or 63) | n.a.
 2 * 4 bytes | float   | texture UV 2, 3, ... | n.a.
 3 * 4 bytes | Int32   | triangle / face 1 (point a, b, c) - 3 points make 1 triangle | 0, 1, 2
 3 * 4 bytes | Int32   | triangle / face 2,  3, ... | 1, 3, 2, ... , 84, 73, 72 (until offset B04)
 4 bytes     | Int32   | Num 1 ?? (if options 62 or 63) | n.a ?
 4 bytes     | Int32   | Num 2 ?? (if options 62 or 63) | n.a ?
 4 bytes     | Int32   | bonelength ?? | n.a. ?
 4 bytes     | Int32   | Round edge shader total value count | 764 ?
 2 * 4 bytes | float   | Round edge shader data 2D vector 1 | -115.5, 0 ?
 2 * 4 bytes | float   | ... | -115.5, 0
 4 bytes     | Int32   | Round edge shader offset 1 | 0, 12, 24 ?
 4 bytes     | Int32   | ... | 36, 48, ...
 3 * 4 bytes | float   | Average normal "header" (X, Y, Z) | 83, 0.0, 0.0 (start offset: 1A00)
 3 * 4 bytes | float   | Average normal 1 | -0.57735, 0.57735, 0.57735
 3 * 4 bytes | float   | Average normal 2, 3, ... | 0.57735, 0.57735, 0.57735, ... 
 4 bytes     | Int32   | Bone Id. References to the primitive xml FlexBone node. | n.a.
 4 bytes     | float   | Weight | n.a.
 
### File Header
The file starts with the following four characters: 10GB.

Then the total **vertex count** and **index count** (3 per triangle).

The last value of the header is the "type" of mesh.

Here are the possible mesh types:
* Standard mesh (Hex: 0x3A Dec: 58)
* Textured mesh (Hex: 0x3B Dec: 59)
* Flexible mesh (Hex: 0x3E Dec: 62)
* Flexible textured mesh (Hex: 0x3F Dec: 63)

### 3D Data
The 3D data section contains the vertices positions and normals and texture coordinates if the mesh is textured.

**Positions**: A list of Vector3. Total size = [**Vertex Count**] \* 3 \* 4 bytes

**Normals**: A list of Vector3. Total size = [**Vertex Count**] \* 3 \* 4 bytes

**UVs**: A list of Vector2. Total size = [**Vertex Count**] \* 2 \* 4 bytes

**Triangles indices**: A list of indexes that makes up the triangles.

### Round edge shader data
This data is used by the shader to draw outlines on the bricks.
This data is a bit complicated and I will document it more thoroughly in a separate document.

Basically for each mesh index there is a set of six 2D vectors.
Those vectors are 2D coordinates for a square texture that as a line at the bottom.
This allows 6 possibles outlines per triangle. 

This data is also "packed" into an ARGB bitmap at runtime to pass to a shader, so the data is padded/aligned.
The bitmap is 64 pixel wide for a total of 256 values (64 pixel x 4 channels (A R G B)).
There is 12 values per index (six 2D coords = 6 x 2) so there is 21 index data per bitmap row.
Since this only makes up 252 bytes (21 * 12), the last index data per row is padded to 16 bytes (with mostly random data as I've seen).

The section is structured like this:
* Total value count (relative to the packed bitmap data, so 4 per pixel)
* Shader data (list of 2D vectors)
* A list of offsets that links each indexes to the shader data. 
**Note** that this is a value offset and not a byte offset. 
So it jumps by increments of 12 or 16 to account for padding.

### Average normals
This data is used by the shader but I don't know for what purpose.
I achieve similar values by calculating the average of the (distinct) faces normals connected to the vertice.
There is one value for each mesh index plus a "header" value that is always X:83 Y:0 Z:0

### Flex bone weights


### Stud culling information


## Data structures
### Vector2
Size | Data type | Description 
:------- | :---: | :--- 
 4 bytes | float | X
 4 bytes | float | Y
 
### Vector3
Size | Data type | Description 
:------- | :---: | :--- 
 4 bytes | float | X
 4 bytes | float | Y
 4 bytes | float | Z
 
### File header
Size | Data type | Description 
:------- | :---: | :--- 
 4 bytes | Char[4] | Header bytes (0x31 0x30 0x47 0x42) ASCII = '10GB'
 4 bytes | Int32 | **Vertex Count**
 4 bytes | Int32 | **Index Count**
 4 bytes | Int32 | **Mesh Type**
 
**Note:** The number of triangles is equals to **Index Count** / 3
 
### Bone Weight
Size | Data type | Description 
:------- | :---: | :--- 
 4 bytes | Int32 | Bone Id. References to the primitive xml FlexBone node.
 4 bytes | float | Weight
 