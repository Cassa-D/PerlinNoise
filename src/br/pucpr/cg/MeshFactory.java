package br.pucpr.cg;

import br.pucpr.mage.Mesh;
import br.pucpr.mage.MeshBuilder;
import br.pucpr.mage.PerlinNoise;
import br.pucpr.mage.Shader;
import org.joml.Vector3f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class MeshFactory {
    private static int linearIndex(int x, int z, int width) {
        return x + z * width;
    }

    public static void refreshSeed() {
        PerlinNoise.refreshSeed();
    }
    public static Mesh createTerrain(Shader shader, int width, int height, float size, float scale) {
        try {
            var depth = height;

            var heightMap = PerlinNoise.generateHeightMap(width, depth, scale, new float[] { 0, 0 });

            var offset = new Vector3f(width / 2.0f, 0.0f, depth / 2.0f);
            var positions = new ArrayList<Vector3f>();
            var normals = new ArrayList<Vector3f>();

            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++) {
                    var tone = heightMap[x][z] * 255;
                    var h = tone * size;

                    positions.add(new Vector3f(x, h, z).sub(offset));
                    normals.add(new Vector3f());
                }
            }

            var indices = new ArrayList<Integer>();

            for (int z = 0; z < depth - 1; z++) {
                for (int x = 0; x < width - 1; x++) {
                    var zero = linearIndex(x, z, width);
                    var one = linearIndex(x + 1, z, width);
                    var two = linearIndex(x, z + 1, width);
                    var three = linearIndex(x + 1, z + 1, width);

                    indices.add(zero);
                    indices.add(three);
                    indices.add(one);

                    indices.add(zero);
                    indices.add(two);
                    indices.add(three);
                }
            }

            for (int i = 0; i < indices.size(); i += 3) {
                var i1 = indices.get(i);
                var i2 = indices.get(i+1);
                var i3 = indices.get(i+2);

                var v1 = positions.get(i1);
                var v2 = positions.get(i2);
                var v3 = positions.get(i3);

                var side1 = new Vector3f(v2).sub(v1);
                var side2 = new Vector3f(v3).sub(v1);

                var normal = side1.cross(side2);

                normals.get(i1).add(normal);
                normals.get(i2).add(normal);
                normals.get(i3).add(normal);
            }

            normals.forEach(Vector3f::normalize);

            return new MeshBuilder(shader).addVector3fAttribute("aPosition", positions).addVector3fAttribute("aNormal", normals).setIndexBuffer(indices).create();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
