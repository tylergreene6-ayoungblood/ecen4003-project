/*
 * BaseKernel.java
 * Authors: Tyler Greene, Akira Youngblood
 * Built for ECEN4003 Concurrent Programming
 */

/**
 * A dynamic, with floating point data. Kernels are small rectangular
 * matrices describing an image filter. The dimensions should be mxn, where
 * both m and n are odd numbers: m,n = 2k+1 for all k.
 * BaseKernel describes an image kernel as a function of space. By specifying
 * a point, BaseKernel can generate a static kernel (Kernel)
 */
public class BaseKernel extends Kernel {
    public String name;
    /**
     * Creates a BaseKernel from a JSONKernel.
     * @param jKernel The JSONKernel to copy from
     */
    public BaseKernel(JSONKernel jKernel) {
        this.width = jKernel.width;
        this.height = jKernel.height;
        kernel = new float[width][height];
        this.name = jKernel.name;
        for (int i = 0; i < this.width; ++i)
            for (int j = 0; j < this.height; ++j)
                kernel[i][j] = jKernel.kernel[i][j] * jKernel.coeff;
    }
    /**
     * Creates a BaseKernel from a path to a kernel in JSON format.
     * @param jKernelPath The path of the JSON file to read
     */
    public BaseKernel(String jKernelPath) {
        this(KOps.kernelFromJSONPath(jKernelPath));
    }
    /**
     * Return a scaled kernel as a Kernel, trimming dimensions if necessary.
     * @param point An xy coordinate, normalized to [0,1]
     * @return A modulated and trimmed kernel
     */
    public Kernel getModulatedKernel(float [] point) {
        float scale = 1.0f/(point[0]*80 + point[1]*40);
        //System.out.println(scale);
        float [][] tempKernel = new float[width][height];
        float thresh = 1/(2048.0f);
        // Scale kernel
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                tempKernel[i][j] = kernel[i][j] * scale;
            }
        }
        int cwidth = width;
        int cheight = height;
        boolean [] edgeCheck = KOps.edgeCheck(tempKernel, thresh);
        boolean trimTB = edgeCheck[0] && edgeCheck[2];
        while (trimTB && cheight > 1) {
            cheight -= 2;
            float [][] newTempKernel = new float[cwidth][cheight];
            for (int i = 0; i < cwidth; ++i)
                for (int j = 0; j < cheight; ++j)
                    newTempKernel[i][j] = tempKernel[i][j+1];
            tempKernel = newTempKernel;
            edgeCheck = KOps.edgeCheck(tempKernel, thresh);
            trimTB = edgeCheck[0] && edgeCheck[2];
        }
        boolean trimLR = edgeCheck[1] && edgeCheck[3];
        while (trimLR && cwidth > 1) {
            cwidth -= 2;
            float [][] newTempKernel = new float[cwidth][cheight];
            for (int i = 0; i < cwidth; ++i)
                for (int j = 0; j < cheight; ++j)
                    newTempKernel[i][j] = tempKernel[i+1][j];
            tempKernel = newTempKernel;
            edgeCheck = KOps.edgeCheck(tempKernel, thresh);
            trimLR = edgeCheck[1] && edgeCheck[3];
        }
        Kernel newKernel = new Kernel(cwidth,cheight);
        newKernel.setKernel(tempKernel);
        newKernel.normalize();
        if (newKernel.getWidth() != width || newKernel.getHeight() != height) {
            //System.out.printf("Kernel trimmed. Original size: %dx%d; new size: %dx%d\n",width,height,newKernel.getWidth(),newKernel.getHeight());
        }
        return newKernel;
    }
}
