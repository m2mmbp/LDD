package bublible.shaders;

import bublible.Shaders;
import static bublible.Utils.rndRng;
import org.sunflow.SunflowAPI;
import org.sunflow.core.ParameterList;
import org.sunflow.core.Ray;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
import org.sunflow.image.Color;
import org.sunflow.math.OrthoNormalBasis;
import org.sunflow.math.Vector3;

public class PlasticShader implements Shader {

    public Color c;
    //public float refl = 0.1f;
    public float refl = 0.04f;
    private final int cislo = 40;
    private final int[] deleno = {cislo, cislo * -1};

    public PlasticShader() {
    }

    public boolean update(ParameterList pl, SunflowAPI api) {
        c = pl.getColor("color", c);
        return true;
    }

    public Color getMaterialColor() {
        return c;
    }

    public float getReflectionValue() {
        return refl;
    }

    public Color getDiffuse(ShadingState state, Color d) {
        return Shaders.getMixture(state, d, "plastic");
    }

    public Color getRadiance(ShadingState state) {
        // make sure we are on the right side of the material
        state.faceforward();
        // direct lighting
        state.initLightSamples();
        state.initCausticSamples();
        Color d = getDiffuse(state, c);
        Color lr = state.diffuse(d);
        if (!state.includeSpecular()) {
            return lr;
        }
        float cos = state.getCosND();
        float dn = 2 * cos;
        Vector3 refDir = new Vector3();
        refDir.x = (dn * state.getNormal().x) + state.getRay().getDirection().x + (float) Math.random() / deleno[rndRng(0, 1)];
        refDir.y = (dn * state.getNormal().y) + state.getRay().getDirection().y + (float) Math.random() / deleno[rndRng(0, 1)];
        refDir.z = (dn * state.getNormal().z) + state.getRay().getDirection().z + (float) Math.random() / deleno[rndRng(0, 1)];
        Ray refRay = new Ray(state.getPoint(), refDir);
        // compute Fresnel term
        cos = 1 - cos;
        float cos2 = cos * cos;
        float cos5 = cos2 * cos2 * cos;

        Color ret = Color.white();
        Color r = d.copy().mul(refl);
        ret.sub(r);
        ret.mul(cos5);
        ret.add(r);
        return lr.add(ret.mul(state.traceReflection(refRay, 0)));
        //return lr.add(state.specularPhong(ret.mul(state.traceReflection(refRay, 0)), 5f, 5));
    }

    public void scatterPhoton(ShadingState state, Color power) {
        Color diffuse;
        // make sure we are on the right side of the material
        state.faceforward();
        diffuse = Shaders.getMixture(state, c, "plastic");
        state.storePhoton(state.getRay().getDirection(), power, diffuse);
        float d = diffuse.getAverage();
        float r = d * refl;
        double rnd = state.getRandom(0, 0, 1);
        if (rnd < d) {
            // photon is scattered
            power.mul(diffuse).mul(1.0f / d);
            OrthoNormalBasis onb = state.getBasis();
            double u = 2 * Math.PI * rnd / d;
            double v = state.getRandom(0, 1, 1);
            float s = (float) Math.sqrt(v);
            float s1 = (float) Math.sqrt(1.0 - v);
            Vector3 w = new Vector3((float) Math.cos(u) * s, (float) Math.sin(u) * s, s1);
            w = onb.transform(w, new Vector3());
            state.traceDiffusePhoton(new Ray(state.getPoint(), w), power);
        } else if (rnd < d + r) {
            float cos = -Vector3.dot(state.getNormal(), state.getRay().getDirection());
            power.mul(diffuse).mul(1.0f / d);
            // photon is reflected
            float dn = 2 * cos;
            Vector3 dir = new Vector3();
            dir.x = (dn * state.getNormal().x) + state.getRay().getDirection().x;
            dir.y = (dn * state.getNormal().y) + state.getRay().getDirection().y;
            dir.z = (dn * state.getNormal().z) + state.getRay().getDirection().z;
            state.traceReflectionPhoton(new Ray(state.getPoint(), dir), power);
        }
    }
}