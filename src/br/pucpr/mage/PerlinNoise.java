package br.pucpr.mage;

import java.util.Random;

public final class PerlinNoise  {
    static public float[][] generateHeightMap(int width, int height, float scale, float[] offset) {
        if (permutation == null) {
            refreshSeed();
        }

        var noiseMap = new float[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float samplePosX = (float) x * scale + offset[0];
                float samplePosY = (float) y * scale + offset[1];

                noiseMap[x][y] = noise(samplePosX, samplePosY);
            }
        }

        return noiseMap;
    };

    static public float noise(float x, float y) {
        float z = 1;

        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        int Z = (int) Math.floor(z) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);
        z -= Math.floor(z);

        float u = fade(x);
        float v = fade(y);
        float w = fade(z);

        int A = p[X] + Y;
        int AA = p[A] + Z;
        int AB = p[A + 1] + Z;
        int B = p[X + 1] + Y;
        int BA = p[B] + Z;
        int BB = p[B + 1] + Z;

        return lerp(
            w,
            lerp(
                v,
                lerp(
                    u,
                    grad(p[AA], x, y, z),
                    grad(p[BA], x-1, y, z)
                ),
                lerp(
                    u,
                    grad(p[AB], x, y - 1, z),
                    grad(p[BB], x-1, y-1, z)
                )
            ),
            lerp(
                v,
                lerp(
                    u,
                    grad(p[AA + 1], x, y, z-1),
                    grad(p[BA + 1], x - 1, y, z - 1)
                ),
                lerp(
                    u,
                    grad(p[AB + 1], x, y - 1, z - 1),
                    grad(p[BB + 1], x - 1, y - 1, z - 1)
                )
            )
        );
    }

    static float fade(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    static float lerp(float t, float a, float b) {
        return a + t * (b - a);
    }

    static float grad(int hash, float x, float y, float z) {
        int h = hash & 15;

        float u = h < 8 ? x : y;
        float v = h < 4 ? y : h == 12 || h == 14 ? x : z;

        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    static int[] p = new int[512], permutation;

    public static void refreshSeed(int seed) {
        // REFRESH PERMUTATION
        Random r = new Random(seed);

        permutation = new int[256];
        for(int i = 0; i < permutation.length; i++)
            permutation[i] = r.nextInt(256);

        for (int i = 0; i < 256; i++)
            p[256 + i] = p[i] = r.nextInt(256);
    }

    public static void refreshSeed() {
        int seed = new Random().nextInt();
        refreshSeed(seed);
    }
}
