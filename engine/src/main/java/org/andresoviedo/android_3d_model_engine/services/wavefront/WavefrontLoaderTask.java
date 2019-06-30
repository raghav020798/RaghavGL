package org.andresoviedo.android_3d_model_engine.services.wavefront;

import android.app.Activity;
import android.net.Uri;
import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Object3D;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.LoaderTask;
import org.andresoviedo.android_3d_model_engine.services.Object3DBuilder;
import org.andresoviedo.util.android.ContentUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class WavefrontLoaderTask extends LoaderTask {

    public WavefrontLoaderTask(final Activity parent, final List<Uri> uris, final Callback callback) {
        super(parent, uris, callback);
    }

    @Override
    protected List<Object3DData> build() throws IOException {

        InputStream params0 = ContentUtils.getInputStream(uris.get(0));
        WavefrontLoader wfl = new WavefrontLoader("");

        // allocate memory
        publishProgress(0);
        wfl.analyzeModel(params0);
        params0.close();

        // Allocate memory
        publishProgress(1);
        wfl.allocateBuffers();
        wfl.reportOnModel();

        // create the 3D object
        Object3DData data3D = new Object3DData(wfl.getVerts(), wfl.getNormals(), wfl.getTexCoords(), wfl.getFaces(),
                wfl.getFaceMats(), wfl.getMaterials());
        data3D.setId(uris.get(0).getPath());
        data3D.setUri(uris.get(0));
        data3D.setLoader(wfl);
        data3D.setDrawMode(GLES20.GL_TRIANGLES);
        data3D.setDimensions(data3D.getLoader().getDimensions());


//        sddddsaasscscs




        List<Object3DData> objects = new ArrayList<>();
        objects.add(data3D);


//        return Collections.singletonList(data3D);
        return objects;
    }

    @Override
    protected void build(List<Object3DData> datas) throws Exception {


        try {

            //gvhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
            for(int i=0; i<datas.size(); i++){

                InputStream stream = ContentUtils.getInputStream(uris.get(i));

                Object3DData data = datas.get(i);

                // parse model
                publishProgress(2);
                data.getLoader().loadModel(stream);
                stream.close();

                // scale object
                publishProgress(3);
                data.centerScale();
                data.setScale(new float[]{5, 5, 5});

                // draw triangles instead of points
                data.setDrawMode(GLES20.GL_TRIANGLES);

                // build 3D object buffers
                publishProgress(4);
                Object3DBuilder.generateArrays(data);
                publishProgress(5);

            }


        } catch (Exception e) {
            Log.e("Object3DBuilder", e.getMessage(), e);
            throw e;
        }
    }
}
